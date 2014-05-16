package org.biokoframework.system.services.cron.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.repository.memory.InMemoryRepository;
import org.biokoframework.system.services.cron.ICronListener;
import org.biokoframework.system.services.cron.ICronLocator;
import org.biokoframework.system.services.cron.ICronService;
import org.biokoframework.system.services.cron.mock.FailingCommand;
import org.biokoframework.system.services.cron.mock.MockListener;
import org.biokoframework.system.services.cron.mock.SuccessfulCommand;
import org.biokoframework.system.services.currenttime.CurrentTimeModule;
import org.biokoframework.system.services.email.EmailModule;
import org.biokoframework.system.services.email.IEmailService;
import org.biokoframework.system.services.entity.EntityModule;
import org.biokoframework.system.services.random.RandomModule;
import org.biokoframework.system.services.repository.RepositoryModule;
import org.biokoframework.system.services.templates.TemplatingModule;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class QuartzCronServiceTest {

    public static final String ADMIN_EMAIL = "notify-me@example.com";
    private QuartzCronService fService;
    private Injector fInjector;

    @BeforeClass
    public static void redirectLoggerToStdOut() {
        ConsoleAppender console = new ConsoleAppender(); //create appender
        //configure the appender
        String PATTERN = "%d [%p|%c|%C{1}] %m%n";
        console.setLayout(new PatternLayout(PATTERN));
        console.setThreshold(Level.ERROR);
        console.activateOptions();
        //add appender to any Logger (here is root)
        Logger.getRootLogger().addAppender(console);
    }

    @Before
    public void createService() {
        fInjector = Guice.createInjector(
                new EntityModule(),
                new EmailModule(ConfigurationEnum.DEV),
                new RandomModule(ConfigurationEnum.DEV),
                new TemplatingModule(ConfigurationEnum.DEV),
                new RepositoryModule(ConfigurationEnum.DEV) {
                    @Override
                    protected void configureForDev() {
                        bindRepositoryTo(InMemoryRepository.class);
                    }
                    @Override
                    protected void configureForDemo() {
                        configureForDev();
                    }
                    @Override
                    protected void configureForProd() {
                        configureForDemo();
                    }
                },
                new CurrentTimeModule(ConfigurationEnum.DEV),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bindConstant().annotatedWith(Names.named("noReplyEmailAddress")).to("no-reply@example.com");
                        bindConstant().annotatedWith(Names.named("cronEmailAddress")).to("cron@example.com");
                        bind(ICronLocator.class).toProvider(Providers.<ICronLocator>of(null));
                        bind(ICronService.class).toProvider(Providers.<ICronService>of(null));
                    }
                });
        IEmailService emailService = fInjector.getInstance(IEmailService.class);

        fService = new QuartzCronService(emailService, "cron@example.com", fInjector, null);

        Mailbox.clearAll();
    }

    @Test
    public void simpleSuccessfulExecution() throws Exception {
        SuccessfulCommand.EXECUTED = false;
        fService.schedule(SuccessfulCommand.class, "0/1 * * * * ?", ADMIN_EMAIL);

        Thread.sleep(2000);

        fService.stop();

        assertThat(SuccessfulCommand.EXECUTED, is(true));
        assertThat(Mailbox.get(ADMIN_EMAIL), is(empty()));
    }

    @Test
    public void successfulExecutionWithCustomListener() throws Exception {
        MockListener customListener = new MockListener();

        fService.schedule(SuccessfulCommand.class, "0/1 * * * * ?", ADMIN_EMAIL, Collections.<ICronListener>singletonList(customListener));

        Thread.sleep(2000);

        fService.stop();

        assertThat(customListener.getCommand(), is(Matchers.<Object>equalTo(SuccessfulCommand.class)));
        assertThat(Mailbox.get(ADMIN_EMAIL), is(empty()));
    }

    @Test
    public void simpleFailingExecution() throws Exception {
        fService.schedule(FailingCommand.class, "0/1 * * * * ?", ADMIN_EMAIL);

        Thread.sleep(2000);

        fService.stop();

        Mailbox mailbox = Mailbox.get(ADMIN_EMAIL);
        assertThat(mailbox, is(not(empty())));
        assertThat(mailbox.get(0).getSubject(), is("Bioko cron service - failure report"));
        assertThat((String) mailbox.get(0).getContent(), startsWith("Command " + FailingCommand.class.getName() + " failed"));
    }

    @Test
    public void failingExecutionWithCustomListener() throws Exception {
        MockListener customListener = new MockListener();

        fService.schedule(FailingCommand.class, "0/1 * * * * ?", ADMIN_EMAIL, Collections.<ICronListener>singletonList(customListener));

        Thread.sleep(2000);

        fService.stop();

        assertThat(customListener.getCommand(), is(Matchers.<Object>equalTo(FailingCommand.class)));

        Throwable cause = customListener.getCause();
        assertThat(cause, is(instanceOf(CommandException.class)));
        CommandException thrownException = (CommandException) cause;
        assertThat(thrownException.getErrors(), is(equalTo(CommandExceptionsFactory.createBadCommandInvocationException().getErrors())));

        Mailbox mailbox = Mailbox.get(ADMIN_EMAIL);
        assertThat(mailbox, is(not(empty())));
        assertThat(mailbox.get(0).getSubject(), is("Bioko cron service - failure report"));
        assertThat((String) mailbox.get(0).getContent(), startsWith("Command " + FailingCommand.class.getName() + " failed"));
    }

    @Test
    public void simpleNullableInjection() {
        fInjector.getInstance(QuartzCronService.class);
    }
}