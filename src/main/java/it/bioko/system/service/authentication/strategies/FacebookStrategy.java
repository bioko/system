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

import it.bioko.system.KILL_ME.commons.GenericConstants;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericRepositoryNames;
import it.bioko.system.command.CommandException;
import it.bioko.system.context.Context;
import it.bioko.system.entity.login.Login;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.system.repository.core.Repository;
import it.bioko.system.service.random.RandomGeneratorService;
import it.bioko.utils.fields.Fields;

import java.util.Arrays;
import java.util.List;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookGraphException;
import com.restfb.types.User;

public class FacebookStrategy implements AuthenticationStrategy {

	public static final List<String> AUTHENTICATON_FIELDS = 
			Arrays.asList(GenericFieldNames.FACEBOOK_TOKEN);
	
	@Override
	public List<String> getAuthFields() {
		return AUTHENTICATON_FIELDS;
	}

	@Override
	public Fields authenticate(Context context, Fields input, boolean failSilently) throws CommandException {
		Repository<Login> loginRepository = context.getRepository(GenericRepositoryNames.LOGIN_REPOSITORY);
		
		String fbToken = input.stringNamed(GenericFieldNames.FACEBOOK_TOKEN);
		String fbId = retrieveFBUser(fbToken).getId();
		
		Login login = loginRepository.retrieveByForeignKey(Login.FACEBOOK_ID, fbId);
		if (login == null) {
			if (failSilently) {
				return null;
			} else {
				throw CommandExceptionsFactory.createInvalidLoginException();
			}
		}
		
		return Fields.single(Login.class.getSimpleName(), login);
	}

	private User retrieveFBUser(String token) throws CommandException {
		FacebookClient facebookClient = new DefaultFacebookClient(token);
		try {
			User user = facebookClient.fetchObject("me", User.class);
			return user;		
		} catch (FacebookGraphException exception) {
			throw CommandExceptionsFactory.createFacebookAuthenticationFailure(exception.getErrorType());
		}
	}

	@Override
	public Login createNewLogin(Context context, Fields input) throws CommandException {
		String fbToken = input.stringNamed(GenericFieldNames.FACEBOOK_TOKEN);
		User fbUser = retrieveFBUser(fbToken);
		
		Login login = new Login(input);
		login.set(Login.FACEBOOK_ID, fbUser.getId());
		if (login.get(Login.USER_EMAIL) == null) {
			login.set(Login.USER_EMAIL, fbUser.getEmail());
		}
		
		if (login.get(Login.PASSWORD) == null) {
			// TODO usare sistema random mockabile di Simone
			login.set(Login.PASSWORD, createPassword(context));
		}
			
		return login;
	}

	private String createPassword(Context context) {
		RandomGeneratorService randomGeneratorService = (RandomGeneratorService) context.get(GenericConstants.RANDOM_PASSWORD_GENERATOR);
		
		String passwordLength = context.getSystemProperty(GenericConstants.PASSWORD_LENGTH);
		if (passwordLength == null) {
			return randomGeneratorService.generateString(GenericFieldNames.PASSWORD, 
					GenericConstants.DEFAULT_PASSWORD_LENGTH);
		} else {
			return randomGeneratorService.generateString(GenericFieldNames.PASSWORD, 
					Integer.parseInt(passwordLength));
		}
	}
	
}
