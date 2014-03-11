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

package org.biokoframework.system.services.authentication.token.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.entity.authentication.Authentication;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.authentication.AuthenticationFailureException;
import org.biokoframework.system.services.authentication.annotation.Auth;
import org.biokoframework.system.services.authentication.token.ITokenAuthenticationService;
import org.biokoframework.system.services.currenttime.ICurrentTimeService;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.system.services.random.IRandomService;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.RepositoryException;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Mar 6, 2014
 *
 */
public class TokenAuthenticationServiceImpl implements ITokenAuthenticationService {

    private static final Logger LOGGER = Logger.getLogger(TokenAuthenticationServiceImpl.class);
    public static final String AUTH_TOKEN = "authToken";

    private final IEntityBuilderService fEntitiesBuilder;
	private final Repository<Login> fLoginRepo;
	private final Repository<Authentication> fAuthRepo;
	private final IRandomService fRandom;
	private final ICurrentTimeService fTime;
	private final long fValidityIntervalSecs;

	@Inject
	public TokenAuthenticationServiceImpl(IEntityBuilderService entityBuilderService, IRepositoryService repoService, 
			IRandomService randomService, ICurrentTimeService currentTimeService, @Named("tokenValiditySecs") long validityIntervalSecs) {
		
		fEntitiesBuilder = entityBuilderService;
		fLoginRepo = repoService.getRepository(Login.class);
		fAuthRepo = repoService.getRepository(Authentication.class);
		fRandom = randomService;
		fTime = currentTimeService;
		fValidityIntervalSecs = validityIntervalSecs;
	}
	
	@Override
	public Fields authenticate(Fields fields, List<String> requiredRoles) throws AuthenticationFailureException {
		Authentication auth = fAuthRepo.retrieveByForeignKey(Authentication.TOKEN, (String) fields.get(AUTH_TOKEN));
		if (auth == null) {
			throw CommandExceptionsFactory.createUnauthorisedAccessException();
        } else if (isExpired(auth)) {
            fAuthRepo.delete(auth.getId());
            throw CommandExceptionsFactory.createTokenExpiredException();
		} else if (!requiredRoles.isEmpty()) {
			String userRoles = auth.get(Authentication.ROLES);
			ensureRoles(requiredRoles, userRoles);
		}

        // TODO renew token

		fields.put(GenericFieldNames.AUTH_LOGIN_ID, auth.get(Authentication.LOGIN_ID));
		
		return fields;
	}

	private void ensureRoles(List<String> requiredRoles, String userRolesString) throws AuthenticationFailureException {
		if (StringUtils.isEmpty(userRolesString)) {
			throw CommandExceptionsFactory.createInsufficientPrivilegesException();
		}
		
		List<String> userRoles = new LinkedList<>(Arrays.asList(userRolesString.split("\\|")));
		userRoles.retainAll(requiredRoles);		
		if (userRoles.isEmpty()) {
			throw CommandExceptionsFactory.createInsufficientPrivilegesException();
		}
	}

	@Override
	public Authentication requestToken(Fields fields) {
		Login login = authenticateUsingAnOtherService(fields);

		Authentication auth = createAuthenticationFor(login);
		
		try {
			auth = fAuthRepo.save(auth);
		} catch (ValidationException|RepositoryException exception) {
			LOGGER.error("Unexpected behaviour", exception);
			return null;
		}
		
		return auth;
	}

	private Authentication createAuthenticationFor(Login login) {
		
		long utcTimeSecs = fTime.getCurrentTimeMillis() / 1000 + fValidityIntervalSecs;
		String token = fRandom.generateString("authToken", 8);
		
		return fEntitiesBuilder.getInstance(Authentication.class, new Fields( 
					GenericFieldNames.LOGIN_ID, login.get(Login.ID),
					Authentication.ROLES, login.get(Login.ROLES),
					GenericFieldNames.AUTH_TOKEN, token,
					GenericFieldNames.AUTH_TOKEN_EXPIRE, utcTimeSecs));
	}

	private boolean isExpired(Authentication authentication) {
		long expireTimeSecs = authentication.get(GenericFieldNames.AUTH_TOKEN_EXPIRE);
		long nowSecs = fTime.getCurrentTimeMillis() / 1000;
		return nowSecs > expireTimeSecs;
	}

//	public static void renewAuthentication(Context context, Authentication authentication) {
//		Long validityIntervalSecs = Long.parseLong(context.getSystemProperty(Context.AUTHENTICATION_VALIDITY_INTERVAL_SECS));
//		
//		long nowSecs = System.currentTimeMillis() / 1000;
//		authentication.fields().put(GenericFieldNames.AUTH_TOKEN_EXPIRE, nowSecs + validityIntervalSecs);
//	}

	private Login authenticateUsingAnOtherService(Fields fields) {
		Login login = fLoginRepo.retrieveByForeignKey(Login.USER_EMAIL, (String) fields.get(Login.USER_EMAIL));
		if (login.get(Login.PASSWORD).equals(fields.get(Login.PASSWORD))) {
			return login;
		}
		return null;
	}
	
}
