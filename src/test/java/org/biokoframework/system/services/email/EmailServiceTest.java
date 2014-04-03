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

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.biokoframework.system.services.email.impl.EmailService;
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

    private IEmailService fEmailService;

    @Before
    public void createMailService() {
        Mailbox.clearAll();

        Injector injector = Guice.createInjector();

        fEmailService = injector.getInstance(EmailService.class);
    }

    @Test
    public void sendMailAsapTest() throws EmailException, MessagingException, IOException {
        fEmailService.sendASAP("target@example.it", "source@example.it", "This is the content", "Subject");

        Mailbox box = Mailbox.get("target@example.it");

        assertThat(box, is(Matchers.<Message>iterableWithSize(1)));

        Message message = box.get(0);
        assertThat(message.getFrom()[0].toString(), is(equalTo("source@example.it")));
        assertThat(message.getSubject(), is(equalTo("Subject")));
        assertThat(message.getContent(), is(Matchers.<Object>equalTo("This is the content")));
        assertThat(message.getContentType(), startsWith("text/html"));

    }

}
