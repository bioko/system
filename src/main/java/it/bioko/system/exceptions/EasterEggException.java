package it.bioko.system.exceptions;

import it.bioko.system.command.CommandException;
import it.bioko.utils.domain.ErrorEntity;

@SuppressWarnings("serial")
public class EasterEggException extends CommandException {

	public EasterEggException(ErrorEntity error) {
		super(error);
	}
}
