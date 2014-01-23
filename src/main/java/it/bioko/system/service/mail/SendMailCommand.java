package it.bioko.system.service.mail;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.command.Command;
import it.bioko.system.command.CommandException;
import it.bioko.system.service.queue.Queue;
import it.bioko.utils.fields.Fields;

import javax.mail.internet.MimeMessage;

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
