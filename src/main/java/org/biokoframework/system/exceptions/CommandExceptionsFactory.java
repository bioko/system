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

package org.biokoframework.system.exceptions;

import java.util.HashMap;
import java.util.List;

import org.biokoframework.system.KILL_ME.exception.CommandNotFoundException;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.EntityNotFoundException;
import org.biokoframework.system.service.authentication.AuthenticationFailureException;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

public class CommandExceptionsFactory {
	
	public static HashMap<String, String[]> _errorMap = ErrorMessagesFactory.createMap();

	public static CommandException createBadCommandInvocationException() {
		String[] message = _errorMap.get(FieldNames.BAD_COMMAND_INVOCATION_CODE);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.BAD_COMMAND_INVOCATION_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.toString());
		return new BadCommandInvocationException(new ErrorEntity(fields));
	}
	
	public static CommandException createCommandNotFoundException(String commandName) {
		String[] message = _errorMap.get(FieldNames.COMMAND_NOT_FOUND_CODE);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.COMMAND_NOT_FOUND_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.append(commandName)
				.append(message[1])
				.toString());
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.COMMAND_NOT_FOUND_CODE);
		
		return new CommandNotFoundException(new ErrorEntity(fields));
	}
	
	public static CommandException createEntityNotFound(String entityName, String entityId) {
		String[] message = _errorMap.get(FieldNames.ENTITY_WITH_ID_NOT_FOUND_CODE);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.ENTITY_WITH_ID_NOT_FOUND_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.append(entityName)
					.append(message[1])
					.append(entityId)
					.append(message[2])
					.toString());
		return new EntityNotFoundException(new ErrorEntity(fields));
	}
	
	public static CommandException createEntityNotFound(String entityName, String fieldName, String fieldValue) {
		String[] message = _errorMap.get(FieldNames.ENTITY_WITH_FIELD_NOT_FOUND_CODE);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.ENTITY_WITH_FIELD_NOT_FOUND_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.append(entityName)
					.append(message[1])
					.append(fieldName)
					.append(message[2])
					.append(fieldValue)
					.append(message[3])
					.toString());
		return new EntityNotFoundException(new ErrorEntity(fields));
	}
	
	public static CommandException createExpectedFieldNotFound(String fieldName) {
		String[] message = _errorMap.get(FieldNames.EXPECTED_FIELD_NOT_FOUND);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.EXPECTED_FIELD_NOT_FOUND);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.append(fieldName)
					.append(message[1])
					.toString());
		return new BadCommandInvocationException(new ErrorEntity(fields));
	}
	
	public static CommandException createNotCompleteEntity(String entityName) {
		String[] message = _errorMap.get(FieldNames.ENTITY_NOT_COMPLETE_CODE);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.ENTITY_NOT_COMPLETE_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.append(entityName)
				.append(message[1])
				.toString());
		return new BadCommandInvocationException(new ErrorEntity(fields));
	}
	
	public static CommandException createAlreadyExistingEntity(String existingEntityReport) {
		String[] message = _errorMap.get(FieldNames.ENTITY_ALREADY_EXISTING_CODE);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.ENTITY_ALREADY_EXISTING_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.append(existingEntityReport)
				.append(message[1])
				.toString());
		return new BadCommandInvocationException(new ErrorEntity(fields));
	}

	public static CommandException repositoryEmpty(String entityName) {
		String[] message = _errorMap.get(FieldNames.REPOSITORY_IS_EMPTY_CODE);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.REPOSITORY_IS_EMPTY_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.append(entityName)
				.append(message[1])
				.toString());
		return new EntityNotFoundException(new ErrorEntity(fields));
	}
	
	public static CommandException createInvalidLoginException() {
		String[] message = _errorMap.get(FieldNames.INVALID_LOGIN_CODE);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.INVALID_LOGIN_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
		.append(message[0])
		.toString());
		return new AuthenticationFailureException(new ErrorEntity(fields));
	}
	
	public static CommandException createTokenNotFoundException() {
		String[] message = _errorMap.get(FieldNames.AUTHENTICATION_REQUIRED_CODE);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.AUTHENTICATION_REQUIRED_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.toString());
		return new AuthenticationFailureException(new ErrorEntity(fields));
	}

	public static CommandException createUnauthorisedAccessException() {
		String[] message = _errorMap.get(FieldNames.AUTHENTICATION_REQUIRED_CODE);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.AUTHENTICATION_REQUIRED_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.toString());
		return new AuthenticationFailureException(new ErrorEntity(fields));
	}
	
	
	public static CommandException createInsufficientPrivilegesException() {
		String[] message = _errorMap.get(FieldNames.INSUFFICIENT_PRIVILEGES_CODE);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.INSUFFICIENT_PRIVILEGES_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.toString());
		return new AuthenticationFailureException(new ErrorEntity(fields));
	}
	
	
	
	public static CommandException createTokenExpiredException() {
		String[] message = _errorMap.get(FieldNames.TOKEN_EXPIRED_CODE);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.TOKEN_EXPIRED_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.toString());
		return new AuthenticationFailureException(new ErrorEntity(fields));
	}
	
	public static CommandException createContainerException(Exception exception) {
		return new CommandException(exception);
	}

	public static CommandException createEasterEggException(String message) {
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.NO_ERROR_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message).toString());
		return new EasterEggException(new ErrorEntity(fields));
	}

	public static CommandException createRepositoryMissingForResolutionException(String simpleName) {
		String[] message = _errorMap.get(FieldNames.REPOSITORY_MISSING_CODE);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.REPOSITORY_MISSING_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.append(simpleName)
				.append(message[1]).toString());
		return new BadCommandInvocationException(new ErrorEntity(fields));
	}

	public static CommandException createDissolutionIncompleteException(List<String> entityFieldNames) {
		String[] message = _errorMap.get(FieldNames.DISSOLUTION_INCOMPLETE_CODE);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.DISSOLUTION_INCOMPLETE_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.append(entityFieldNames)
				.append(message[1]).toString());
		return new BadCommandInvocationException(new ErrorEntity(fields));
	}

	public static CommandException createFacebookAuthenticationFailure(String facebookErrorType) {
		String[] message = _errorMap.get(FieldNames.FACEBOOK_AUTH_FAILURE_CODE);
		Fields fields = new Fields();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.FACEBOOK_AUTH_FAILURE_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.append(facebookErrorType).toString());
		return new AuthenticationFailureException(new ErrorEntity(fields));
	}

}
