package it.bioko.system.service.authentication;

import it.bioko.system.command.CommandException;
import it.bioko.utils.domain.ErrorEntity;

@SuppressWarnings("serial")
public class AuthenticationFailureException extends CommandException {

	public AuthenticationFailureException(ErrorEntity error) {
		super(error);
	}

}
