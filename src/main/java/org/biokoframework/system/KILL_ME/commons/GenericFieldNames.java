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

package org.biokoframework.system.KILL_ME.commons;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GenericFieldNames implements Serializable {

	public static final String INPUT = "input";
	public static final String OUTPUT = "output";
	public static final String DEFAULT = "default";
	public static final String HTTP_PARAMETER_TYPE = "httpParameterType";
	public static final String MINIMUM = "minimum";
	public static final String MAXIMUM = "maximum";
	public static final String COMMAND = "command";
	
	public static final String COMMANDS_LIST = "commandsList";
	public static final String REPOSITORY_REPORT = "repositoryReport";
	
	public static final String LOGIN_ID = "loginId";
	public static final String USER_EMAIL = "userEmail";
	public static final String PASSWORD = "password";
	
	public static final String NAME = "name";
	public static final String SURNAME = "surname";
	
	public static final String GENDER = "gender";
	
	public static final String CONTENT = "content";

	public static final String RESPONSE = "RESPONSE";
	public static final String RESPONSE_CONTENT_TYPE = "responseContentType";
	
	public static final String TOKEN_HEADER = "Engaged-Auth-Token";
	public static final String TOKEN_EXPIRE_HEADER = "Engaged-Auth-Token-Expire";
	public static final String BASIC_AUTHENTICATION_HEADER = "Authorization";
	public static final String AUTH_TOKEN = "authToken";
	public static final String AUTH_TOKEN_EXPIRE = "authTokenExpire";
	public static final String AUTH_LOGIN_ID = "authLoginId";
	public static final String BASIC_AUTHENTICATION = "basicAuthentication";
	public static final String AUTHENTICATION_ID = "authenticationId";
	public static final String FACEBOOK_TOKEN = "facebookToken";
	
	public static final String RESOLVE_ENTITIES = "resolveEntities";

	public static final String QUEUE_NAME = "queueName";

	public static final String CONTENT_LENGTH_HEADER = "Content-Length";

	public static final String NOT_EXPECTED_ID = "NOT_EXPECTED_ID";
	
}
