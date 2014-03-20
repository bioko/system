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

import org.biokoframework.system.KILL_ME.exception.CommandNotFoundException;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.EntityNotFoundException;
import org.biokoframework.system.services.authentication.AuthenticationFailureException;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

import java.util.HashMap;
import java.util.List;

public class CommandExceptionsFactory {
	
	public static HashMap<String, String[]> sErrorMap = ErrorMessagesFactory.createMap();

	public static CommandException createBadCommandInvocationException() {
		String[] message = sErrorMap.get(FieldNames.BAD_COMMAND_INVOCATION_CODE);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.BAD_COMMAND_INVOCATION_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.toString());
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new BadCommandInvocationException(entity);
	}
	
	public static CommandException createCommandNotFoundException(String commandName) {
		String[] message = sErrorMap.get(FieldNames.COMMAND_NOT_FOUND_CODE);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.COMMAND_NOT_FOUND_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.append(commandName)
					.append(message[1])
					.toString());
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new CommandNotFoundException(entity);
	}
	
	public static CommandException createEntityNotFound(String entityName, String entityId) {
		String[] message = sErrorMap.get(FieldNames.ENTITY_WITH_ID_NOT_FOUND_CODE);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.ENTITY_WITH_ID_NOT_FOUND_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.append(entityName)
					.append(message[1])
					.append(entityId)
					.append(message[2])
					.toString());
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new EntityNotFoundException(entity);
	}
	
	public static CommandException createEntityNotFound(String entityName, String fieldName, String fieldValue) {
		String[] message = sErrorMap.get(FieldNames.ENTITY_WITH_FIELD_NOT_FOUND_CODE);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.ENTITY_WITH_FIELD_NOT_FOUND_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.append(entityName)
					.append(message[1])
					.append(fieldName)
					.append(message[2])
					.append(fieldValue)
					.append(message[3])
					.toString());
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new EntityNotFoundException(entity);
	}
	
	public static CommandException createExpectedFieldNotFound(String fieldName) {
		String[] message = sErrorMap.get(FieldNames.EXPECTED_FIELD_NOT_FOUND);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.EXPECTED_FIELD_NOT_FOUND,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.append(fieldName)
					.append(message[1])
					.toString());
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new BadCommandInvocationException(entity);
	}
	
	public static CommandException createNotCompleteEntity(String entityName) {
		String[] message = sErrorMap.get(FieldNames.ENTITY_NOT_COMPLETE_CODE);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.ENTITY_NOT_COMPLETE_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.append(entityName)
					.append(message[1])
					.toString());
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new BadCommandInvocationException(entity);
	}
	
	public static CommandException createAlreadyExistingEntity(String existingEntityReport) {
		String[] message = sErrorMap.get(FieldNames.ENTITY_ALREADY_EXISTING_CODE);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.ENTITY_ALREADY_EXISTING_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.append(existingEntityReport)
					.append(message[1])
					.toString());
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new BadCommandInvocationException(entity);
	}

	public static CommandException repositoryEmpty(String entityName) {
		String[] message = sErrorMap.get(FieldNames.REPOSITORY_IS_EMPTY_CODE);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.REPOSITORY_IS_EMPTY_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.append(entityName)
					.append(message[1])
					.toString());
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new EntityNotFoundException(entity);
	}
	
	public static AuthenticationFailureException createInvalidLoginException() {
		String[] message = sErrorMap.get(FieldNames.INVALID_LOGIN_CODE);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.INVALID_LOGIN_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.toString());
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new AuthenticationFailureException(entity);
	}
	
	public static AuthenticationFailureException createTokenNotFoundException() {
		String[] message = sErrorMap.get(FieldNames.AUTHENTICATION_REQUIRED_CODE);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.AUTHENTICATION_REQUIRED_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.toString());

		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new AuthenticationFailureException(entity);
	}

	public static AuthenticationFailureException createUnauthorisedAccessException() {
		String[] message = sErrorMap.get(FieldNames.AUTHENTICATION_REQUIRED_CODE);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.AUTHENTICATION_REQUIRED_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.toString());
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new AuthenticationFailureException(entity);
	}
	
	
	public static AuthenticationFailureException createInsufficientPrivilegesException() {
		String[] message = sErrorMap.get(FieldNames.INSUFFICIENT_PRIVILEGES_CODE);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.INSUFFICIENT_PRIVILEGES_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.toString());
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new AuthenticationFailureException(entity);
	}
	
	
	
	public static AuthenticationFailureException createTokenExpiredException() {
		String[] message = sErrorMap.get(FieldNames.TOKEN_EXPIRED_CODE);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.TOKEN_EXPIRED_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.toString());
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new AuthenticationFailureException(entity);
	}
	
	public static CommandException createContainerException(Exception exception) {
		return new CommandException(exception);
	}

	public static CommandException createEasterEggException(String message) {
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.NO_ERROR_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message).toString());
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new EasterEggException(entity);
	}

	public static CommandException createRepositoryMissingForResolutionException(String simpleName) {
		String[] message = sErrorMap.get(FieldNames.REPOSITORY_MISSING_CODE);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.REPOSITORY_MISSING_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.append(simpleName)
					.append(message[1]).toString());
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new BadCommandInvocationException(entity);
	}

	public static CommandException createDissolutionIncompleteException(List<String> entityFieldNames) {
		String[] message = sErrorMap.get(FieldNames.DISSOLUTION_INCOMPLETE_CODE);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.DISSOLUTION_INCOMPLETE_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.append(entityFieldNames)
					.append(message[1]).toString());
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new BadCommandInvocationException(entity);
	}

	public static AuthenticationFailureException createFacebookAuthenticationFailure(String facebookErrorType) {
		String[] message = sErrorMap.get(FieldNames.FACEBOOK_AUTH_FAILURE_CODE);
		Fields fields = new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.FACEBOOK_AUTH_FAILURE_CODE,
				ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.append(facebookErrorType).toString());
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(fields);
		return new AuthenticationFailureException(entity);
	}

}
