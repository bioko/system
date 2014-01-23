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

package org.biokoframework.system.service.mail;

import javax.mail.internet.MimeMessage;

import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.Command;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.service.queue.Queue;
import org.biokoframework.utils.fields.Fields;

public class SendMailCommand extends Command {

	public static final String CONTENT = GenericFieldNames.CONTENT;
	public static final String TO = "to";
	public static final String FROM = "from";
	public static final String SUBJECT = "subject";
	

	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);

		Queue mailQueue = _context.get(input.stringNamed(GenericFieldNames.QUEUE_NAME));
		
		Fields mailFields;
		while((mailFields = mailQueue.popFields()) != null) {
			EmailFiller filler = new EmailFiller();
			
			String mailAddress = mailFields.stringNamed(TO);
			filler.addTo(mailAddress);
			filler.setFrom(mailFields.stringNamed(FROM));
			String content = mailFields.stringNamed(CONTENT);
			filler.setContent(content);
			filler.setSubject(mailFields.stringNamed(SUBJECT));
			
			EmailServiceImplementation dispatcher = EmailServiceImplementation.mailServer();
			MimeMessage message = dispatcher.newMessage();
			
			filler.fill(message);
			
			dispatcher.send(message);
			
		}
			
		logOutput();
		return Fields.empty();
	}

	@Override
	public String getName() {
		return SendMailCommand.class.getSimpleName();
	}

}