package it.bioko.system.service.mail;

import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.command.CommandException;
import it.bioko.system.exceptions.CommandExceptionsFactory;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class EmailFiller {

	private Address _from;
	private List<Address> _to = new ArrayList<Address>();
	private List<Address> _cc = new ArrayList<Address>();
	private List<Address> _bcc = new ArrayList<Address>();
	private String _subject;
	private Object _content;
	//private String _contentType = "text/html; charset=\"" + Charset.defaultCharset() + "\"";
	private String _contentType = "text/html; charset=\"utf-8\"";

	public void fill(MimeMessage message) throws CommandException {
		try {
			message.setFrom(from());
			message.setRecipients(RecipientType.TO, to().toArray(new Address[0]));
			message.setRecipients(RecipientType.CC, cc().toArray(new Address[0]));
			message.setRecipients(RecipientType.BCC, bcc().toArray(new Address[0]));
			message.setSubject(subject());
			message.setContent(content(), contentType());
		} catch (MessagingException exception) {
			Loggers.engagedServer.error("Fill mime message", exception);
			throw CommandExceptionsFactory.createContainerException(exception);
		}
	}
	

	protected List<Address> to() {
		return _to;
	}
	public void addTo(String receiver) throws CommandException {
		try {
			_to.add(toInternetAddress(receiver));
		} catch (AddressException exception) {
			Loggers.engagedServer.error("Set address for mail message", exception);
			throw CommandExceptionsFactory.createContainerException(exception);
		}
	}
	public void addTos(List<String> receivers) throws CommandException {
		for (String aReceiver : receivers) {
			addTo(aReceiver);
		}
	}
	
	protected Address from() {
		return _from;
	}
	public void setFrom(String sender) throws CommandException {
		try {
			_from = toInternetAddress(sender);
		} catch (AddressException exception) {
			Loggers.engagedServer.error("Set address for mail message", exception);
			throw CommandExceptionsFactory.createContainerException(exception);
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
