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

package org.biokoframework.system.services.email.impl;

import com.google.inject.Inject;
import org.biokoframework.system.services.cron.ICronService;
import org.biokoframework.system.services.email.EmailException;
import org.biokoframework.system.services.email.IEmailService;
import org.biokoframework.system.services.email.KILL_ME.EmailFiller;
import org.biokoframework.system.services.email.KILL_ME.EmailServiceImplementation;

import javax.annotation.Nullable;
import javax.mail.internet.MimeMessage;

public class EmailService implements IEmailService {

    private static final String MAIL_QUEUE = "mailQueue";

    private final ICronService fCronService;

    @Inject
	public EmailService(@Nullable ICronService cronService) throws EmailException {
        fCronService = cronService;
	}
	
	@Override
	public void send(String destinationAddress, String sourceAddress, String content, String subject) throws EmailException {
        if (fCronService != null) {
//    		Fields sendMailInputFields = new Fields();
//    		sendMailInputFields.put(SendMailCommand.FROM, sourceAddress);
//    		sendMailInputFields.put(SendMailCommand.TO, destinationAddress);
//    		sendMailInputFields.put(SendMailCommand.CONTENT, content);
//    		sendMailInputFields.put(SendMailCommand.SUBJECT, subject);
//
//    		fMailQueueService.pushFields(sendMailInputFields);
        } else {
            sendASAP(destinationAddress, sourceAddress, content, subject);
        }
	}

	@Override
	public void sendASAP(String destinationAddress, String sourceAddress, String content, String subject) throws EmailException {
		
		EmailFiller filler = new EmailFiller();
		filler.addTo(destinationAddress);
		filler.setFrom(sourceAddress);
		
		filler.setContent(content);
		filler.setSubject(subject);

		EmailServiceImplementation dispatcher = EmailServiceImplementation.mailServer();
		MimeMessage message = dispatcher.newMessage();
		
		filler.fill(message);
		
		dispatcher.send(message);
		
	}
	
}
