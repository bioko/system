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

import org.biokoframework.utils.fields.FieldNames;

import java.util.HashMap;

public class ErrorMessagesFactory {

	public static HashMap<Long, String[]> createMap() {
		HashMap<Long, String[]> errorMap = new HashMap<Long, String[]>();
		
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
