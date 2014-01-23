package it.bioko.system.service.push;


public class NotificationFailureException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotificationFailureException(String message) {
		super(message);
	}

	public NotificationFailureException(Exception exception) {
		super(exception);
	}

}
