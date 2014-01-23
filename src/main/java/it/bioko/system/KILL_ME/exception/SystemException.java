/*
 * Copyright (c) 2014																 
 *	Mikol Faro			<mikol.faro@gmail.com>
 *	Simone Mangano		<simone.mangano@ieee.org>
 *	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

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
