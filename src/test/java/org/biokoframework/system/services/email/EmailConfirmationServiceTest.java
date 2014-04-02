/*
 * Copyright (c) 2014.
 * 	Mikol Faro		<mikol.faro@gmail.com>
 * 	Simone Mangano	 	<simone.mangano@ieee.org>
 * 	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
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
 */

package org.biokoframework.system.services.email;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.entity.authentication.EmailConfirmation;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.login.LoginBuilder;
import org.biokoframework.system.entity.template.Template;
import org.biokoframework.system.repository.memory.InMemoryRepository;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.email.impl.EmailConfirmationServiceImpl;
import org.biokoframework.system.services.email.impl.EmailService;
import org.biokoframework.system.services.entity.EntityModule;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.system.services.random.IRandomService;
import org.biokoframework.system.services.random.RandomModule;
import org.biokoframework.system.services.random.impl.TestRandomGeneratorService;
import org.biokoframework.system.services.repository.RepositoryModule;
import org.biokoframework.system.services.templates.ITemplatingService;
import org.biokoframework.system.services.templates.TemplatingException;
import org.biokoframework.system.services.templates.TemplatingModule;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.EntityBuilder;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.matcher.DomainEntityHas;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.RepositoryException;
import org.biokoframework.utils.validation.ValidationModule;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.biokoframework.utils.matcher.Matchers.has;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014 Apr 01
 */
public class EmailConfirmationServiceTest {

    private Repository<Login> fLoginRepo;
    private Repository<EmailConfirmation> fConfirmRepo;
    private Injector fInjector;
    private EmailConfirmationServiceImpl fService;
    private EntityBuilder<Login> fLoginBuilder;

    @Before
    public void prepareService() {
        fInjector = Guice.createInjector(
                new ValidationModule(),
                new EntityModule(),
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
                        configureForDev();
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(IEmailService.class).to(EmailService.class);
                    }
                }

        );

        IRepositoryService repos = fInjector.getInstance(IRepositoryService.class);
        fLoginRepo = repos.getRepository(Login.class);
        fConfirmRepo = repos.getRepository(EmailConfirmation.class);

        fService = new EmailConfirmationServiceImpl(repos, fInjector.getInstance(IEntityBuilderService.class),
                fInjector.getInstance(IRandomService.class), fInjector.getInstance(IEmailService.class),
                fInjector.getInstance(ITemplatingService.class), "no.reply@example.com");

        fLoginBuilder = fInjector.getInstance(LoginBuilder.class);
    }

    @Test
    public void emailSendingTest() throws Exception {
        Login login = fLoginBuilder.loadDefaultExample().build(false);
        login = fLoginRepo.save(login);

        Template template = fInjector.getInstance(Template.class);
        template.setAll(new Fields(
                Template.TITLE, "Email confirmation",
                Template.BODY, "<html><body><a href=\"${url}?token=${token}\">Click here</a>"));

        Map<String, Object> params = new HashMap<>();
        params.put("url", "http://confirm.example.it");

        fService.sendConfirmationEmail(login, template, params);

        Mailbox box =  Mailbox.get((String) login.get(Login.USER_EMAIL));
        assertThat(box, Matchers.hasSize(1));
        Message email = box.get(0);
        assertThat(email.getSubject(), is(equalTo("Email confirmation")));
        assertThat(email.getFrom()[0].toString(), is(equalTo("no.reply@example.com")));
        assertThat(email.getContentType(), startsWith("text/html"));
        assertThat((String) email.getContent(), is(equalTo("<html><body><a href=\"http://confirm.example.it?token=00000000-0000-0000-0000-000000000000\">Click here</a>")));

        EmailConfirmation confirm = fConfirmRepo.retrieveByForeignKey(EmailConfirmation.LOGIN_ID, login.getId());
        assertThat(confirm, has(EmailConfirmation.TOKEN, equalTo("00000000-0000-0000-0000-000000000000")));
        assertThat(confirm, has(EmailConfirmation.CONFIRMATION_TIMESTAMP, nullValue()));
        assertThat(confirm, has(EmailConfirmation.CONFIRMED, equalTo(false)));
    }

}
