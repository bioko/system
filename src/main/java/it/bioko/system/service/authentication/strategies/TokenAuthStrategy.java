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
import it.bioko.system.entity.authentication.Authentication;
import it.bioko.system.entity.authentication.AuthenticationManager;
import it.bioko.system.entity.login.Login;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.system.repository.core.Repository;
import it.bioko.system.repository.core.SafeRepositoryHelper;
import it.bioko.utils.fields.Fields;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class TokenAuthStrategy implements AuthenticationStrategy {

	@Override
	public List<String> getAuthFields() {
		return Arrays.asList(GenericFieldNames.AUTH_TOKEN);
	}

	@Override
	public Fields authenticate(Context context, Fields input, boolean failSilently) throws CommandException {
		Repository<Login> loginRepo = context.getRepository(GenericRepositoryNames.LOGIN_REPOSITORY);
		Repository<Authentication> authRepo = context.getRepository(GenericRepositoryNames.AUTHENTICATION_REPOSITORY);
		Fields authFields = Fields.empty();

		String token = input.stringNamed(GenericFieldNames.AUTH_TOKEN);
		if (StringUtils.isEmpty(token)) {
			token = input.stringNamed(GenericFieldNames.TOKEN_HEADER);
		}
		
		Authentication auth = authRepo.retrieveByForeignKey(Authentication.TOKEN, token);
		if (auth == null) {
			if (failSilently) {
				 return null;
			} else {
				throw CommandExceptionsFactory.createTokenNotFoundException();
			}
		} else if (!AuthenticationManager.isExpired(auth)) {
			authRepo.delete(auth.getId());
			throw CommandExceptionsFactory.createTokenExpiredException();
		} else {
			AuthenticationManager.renewAuthentication(context, auth);
			SafeRepositoryHelper.save(authRepo, auth, context);
			
			Login login = loginRepo.retrieve(auth.get(Authentication.LOGIN_ID));
			
			Fields outputFields = Fields.empty();
			outputFields.put(GenericFieldNames.TOKEN_HEADER, token);
			outputFields.put(GenericFieldNames.TOKEN_EXPIRE_HEADER, auth.get(Authentication.TOKEN_EXPIRE));
			authFields.put(Login.class.getSimpleName(), login);
			authFields.put(AuthenticationStrategy.OUTPUT_FIELDS, outputFields);
			return authFields;
		}		
	}

	@Override
	public Login createNewLogin(Context context, Fields input) throws CommandException {
		throw new UnsupportedOperationException();
	}

}
