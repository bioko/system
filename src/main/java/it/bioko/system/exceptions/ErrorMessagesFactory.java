package it.bioko.system.exceptions;

import it.bioko.utils.fields.FieldNames;

import java.util.HashMap;

public class ErrorMessagesFactory {

	public static HashMap<String, String[]> createMap() {
		HashMap<String, String[]> errorMap = new HashMap<String, String[]>();
		
		errorMap.put(FieldNames.BAD_COMMAND_INVOCATION_CODE, new String[] { "Error in command invocation" });
		errorMap.put(FieldNames.COMMAND_NOT_FOUND_CODE, new String[] { "Command ", " not found."});
		
		errorMap.put(FieldNames.ENTITY_WITH_ID_NOT_FOUND_CODE, new String[] { "Entity ", " with id ", " not found." });
		errorMap.put(FieldNames.ENTITY_WITH_FIELD_NOT_FOUND_CODE, new String[] { "Entity ", " with ", " equal to ", " not found."});
		errorMap.put(FieldNames.EXPECTED_FIELD_NOT_FOUND, new String[] { "Field ", " is required."});
		errorMap.put(FieldNames.ENTITY_NOT_COMPLETE_CODE, new String[] { "Entity ", " is not complete."});
		errorMap.put(FieldNames.ENTITY_ALREADY_EXISTING_CODE, new String[] { "Entity ", " already exists."});
		errorMap.put(FieldNames.REPOSITORY_IS_EMPTY_CODE, new String[] { "Entity ", " repository is empty."});
		errorMap.put(FieldNames.REPOSITORY_MISSING_CODE, new String[] { "Repository for entity ", " not found."});
		
		errorMap.put(FieldNames.AUTHENTICATION_REQUIRED_CODE, new String[] { "Authentication is required to use this command" });
		errorMap.put(FieldNames.INVALID_LOGIN_CODE, new String[] { "Invalid login information" });
		errorMap.put(FieldNames.TOKEN_EXPIRED_CODE, new String[] { "Token is expired" });
		errorMap.put(FieldNames.INSUFFICIENT_PRIVILEGES_CODE, new String[] { "You have insufficient privileges to execute this command" });
		errorMap.put(FieldNames.FACEBOOK_AUTH_FAILURE_CODE, new String[] { "Facebook authentication check failed, facebook error code: " });
		
		errorMap.put(FieldNames.DISSOLUTION_INCOMPLETE_CODE, new String[] { "Entities ", " cannot be dissolved because of missing dependencies."});
		
		errorMap.put(FieldNames.CONTAINER_EXCEPTION_CODE, new String[] { "Captured ", " with message " });
		
		return errorMap;
	}

}
