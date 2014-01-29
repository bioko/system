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

package org.biokoframework.system.service.authentication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.biokoframework.system.KILL_ME.commons.GenericConstants;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractFilter;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.service.authentication.strategies.AuthenticationStrategy;
import org.biokoframework.system.service.authentication.strategies.AuthenticationStrategyFactory;
import org.biokoframework.utils.fields.Fields;

public class AuthFilter extends AbstractFilter {

	private boolean _checkAuth;
	private List<String> _commandRoles;
	private Fields _outputFields;

	public AuthFilter(boolean checkAuth, String[] roles) {
		_checkAuth = checkAuth;
		if (roles != null) {
			_commandRoles = Arrays.asList(roles);
		} else {
			_commandRoles = new ArrayList<String>();
		}
	}
	
	@Override
	public void filterInput(Fields input) throws CommandException {
		
		AuthenticationStrategy strategy = AuthenticationStrategyFactory.retrieveAuthenticationStrategy(input);
		if (strategy == null) {
			if (_checkAuth) {
				throw CommandExceptionsFactory.createUnauthorisedAccessException();
			} else {
				return;
			}
		}
		
		Fields authenticationFields = strategy.authenticate(_context, input, !_checkAuth);
		if (authenticationFields != null) {
			Login login = authenticationFields.get(Login.class.getSimpleName());
			if (!_commandRoles.isEmpty()) {
				String userRolesAsString = StringUtils.defaultString(login.get(Login.ROLES).toString());

				LinkedList<String> userRoles = new LinkedList<String>(Arrays.asList(userRolesAsString.split(GenericConstants.USER_ROLE_SEPARATOR)));
				userRoles.retainAll(_commandRoles);
				if (userRoles.isEmpty()) {
					throw CommandExceptionsFactory.createInsufficientPrivilegesException();
				}
			}
			input.put(GenericFieldNames.AUTH_LOGIN_ID, login.getId());
			
			_outputFields = authenticationFields.get(AuthenticationStrategy.OUTPUT_FIELDS);
		}
	}

	@Override
	public void filterOutput(Fields output) throws CommandException {
		if (_outputFields != null) {
			output.putAll(_outputFields);
			_outputFields = null;
		}
	}
	
}
