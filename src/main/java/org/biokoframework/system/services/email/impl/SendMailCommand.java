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
import org.apache.log4j.Logger;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.services.email.EmailException;
import org.biokoframework.system.services.email.IEmailService;
import org.biokoframework.system.services.queue.IQueueService;
import org.biokoframework.utils.fields.Fields;

public class SendMailCommand extends AbstractCommand {

	private static final Logger LOGGER = Logger.getLogger(SendMailCommand.class);

	public static final String CONTENT = GenericFieldNames.CONTENT;
	public static final String TO = "to";
	public static final String FROM = "from";
	public static final String SUBJECT = "subject";
    private static final String MAIL_QUEUE = "mailQueue";
    private final IQueueService fMailQueueService;
	private final IEmailService fEmailService;
	
	@Inject
	public SendMailCommand(IQueueService mailQueueService, IEmailService emailService) {
		fMailQueueService = mailQueueService;
		fEmailService = emailService;
	}

	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);

		Fields mailFields;
		while((mailFields = fMailQueueService.popFields(MAIL_QUEUE)) != null) {
			String destinationAddress = mailFields.get(TO);
			String sourceAddress = mailFields.get(FROM);
			
			String content = mailFields.get(CONTENT);
			String subject = mailFields.get(SUBJECT);

			try {
				// TODO use this
				fEmailService.sendASAP(destinationAddress, sourceAddress, content, subject);
			} catch (EmailException exception) {
                LOGGER.error("error while sending mail", exception);

				fMailQueueService.pushFields(MAIL_QUEUE, mailFields);
				
				throw new CommandException(exception);
			}
			
		}
			
		logOutput();
		return new Fields();
	}

}
