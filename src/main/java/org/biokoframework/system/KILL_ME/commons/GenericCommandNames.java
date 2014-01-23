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

import org.biokoframework.system.entity.EntityClassNameTranslator;


public class GenericCommandNames {

	public static final String COMMAND_LIST = "command-list";
	public static final String COMMAND_INVOCATION_INFO = "command-invocation-info";
	
	public static final String composeRestCommandName(HttpMethod restOperation, String entityName) {
		return composeCommandName(restOperation.name(), EntityClassNameTranslator.toHyphened(entityName));
	}
	
	public static final String composeCommandName(String operationName, String entityName) {
		return new StringBuilder().append(operationName).append("_").append(entityName).toString();
	}
	
	public static final String retriveOperation(String commandName) {
		return commandName.split("_")[0];
	}
	
	public static final String retrieveEntityName(String commandName) {
		return commandName.split("_")[1];
	}

	
	public static final String CRUD_METHOD = "crudMethod";
	public static final String FULL_REGISTRATION = "full-registration";
	public static final String GET_COMMAND_LIST = "getCommandList";
	public static final String MASTER = "master";
	public static final String PRINT_REPOSITORY = "printRepository";
	public static final String PRINT_REPOSITORIES = "printRepositories";
	public static final String SEND_TEST_EMAIL = "sendTestEmail";
	
	public static final String ENGAGED_CHECK_IN = "engaged-check-in";
	public static final String BASIC_CHECK_IN = "check-in";
	public static final String AUTHENTICATED = "authenticated";
	
	public static final String RESOLVABLE = "resolvable";
	public static final String DISSOLVABLE = "dissolvable";
	public static final String UNIQUE_CHECKER = "unique-checker";
	public static final String LOGIN_UNIQUE_CHECK = "login-unique-check";
	
}
