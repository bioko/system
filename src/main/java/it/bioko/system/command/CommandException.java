package it.bioko.system.command;

import it.bioko.system.KILL_ME.exception.SystemException;
import it.bioko.utils.domain.ErrorEntity;

import java.util.List;

@SuppressWarnings("serial")
public class CommandException extends SystemException {

	public CommandException(ErrorEntity error) {
		super(error);
	}
	
	public CommandException(List<ErrorEntity> errors) {
		super(errors);
	}
	
	public CommandException(Exception exception) {
		super(exception);
	}
	
	public CommandException(ErrorEntity error, Exception exception) {
		super(error, exception);
	}
	
	public CommandException(List<ErrorEntity> errors, Exception exception) {
		super(errors, exception);
	}
	
}