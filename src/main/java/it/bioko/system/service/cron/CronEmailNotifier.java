package it.bioko.system.service.cron;

import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.command.CommandException;
import it.bioko.system.service.mail.EmailFiller;
import it.bioko.system.service.mail.EmailServiceImplementation;
import it.bioko.utils.fields.Fields;

import java.io.PrintStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.output.WriterOutputStream;

public class CronEmailNotifier extends CronJobListener {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private final String _emailAddress;

	public CronEmailNotifier(String emailAddress) {
		_emailAddress = emailAddress;
	}

	@Override
	public void commandFailed(String commandName, Fields commandInput, Throwable cause) {
		
		try {
			StringWriter writer = new StringWriter();
			writer.write("Command " + commandName + " failed");
			if (cause.getMessage() != null) {
				writer.write(" with message " + cause.getMessage() + "\n");
			} else {
				writer.write("\n");
			}

			writer.write("Time stamp: " + DATE_FORMAT.format(new Date()) + "\n");
			
			writer.write("Stacktrace: ");
			cause.printStackTrace(new PrintStream(new WriterOutputStream(writer)));
			
			EmailFiller filler = new EmailFiller();
			filler.addTo(_emailAddress);
			filler.setFrom(GenericFieldValues.CRON_EMAIL);
			filler.setContent(writer.toString());
			filler.setSubject(GenericFieldValues.CRON_MAIL_SUBJECT);
			
			EmailServiceImplementation dispatcher = EmailServiceImplementation.mailServer();
			MimeMessage mimeMessage = dispatcher.newMessage();
			filler.fill(mimeMessage);
			
			dispatcher.send(mimeMessage);
			
		} catch (CommandException exception) {
			Loggers.jobs.error("Error while reporting error for cron", exception);
		}
	}
	
}
