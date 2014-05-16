/*
 * Copyright (c) 2014
 *	Mikol Faro			<mikol.faro@gmail.com>
 *	Simone Mangano		<simone.mangano@ieee.org>
 *	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */


package org.biokoframework.system.services.email;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.repository.memory.InMemoryRepository;
import org.biokoframework.system.services.cron.CronException;
import org.biokoframework.system.services.cron.ICronLocator;
import org.biokoframework.system.services.cron.ICronService;
import org.biokoframework.system.services.cron.dev.DevCronService;
import org.biokoframework.system.services.email.impl.EmailService;
import org.biokoframework.system.services.email.impl.SendMailCommand;
import org.biokoframework.system.services.entity.EntityModule;
import org.biokoframework.system.services.queue.IQueueService;
import org.biokoframework.system.services.queue.impl.SqlQueueService;
import org.biokoframework.system.services.repository.RepositoryModule;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-03-27
 */
public class EmailServiceTest {

    @Before
    public void createMailboxes() {
        Mailbox.clearAll();
    }

    @Test
    public void sendMailAsapTest() throws EmailException, MessagingException, IOException {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ICronService.class).toProvider(Providers.<ICronService>of(null));
            }
        });

        IEmailService emailService = injector.getInstance(EmailService.class);

        emailService.sendASAP("target@example.it", "source@example.it", "This is the content", "Subject");

        Mailbox box = Mailbox.get("target@example.it");

        assertThat(box, is(Matchers.<Message>iterableWithSize(1)));

        Message message = box.get(0);
        assertThat(message.getFrom()[0].toString(), is(equalTo("source@example.it")));
        assertThat(message.getSubject(), is(equalTo("Subject")));
        assertThat(message.getContent(), is(Matchers.<Object>equalTo("This is the content")));
        assertThat(message.getContentType(), startsWith("text/html"));

    }

    @Test
    public void sendMailScheduledTest() throws Exception {
        IEmailService emailService = prepareEmailServiceWithCron();

        emailService.send("target@example.it", "source@example.it", "This is the content", "Subject");

        Mailbox box = Mailbox.get("target@example.it");
        assertThat(box, is(Matchers.<Message>iterableWithSize(0)));

        DevCronService.INSTANCE.trigger(SendMailCommand.class);

        assertThat(box, is(Matchers.<Message>iterableWithSize(1)));

        Message message = box.get(0);
        assertThat(message.getFrom()[0].toString(), is(equalTo("source@example.it")));
        assertThat(message.getSubject(), is(equalTo("Subject")));
        assertThat(message.getContent(), is(Matchers.<Object>equalTo("This is the content")));
        assertThat(message.getContentType(), startsWith("text/html"));

    }

    private IEmailService prepareEmailServiceWithCron() throws CronException {
        Injector injector = Guice.createInjector(
                new EntityModule(),
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
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(IEmailService.class).to(EmailService.class);
                        bind(ICronLocator.class).toProvider(Providers.<ICronLocator>of(null));
                        bind(ICronService.class).to(DevCronService.class);
                        bind(String.class).annotatedWith(Names.named("cronEmailAddress")).toInstance("cron@drillo.eu");
                        bind(IQueueService.class).to(SqlQueueService.class);
                    }
                }
        );

        injector.getInstance(ICronService.class).schedule(SendMailCommand.class, "0/30 * * * * * ?", "root@drillo.eu");

        return injector.getInstance(EmailService.class);
    }

}
