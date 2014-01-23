package it.bioko.system.KILL_ME;

@Deprecated
public class FieldsErrors {

	public static final String FAILURE = "FAILURE: ";
	public static final String ERROR = "ERROR: ";
	
	public static final String EMPTY_COMMAND = "Input is empty or has no command or wrong command.";
	public static final String ENTITY_ALREADY_EXISTS = "Entity already exists";
	

	public static String failureWithMessage(String message) {
		return new StringBuilder(FAILURE).append(message).toString();
	}
	
	public static String errorWithMessage(String message) {
		return new StringBuilder(ERROR).append(message).toString();
	}
}