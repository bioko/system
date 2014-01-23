package it.bioko.system.exceptions;

import it.bioko.system.command.CommandException;
import it.bioko.utils.domain.ErrorEntity;

@SuppressWarnings("serial")
@Deprecated
public class BadCommandInvocationException extends CommandException {

	public BadCommandInvocationException(ErrorEntity error) {
		super(error);
	}

}
