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

package org.biokoframework.system.services.email.KILL_ME;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.biokoframework.system.KILL_ME.commons.logger.Loggers;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.services.email.EmailException;

@Deprecated
public class EmailFiller {

	private Address _from;
	private List<Address> _to = new ArrayList<Address>();
	private List<Address> _cc = new ArrayList<Address>();
	private List<Address> _bcc = new ArrayList<Address>();
	private String _subject;
	private Object _content;
	//private String _contentType = "text/html; charset=\"" + Charset.defaultCharset() + "\"";
	private String _contentType = "text/html; charset=\"utf-8\"";

	public void fill(MimeMessage message) throws EmailException {
		try {
			message.setFrom(from());
			message.setRecipients(RecipientType.TO, to().toArray(new Address[0]));
			message.setRecipients(RecipientType.CC, cc().toArray(new Address[0]));
			message.setRecipients(RecipientType.BCC, bcc().toArray(new Address[0]));
			message.setSubject(subject());
			message.setContent(content(), contentType());
		} catch (MessagingException exception) {
			Loggers.engagedServer.error("Fill mime message", exception);
			throw new EmailException(exception);
		}
	}
	

	protected List<Address> to() {
		return _to;
	}
	public void addTo(String receiver) throws EmailException {
		try {
			_to.add(toInternetAddress(receiver));
		} catch (AddressException exception) {
			// TODO call logger 
			// Loggers.engagedServer.error("Set address for mail message", exception);
			
			throw new EmailException(exception);
		}
	}
	public void addTos(List<String> receivers) throws EmailException {
		for (String aReceiver : receivers) {
			addTo(aReceiver);
		}
	}
	
	protected Address from() {
		return _from;
	}
	public void setFrom(String sender) throws EmailException {
		try {
			_from = toInternetAddress(sender);
		} catch (AddressException exception) {
			Loggers.engagedServer.error("Set address for mail message", exception);
			throw new EmailException(exception);
		}
	}
	
	protected List<Address> cc() {
		return _cc;
	}
	public void addCC(String carbonCopyReceiver) throws CommandException {
		try {
			_cc.add(toInternetAddress(carbonCopyReceiver));
		} catch (AddressException exception) {
			Loggers.engagedServer.error("Set address for mail message", exception);
			throw CommandExceptionsFactory.createContainerException(exception);
		}
	}

	protected List<Address> bcc() {
		return _bcc;
	}
	public void addBCC(String blindCarbonCopyReceiver) throws CommandException {
		try {
			_bcc.add(toInternetAddress(blindCarbonCopyReceiver));
		} catch (AddressException exception) {
			Loggers.engagedServer.error("Set address for mail message", exception);
			throw CommandExceptionsFactory.createContainerException(exception);
		}
	}

	protected String subject() {
		return _subject;
	}
	public void setSubject(String subject) {
		_subject = subject;
	}

	protected String contentType() {
		return _contentType; 
	}
	public void setContentType(String contentType) {
		_contentType = contentType;
	}
	
	
	protected Object content() {
		return _content;
	}
	public void setContent(String content) {
		_content = content;
	}
	
	protected Address toInternetAddress(String addressString) throws AddressException {
		return new InternetAddress(addressString);
	}
}
