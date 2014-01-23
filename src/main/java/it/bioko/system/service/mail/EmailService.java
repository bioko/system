package it.bioko.system.service.mail;

import it.bioko.system.context.Context;
import it.bioko.system.service.queue.Queue;
import it.bioko.utils.fields.Fields;

public class EmailService {

	public static final String EMAIL_QUEUE = "mailQueue";
	private Context _context;

	public EmailService(Context context, String mailQueueRepositoryName) {
		_context = context;
	}
	
	public void sendMail(String destinationAddress, String sourceAddress, String content, String subject) {
		Fields sendMailInputFields = Fields.empty();
		sendMailInputFields.put(SendMailCommand.FROM, sourceAddress);
		sendMailInputFields.put(SendMailCommand.TO, destinationAddress);
		sendMailInputFields.put(SendMailCommand.CONTENT, content);
		sendMailInputFields.put(SendMailCommand.SUBJECT, subject);
		
		Queue mailQueue = _context.get(EMAIL_QUEUE);
		mailQueue.pushFields(sendMailInputFields);
	}
	
}
