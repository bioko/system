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

package it.bioko.system.service.authentication.strategies;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericRepositoryNames;
import it.bioko.system.command.CommandException;
import it.bioko.system.context.Context;
import it.bioko.system.entity.login.Login;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.system.repository.core.Repository;
import it.bioko.utils.fields.Fields;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

public class BasicStrategy implements AuthenticationStrategy {
	
	private static final String BASIC_AUTH_START = "Basic ";

	private static final List<String> AUTH_FIELDS = 
			Arrays.asList(GenericFieldNames.BASIC_AUTHENTICATION);

	@Override
	public List<String> getAuthFields() {
		return AUTH_FIELDS;
	}

	@Override
	public Fields authenticate(Context context, Fields input, boolean failSilently) throws CommandException {
		Repository<Login> loginRepository = context.getRepository(GenericRepositoryNames.LOGIN_REPOSITORY);
		
		String encodedAuthentication = input.stringNamed(GenericFieldNames.BASIC_AUTHENTICATION);
		String decodedString = new String(Base64.decodeBase64(encodedAuthentication.substring(BASIC_AUTH_START.length())));

		String userName = decodedString.replaceFirst(":.*", "");
		String password = decodedString.replaceFirst(".*:", "");
		
		Login login = loginRepository.retrieveByForeignKey(GenericFieldNames.USER_EMAIL, userName);
		if (login == null) {
			if (failSilently) {
				return null;
			} else {
				throw CommandExceptionsFactory.createInvalidLoginException();
			}
		} else if (!login.get(GenericFieldNames.PASSWORD).equals(password)) {
			throw CommandExceptionsFactory.createInvalidLoginException();
		}
		
		return Fields.single(Login.class.getSimpleName(), login);
	}

	@Override
	public Login createNewLogin(Context context, Fields input) throws CommandException {	
		String userName = input.stringNamed(GenericFieldNames.USER_EMAIL);
		String password = input.stringNamed(GenericFieldNames.PASSWORD);
		
		Login login = new Login(Fields.empty());
		login.set(Login.USER_EMAIL, userName);
		login.set(Login.PASSWORD, password);
		
		return login;
	}
	
}
