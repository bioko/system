package it.bioko.system.exceptions;

import it.bioko.utils.domain.ErrorEntity;


@SuppressWarnings("serial")
public class SystemNotFoundException extends Exception {
	
	private ErrorEntity _error;

	public SystemNotFoundException(ErrorEntity error) {
		_error = error;
	}
	
	public ErrorEntity error() {
		return _error;
	}
}
