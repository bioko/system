package it.bioko.system.KILL_ME.exception;

import it.bioko.utils.domain.ErrorEntity;

import java.util.ArrayList;
import java.util.List;

public class SystemException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private Exception _exception;
	//private ErrorEntity _error;
	private List<ErrorEntity> _errors = new ArrayList<ErrorEntity>();
	
	public SystemException(ErrorEntity error) {
		super(error.toJSONString());
		_errors.add(error);
	}
	
	public SystemException(List<ErrorEntity> errors) {
		_errors = new ArrayList<ErrorEntity>(errors);
	}
	
	public SystemException(Exception exception) {
		super(exception);
		_exception = exception;
	}
	
	public SystemException(ErrorEntity error, Exception exception) {
		super(exception);
		_errors.add(error);
		_exception = exception;
	}
	
	public SystemException(List<ErrorEntity> errors, Exception exception) {
		super(exception);
		_errors = new ArrayList<ErrorEntity>(errors);		
		_exception = exception;
	}
	
	public List<ErrorEntity> getErrors() {
		return _errors;
	}
	
	public Exception exception() {
		return _exception;
	}

}
