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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.biokoframework.system.KILL_ME.commons.GenericConstants;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.GenericRepositoryNames;
import org.biokoframework.system.command.AbstractFilter;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

public class BasicAuthFilter extends AbstractFilter {
	
	private Repository<Login> _loginRepo;

	private static final String BASIC_AUTH_START = "Basic ";

	private boolean _checkAuth;
	private ArrayList<String> _commandRoles;

		
	public BasicAuthFilter(boolean checkAuth, String[] roles) {
		_checkAuth = checkAuth;
		_commandRoles = new ArrayList<String>();
		if (roles!=null) {
			for (String role: roles)
				_commandRoles.add(role);
		}
	}


	@Override
	public void onContextInitialized() {
		_loginRepo = _context.getRepository(GenericRepositoryNames.LOGIN_REPOSITORY);
	}

	public void filterInput(Fields input) throws CommandException {
		if(_loginRepo == null) {
			return;
		}
		
		String encodedAuthentication = input.get(GenericFieldNames.BASIC_AUTHENTICATION);
		String decodedString = new String(Base64.decodeBase64(encodedAuthentication.substring(BASIC_AUTH_START.length())));

		String userName = decodedString.replaceFirst(":.*", "");
		String password = decodedString.replaceFirst(".*:", "");
		
		Login login = _loginRepo.retrieveByForeignKey(GenericFieldNames.USER_EMAIL, userName);
		if (login == null || !login.get(GenericFieldNames.PASSWORD).equals(password)) {
			if (_checkAuth) {
				throw CommandExceptionsFactory.createInvalidLoginException();
			}
		} else {
			
			if (!_commandRoles.isEmpty()) {
				String userRolesAsString = StringUtils.defaultString(login.get(Login.ROLES).toString());
				
				LinkedList<String> userRoles = new LinkedList<String>(Arrays.asList(userRolesAsString.split(GenericConstants.USER_ROLE_SEPARATOR)));
				userRoles.retainAll(_commandRoles);
				if (userRoles.isEmpty()) {
					throw CommandExceptionsFactory.createInsufficientPrivilegesException();
				}
			}
			
			input.put(GenericFieldNames.AUTH_LOGIN_ID, login.getId());
		}
		
	}
	
}
