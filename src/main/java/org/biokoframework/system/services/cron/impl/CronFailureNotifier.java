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

package org.biokoframework.system.services.cron.impl;

import java.io.PrintStream;
import java.io.StringWriter;

import org.apache.commons.io.output.WriterOutputStream;
import org.biokoframework.system.command.ICommand;
import org.biokoframework.system.services.cron.ICronListener;
import org.biokoframework.system.services.email.EmailException;
import org.biokoframework.system.services.email.IEmailService;
import org.joda.time.DateTime;

public class CronFailureNotifier implements ICronListener {

	private static final String SCHEDULED_ERROR_SUBJECT = "Bioko cron service - failure report";
	
	private final IEmailService fMailService;
	
	private final String fSourceEmailAddress;
	private final String fDestinationEmailAddress;

	public CronFailureNotifier(IEmailService mailService, String sourceMailAddress, String destinationMailAddress) {
		fMailService = mailService;

		fSourceEmailAddress = sourceMailAddress;
		fDestinationEmailAddress = destinationMailAddress;
	}
	
	@Override
	public <C extends ICommand> void commandFinished(Class<C> command) {}
	
	@Override
	public <C extends ICommand> void commandFailed(Class<C> command, Throwable cause) {
		try {
			StringWriter writer = new StringWriter();
			writer.write("Command " + command.getName() + " failed");
			if (cause.getMessage() != null) {
			writer.write(" with message " + cause.getMessage() + "\n");
			} else {
			writer.write("\n");
			}

			writer.write("Time stamp: " + DateTime.now() + "\n");

			writer.write("Stacktrace: ");
			cause.printStackTrace(new PrintStream(new WriterOutputStream(writer)));

			fMailService.sendASAP(fDestinationEmailAddress, fSourceEmailAddress, writer.toString(), SCHEDULED_ERROR_SUBJECT);
			
		} catch (EmailException exception) {
			// The system failed at failing (looks like Windows) 
			
			// TODO log error
			// Loggers.jobs.error("Error while reporting error for cron", exception);
		}
		

	}

}
