package it.bioko.system.KILL_ME.exception;

import it.bioko.system.command.CommandException;
import it.bioko.utils.domain.ErrorEntity;

public class CommandNotFoundException extends CommandException {

	private static final long serialVersionUID = 1L;

	public CommandNotFoundException(ErrorEntity errorEntity) {
		super(errorEntity);
	}

}
