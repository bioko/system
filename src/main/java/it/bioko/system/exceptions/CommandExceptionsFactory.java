package it.bioko.system.exceptions;

import it.bioko.system.KILL_ME.exception.CommandNotFoundException;
import it.bioko.system.command.CommandException;
import it.bioko.system.entity.EntityNotFoundException;
import it.bioko.system.service.authentication.AuthenticationFailureException;
import it.bioko.utils.domain.ErrorEntity;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;

import java.util.HashMap;
import java.util.List;

public class CommandExceptionsFactory {
	
	public static HashMap<String, String[]> _errorMap = ErrorMessagesFactory.createMap();

	public static CommandException createBadCommandInvocationException() {
		String[] message = _errorMap.get(FieldNames.BAD_COMMAND_INVOCATION_CODE);
		Fields fields = Fields.empty();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.BAD_COMMAND_INVOCATION_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
					.append(message[0])
					.toString());
		return new BadCommandInvocationException(new ErrorEntity(fields));
	}
	
	public static CommandException createCommandNotFoundException(String commandName) {
		String[] message = _errorMap.get(FieldNames.COMMAND_NOT_FOUND_CODE);
		Fields fields = Fields.empty();
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
		Fields fields = Fields.empty();
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
		Fields fields = Fields.empty();
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
		Fields fields = Fields.empty();
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
		Fields fields = Fields.empty();
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
		Fields fields = Fields.empty();
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
		Fields fields = Fields.empty();
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
		Fields fields = Fields.empty();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.INVALID_LOGIN_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
		.append(message[0])
		.toString());
		return new AuthenticationFailureException(new ErrorEntity(fields));
	}
	
	public static CommandException createTokenNotFoundException() {
		String[] message = _errorMap.get(FieldNames.AUTHENTICATION_REQUIRED_CODE);
		Fields fields = Fields.empty();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.AUTHENTICATION_REQUIRED_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.toString());
		return new AuthenticationFailureException(new ErrorEntity(fields));
	}

	public static CommandException createUnauthorisedAccessException() {
		String[] message = _errorMap.get(FieldNames.AUTHENTICATION_REQUIRED_CODE);
		Fields fields = Fields.empty();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.AUTHENTICATION_REQUIRED_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.toString());
		return new AuthenticationFailureException(new ErrorEntity(fields));
	}
	
	
	public static CommandException createInsufficientPrivilegesException() {
		String[] message = _errorMap.get(FieldNames.INSUFFICIENT_PRIVILEGES_CODE);
		Fields fields = Fields.empty();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.INSUFFICIENT_PRIVILEGES_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.toString());
		return new AuthenticationFailureException(new ErrorEntity(fields));
	}
	
	
	
	public static CommandException createTokenExpiredException() {
		String[] message = _errorMap.get(FieldNames.TOKEN_EXPIRED_CODE);
		Fields fields = Fields.empty();
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
		Fields fields = Fields.empty();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.NO_ERROR_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message).toString());
		return new EasterEggException(new ErrorEntity(fields));
	}

	public static CommandException createRepositoryMissingForResolutionException(String simpleName) {
		String[] message = _errorMap.get(FieldNames.REPOSITORY_MISSING_CODE);
		Fields fields = Fields.empty();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.REPOSITORY_MISSING_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.append(simpleName)
				.append(message[1]).toString());
		return new BadCommandInvocationException(new ErrorEntity(fields));
	}

	public static CommandException createDissolutionIncompleteException(List<String> entityFieldNames) {
		String[] message = _errorMap.get(FieldNames.DISSOLUTION_INCOMPLETE_CODE);
		Fields fields = Fields.empty();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.DISSOLUTION_INCOMPLETE_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.append(entityFieldNames)
				.append(message[1]).toString());
		return new BadCommandInvocationException(new ErrorEntity(fields));
	}

	public static CommandException createFacebookAuthenticationFailure(String facebookErrorType) {
		String[] message = _errorMap.get(FieldNames.FACEBOOK_AUTH_FAILURE_CODE);
		Fields fields = Fields.empty();
		fields.put(ErrorEntity.ERROR_CODE, FieldNames.FACEBOOK_AUTH_FAILURE_CODE);
		fields.put(ErrorEntity.ERROR_MESSAGE, new StringBuilder()
				.append(message[0])
				.append(facebookErrorType).toString());
		return new AuthenticationFailureException(new ErrorEntity(fields));
	}

}
