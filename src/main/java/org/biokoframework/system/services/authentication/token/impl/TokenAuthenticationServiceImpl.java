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

import org.apache.log4j.Logger;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.entity.authentication.Authentication;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.authentication.AuthResponse;
import org.biokoframework.system.services.authentication.AuthenticationFailureException;
import org.biokoframework.system.services.authentication.impl.AbstractAuthenticationService;
import org.biokoframework.system.services.authentication.token.ITokenAuthenticationService;
import org.biokoframework.system.services.authentication.token.TokenCreationException;
import org.biokoframework.system.services.currenttime.ICurrentTimeService;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.system.services.random.IRandomService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.RepositoryException;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Mar 6, 2014
 *
 */
public class TokenAuthenticationServiceImpl extends AbstractAuthenticationService implements ITokenAuthenticationService {

    private static final Logger LOGGER = Logger.getLogger(TokenAuthenticationServiceImpl.class);

    public static final String AUTH_TOKEN = "authToken";
    private static final Object AUTH_TOKEN_EXPIRE = "authTokenExpire";

    private final IEntityBuilderService fEntitiesBuilder;
	private final Repository<Login> fLoginRepo;
	private final Repository<Authentication> fAuthRepo;
	private final IRandomService fRandom;
	private final ICurrentTimeService fTime;
	private final long fValidityIntervalSecs;

    @Inject
	public TokenAuthenticationServiceImpl(IEntityBuilderService entityBuilderService, IRepositoryService repoService, 
			IRandomService randomService, ICurrentTimeService currentTimeService,
            @Named("tokenValiditySecs") long validityIntervalSecs) {
		
		fEntitiesBuilder = entityBuilderService;
		fLoginRepo = repoService.getRepository(Login.class);
		fAuthRepo = repoService.getRepository(Authentication.class);
		fRandom = randomService;
		fTime = currentTimeService;
		fValidityIntervalSecs = validityIntervalSecs;
	}
	
	@Override
	public AuthResponse authenticate(Fields fields, List<String> requiredRoles) throws AuthenticationFailureException {
        String token = (String) fields.get(AUTH_TOKEN);
        if (token == null) {
            throw CommandExceptionsFactory.createUnauthorisedAccessException();
        }
		Authentication auth = fAuthRepo.retrieveByForeignKey(Authentication.TOKEN, token);
		if (auth == null) {
			throw CommandExceptionsFactory.createUnauthorisedAccessException();
        } else if (isExpired(auth)) {
            fAuthRepo.delete(auth.getId());
            throw CommandExceptionsFactory.createTokenExpiredException();
		} else if (!requiredRoles.isEmpty()) {
			String userRoles = auth.get(Authentication.ROLES);
			ensureRoles(requiredRoles, userRoles);
		}

        auth.set(Authentication.TOKEN_EXPIRE, renewAuthentication());
        try {
            fAuthRepo.save(auth);
        } catch (ValidationException|RepositoryException exception) {
            LOGGER.error("Unexpected error while updating authentication", exception);
            throw new AuthenticationFailureException(exception);
        }
		
		return new AuthResponse(
                new Fields(GenericFieldNames.AUTH_LOGIN_ID, auth.get(Authentication.LOGIN_ID)),
                new Fields(AUTH_TOKEN, token,
                        AUTH_TOKEN_EXPIRE, auth.get(Authentication.TOKEN_EXPIRE)));
	}

	@Override
	public Authentication requestToken(Login login) throws TokenCreationException {

		Authentication auth = createAuthenticationFor(login);
		
		try {
			auth = fAuthRepo.save(auth);
		} catch (ValidationException|RepositoryException exception) {
            LOGGER.error("Unexpected error while updating authentication", exception);
            throw new TokenCreationException(exception);
        }
		
		return auth;
	}

	private Authentication createAuthenticationFor(Login login) {

        DateTime tokenExpiration = fTime.getCurrentTimeAsDateTime().withDurationAdded(fValidityIntervalSecs * 1000, 1);
		String token = fRandom.generateUUID().toString();
		
		return fEntitiesBuilder.getInstance(Authentication.class, new Fields( 
					Authentication.LOGIN_ID, login.get(DomainEntity.ID),
					Authentication.ROLES, login.get(Login.ROLES),
					Authentication.TOKEN, token,
					Authentication.TOKEN_EXPIRE, tokenExpiration.toString(ISODateTimeFormat.dateTimeNoMillis())));
	}

	private boolean isExpired(Authentication authentication) {
        String expireStr = authentication.get(GenericFieldNames.AUTH_TOKEN_EXPIRE);
        DateTime expire = DateTime.parse(expireStr);

		return expire.isBefore(fTime.getCurrentTimeAsDateTime());
	}

	private String renewAuthentication() {
        DateTime expire = fTime.getCurrentTimeAsDateTime().plus(fValidityIntervalSecs * 1000);
        return expire.toString(ISODateTimeFormat.dateTimeNoMillis());
	}

}
