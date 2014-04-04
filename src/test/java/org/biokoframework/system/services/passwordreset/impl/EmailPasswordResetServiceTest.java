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

package org.biokoframework.system.services.passwordreset.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.entity.authentication.PasswordReset;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.login.LoginBuilder;
import org.biokoframework.system.entity.template.Template;
import org.biokoframework.system.repository.memory.InMemoryRepository;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.currenttime.CurrentTimeModule;
import org.biokoframework.system.services.currenttime.ICurrentTimeService;
import org.biokoframework.system.services.currenttime.impl.TestCurrentTimeService;
import org.biokoframework.system.services.email.EmailModule;
import org.biokoframework.system.services.email.IEmailService;
import org.biokoframework.system.services.entity.EntityModule;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.system.services.random.IRandomService;
import org.biokoframework.system.services.random.RandomModule;
import org.biokoframework.system.services.repository.RepositoryModule;
import org.biokoframework.system.services.templates.ITemplatingService;
import org.biokoframework.system.services.templates.TemplatingModule;
import org.biokoframework.utils.domain.EntityBuilder;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.validation.ValidationModule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;

import javax.mail.Message;
import java.util.HashMap;
import java.util.Map;

import static com.google.inject.name.Names.named;
import static org.biokoframework.utils.matcher.Matchers.has;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014 Apr 03
 */
public class EmailPasswordResetServiceTest {

    private static final String NO_REPLY_EMAIL_ADDRESS = "no-reply@example.com";

    private Injector fInjector;

    private Repository<Login> fLoginRepo;
    private Repository<PasswordReset> fPasswordResetRepo;

    private EntityBuilder<Login> fLoginBuilder;
    private EmailPasswordResetService fPasswordResetService;

    @Before
    public void createInjector() {
        fInjector = Guice.createInjector(
                new EntityModule(),
                new ValidationModule(),
                new CurrentTimeModule(ConfigurationEnum.DEV),
                new RandomModule(ConfigurationEnum.DEV),
                new EmailModule(ConfigurationEnum.DEV),
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
                        bindConstant().annotatedWith(named("noReplyEmailAddress")).to(NO_REPLY_EMAIL_ADDRESS);
                    }
                });

        IRepositoryService repos = fInjector.getInstance(IRepositoryService.class);
        fLoginRepo = repos.getRepository(Login.class);
        fPasswordResetRepo = repos.getRepository(PasswordReset.class);
        fLoginBuilder = fInjector.getInstance(LoginBuilder.class);

        Mailbox.clearAll();

        fPasswordResetService = new EmailPasswordResetService(repos, fInjector.getInstance(IEntityBuilderService.class),
                fInjector.getInstance(ICurrentTimeService.class), fInjector.getInstance(IRandomService.class),
                fInjector.getInstance(IEmailService.class), fInjector.getInstance(ITemplatingService.class), NO_REPLY_EMAIL_ADDRESS);
    }

    @Test
    public void simpleTest() throws Exception {
        String now = "2014-01-12T01:45:23+0100";
        String tomorrow = "2014-01-13T01:45:23+0100";
        TestCurrentTimeService.setCalendar(now);

        Login login = fLoginBuilder.loadDefaultExample().build(false);
        login = fLoginRepo.save(login);

        String userEmail = login.get(Login.USER_EMAIL);

        Template template = fInjector.getInstance(Template.class);
        template.setAll(new Fields(
                Template.BODY, "<a href=\"${url}?token=${token}\">Click me</a>",
                Template.TITLE, "Reset password"));

        Map<String, Object> content = new HashMap<>();
        content.put("url", "http://example.com/reset-password");

        fPasswordResetService.requestPasswordReset(userEmail, template, content);

        PasswordReset reset = fPasswordResetRepo.retrieveByForeignKey(PasswordReset.LOGIN_ID, login.getId());
        assertThat(reset, has(PasswordReset.TOKEN, equalTo("00000000-0000-0000-0000-000000000000")));
        assertThat(reset, has(PasswordReset.TOKEN_EXPIRATION, equalTo(tomorrow)));

        Mailbox mailbox = Mailbox.get(userEmail);
        assertThat(mailbox, hasSize(equalTo(1)));
        Message message = mailbox.get(0);
        assertThat(message.getFrom()[0].toString(), is(equalTo(NO_REPLY_EMAIL_ADDRESS)));
        assertThat(message.getSubject(), is(equalTo("Reset password")));
        assertThat((String) message.getContent(), is(equalTo("<a href=\"http://example.com/reset-password?token=00000000-0000-0000-0000-000000000000\">Click me</a>")));
    }

    @Ignore("not yet implemented")
    @Test
    public void resetPasswordOfUnexistingAccount() {

    }

}
