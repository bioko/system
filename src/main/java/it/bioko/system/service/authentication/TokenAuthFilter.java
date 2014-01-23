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

package it.bioko.system.service.authentication;

import it.bioko.system.KILL_ME.commons.GenericConstants;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericRepositoryNames;
import it.bioko.system.command.AbstractFilter;
import it.bioko.system.command.CommandException;
import it.bioko.system.entity.authentication.Authentication;
import it.bioko.system.entity.authentication.AuthenticationManager;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.system.repository.core.Repository;
import it.bioko.system.repository.core.SafeRepositoryHelper;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;

public class TokenAuthFilter extends AbstractFilter {

	private boolean _checkAuth;	
	private Repository<Authentication> _authenticationRepository;
	private ArrayList<String> _commandRoles;
	
	private boolean _authenticated;
	private String _currentToken;
	private Authentication _currentAuthentication;

	public TokenAuthFilter(boolean checkAuth, String[] roles) {
		_checkAuth = checkAuth;
		_commandRoles = new ArrayList<String>();
		if (roles!=null) {
			for (String role: roles) {
				_commandRoles.add(role);
			}
		}
	}

	@Override
	public void onContextInitialized() {
		super.onContextInitialized();
		_authenticationRepository = _context.getRepository(GenericRepositoryNames.AUTHENTICATION_REPOSITORY);
	}

	@Override
	public void filterInput(Fields input) throws CommandException {

		if (_authenticationRepository == null)
			return;
		
		_authenticated = false;
		
		_currentToken = input.stringNamed(GenericFieldNames.AUTH_TOKEN);
		if (StringUtils.isEmpty(_currentToken)) {
			_currentToken = input.stringNamed(GenericFieldNames.TOKEN_HEADER);
		}
		
		ArrayList<Authentication> authentications = _authenticationRepository.getEntitiesByForeignKey(GenericFieldNames.AUTH_TOKEN, _currentToken);
		if (authentications.size() != 1) {
			if (_checkAuth) {
				throw CommandExceptionsFactory.createTokenNotFoundException();
			} 
		} else {
			_authenticated = true;
			_currentAuthentication = authentications.get(0);

			if (!_commandRoles.isEmpty()) {
				String userRolesAsString = StringUtils.defaultString(_currentAuthentication.get(Authentication.ROLES));

				LinkedList<String> userRoles = new LinkedList<String>(Arrays.asList(userRolesAsString.split(GenericConstants.USER_ROLE_SEPARATOR)));
				userRoles.retainAll(_commandRoles);
				if (userRoles.isEmpty()) {
					throw CommandExceptionsFactory.createInsufficientPrivilegesException();
				}
			}


			if (!AuthenticationManager.isExpired(_currentAuthentication)) {
				_authenticationRepository.delete(_currentAuthentication.getId());
				throw CommandExceptionsFactory.createTokenExpiredException();
			} else {
				AuthenticationManager.renewAuthentication(_context, _currentAuthentication);
				SafeRepositoryHelper.save(_authenticationRepository, _currentAuthentication, _context);

				input.put(GenericFieldNames.AUTH_LOGIN_ID, _currentAuthentication.get(GenericFieldNames.LOGIN_ID));
				
			}
		}
		
	}

	@Override
	public void filterOutput(Fields output) throws CommandException {
		if (_authenticationRepository == null) {
			return;
		}
		
		if (_authenticated) {
			output.put(GenericFieldNames.TOKEN_HEADER, _currentToken);
			output.put(GenericFieldNames.TOKEN_EXPIRE_HEADER, _currentAuthentication.get(GenericFieldNames.AUTH_TOKEN_EXPIRE));
		}
	}

}
