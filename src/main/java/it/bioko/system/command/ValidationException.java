package it.bioko.system.command;

import it.bioko.system.KILL_ME.exception.SystemException;
import it.bioko.utils.domain.ErrorEntity;

import java.util.List;

public class ValidationException extends SystemException {

	private static final long serialVersionUID = -3444520423474495086L;

	public ValidationException(ErrorEntity error) {
		super(error);
	}
	
	public ValidationException(List<ErrorEntity> errors) {
		super(errors);
	}

}
