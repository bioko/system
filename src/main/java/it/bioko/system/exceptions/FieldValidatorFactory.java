package it.bioko.system.exceptions;

import it.bioko.system.command.CommandException;
import it.bioko.utils.fields.Fields;

public class FieldValidatorFactory {

	public static void fieldsContainsKey(Fields fields, String keyName) throws CommandException {
		if (fields.valueFor(keyName) == null) {
			throw CommandExceptionsFactory.createExpectedFieldNotFound(keyName);
		}
	}
	
}
