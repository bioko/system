package it.bioko.system.repository.core;

import it.bioko.system.KILL_ME.exception.SystemException;
import it.bioko.utils.domain.ErrorEntity;

public class RepositoryException extends SystemException {

	private static final long serialVersionUID = 1L;

	public RepositoryException(ErrorEntity error) {
		super(error);
	}

	public RepositoryException(Exception exception) {
		super(exception);
	}


}
