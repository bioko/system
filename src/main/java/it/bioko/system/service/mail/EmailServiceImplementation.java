package it.bioko.system.service.mail;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

// exim installation
// http://www.humans-enabled.com/2012/04/easy-mail-by-smarthost-smtp-server-in.html
// sudo apt-get install exim4-daemon-light
// sudo dpkg-reconfigure exim4-config
// exim usage
// http://bradthemad.org/tech/notes/exim_cheatsheet.php

public class EmailServiceImplementation {
     
	public static EmailServiceImplementation mailServer() {
		return localhostMailServer();
	}
	
    private static EmailServiceImplementation localhostMailServer() {
        return new EmailServiceImplementation("localhost", "25");
    }

    private Session _session;

    public EmailServiceImplementation(String host, String port) {
        java.util.Properties props = new java.util.Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        
        _session =  Session.getInstance(props);
    }

    public MimeMessage newMessage() {
        return new MimeMessage(_session);
    }
        
    public boolean send(MimeMessage aMessage) {
    	try {
    		Transport.send(aMessage);
    		return true;
    	} catch (MessagingException ignored) {
    		ignored.printStackTrace();
    		return false;
    	}
    }
}