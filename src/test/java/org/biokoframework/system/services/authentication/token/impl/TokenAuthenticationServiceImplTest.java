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

import static org.biokoframework.utils.matcher.Matchers.contains;
import static org.biokoframework.utils.matcher.Matchers.valid;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.entity.authentication.Authentication;
import org.biokoframework.system.entity.authentication.AuthenticationBuilder;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.login.LoginBuilder;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.repository.memory.InMemoryRepository;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.authentication.AuthResponse;
import org.biokoframework.system.services.authentication.AuthenticationFailureException;
import org.biokoframework.system.services.authentication.annotation.Auth;
import org.biokoframework.system.services.currenttime.CurrentTimeModule;
import org.biokoframework.system.services.currenttime.impl.TestCurrentTimeService;
import org.biokoframework.system.services.entity.EntityModule;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.system.services.random.RandomModule;
import org.biokoframework.system.services.random.impl.TestRandomGeneratorService;
import org.biokoframework.system.services.repository.RepositoryModule;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.EntityBuilder;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.matcher.Matchers;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.RepositoryException;
import org.biokoframework.utils.validation.ValidationModule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import org.junit.rules.ExpectedException;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Mar 7, 2014
 *
 */
public class TokenAuthenticationServiceImplTest {

	private final long fTokenValiditySecs = 900;
	private TokenAuthenticationServiceImpl fAuthService;
	private Injector fInjector;
	private Repository<Login> fLoginRepo;
    private Repository<Authentication> fAuthRepo;
	private IEntityBuilderService fEntitiesBuilder;
	private EntityBuilder<Login> fLoginBuilder;
    private EntityBuilder<Authentication> fAuthBuilder;
	private TestCurrentTimeService fTimeService;

    @Before
	public void createInjector() {		
		fInjector = Guice.createInjector(
				new EntityModule(),
				new ValidationModule(),
				new RandomModule(ConfigurationEnum.DEV),
				new CurrentTimeModule(ConfigurationEnum.DEV),
				new RepositoryModule(ConfigurationEnum.DEV) {
					@Override
					protected void configureForDev() {
						bindRepositoryTo(InMemoryRepository.class);
					}
					@Override
					protected void configureForProd() {}
					@Override
					protected void configureForDemo() {}
				},
				new AbstractModule(){
					@Override
					protected void configure() {
						bindConstant()
							.annotatedWith(Names.named("tokenValiditySecs"))
							.to(fTokenValiditySecs);
					}
				});
		
		fLoginRepo = fInjector.getInstance(IRepositoryService.class).getRepository(Login.class);
		fLoginBuilder = fInjector.getInstance(LoginBuilder.class);
        fAuthRepo = fInjector.getInstance(IRepositoryService.class).getRepository(Authentication.class);
        fAuthBuilder = fInjector.getInstance(AuthenticationBuilder.class);
		fEntitiesBuilder = fInjector.getInstance(IEntityBuilderService.class);
		fTimeService = fInjector.getInstance(TestCurrentTimeService.class);
	}
	
	@Test
	public void testSimpleGetToken() throws Exception {
		Login login = fLoginBuilder.loadDefaultExample().build(false);
		login = fLoginRepo.save(login);

		Authentication expectedAuthentication = fEntitiesBuilder.getInstance(Authentication.class, new Fields(
				DomainEntity.ID, "1",
				Authentication.LOGIN_ID, login.getId(),
				Authentication.ROLES, login.get(Login.ROLES),
				Authentication.TOKEN, "00000000-0000-0000-0000-000000000000",
				Authentication.TOKEN_EXPIRE, fTimeService.getCurrentTimeMillis() / 1000 + fTokenValiditySecs));
		
		fAuthService = fInjector.getInstance(TokenAuthenticationServiceImpl.class);
		
		Authentication authentication = fAuthService.requestToken(login);
		assertThat(authentication, is(valid()));
		assertThat(authentication, is(equalTo(expectedAuthentication)));
		
	}

    @Test
    public void testSuccessfulAuthenticationUsingToken() throws ValidationException, RepositoryException, AuthenticationFailureException {
        Login login = fLoginBuilder.loadDefaultExample().build(false);
        login = fLoginRepo.save(login);

        fAuthService = fInjector.getInstance(TokenAuthenticationServiceImpl.class);

        Authentication auth = fAuthBuilder.loadDefaultExample()
                .set(Authentication.LOGIN_ID, login.getId()).build(false);
        fAuthRepo.save(auth);

        Fields fields = new Fields("authToken", auth.get(Authentication.TOKEN));
        AuthResponse authResponse = fAuthService.authenticate(fields, Collections.<String>emptyList());

        assertThat(authResponse.getMergeFields(), contains(GenericFieldNames.AUTH_LOGIN_ID, login.getId()));

        long authTokenExpire = authResponse.getOverrideFields().get(Authentication.TOKEN_EXPIRE);
        assertThat(authTokenExpire, is(fTimeService.getCurrentTimeMillis() / 1000 + fTokenValiditySecs));
    }

	@Test
	public void testFailedAuthenticationBecauseTokenExpired() throws ValidationException, RepositoryException, AuthenticationFailureException {
        Login login = fLoginBuilder.loadDefaultExample().build(false);
        login = fLoginRepo.save(login);

        fAuthService = fInjector.getInstance(TokenAuthenticationServiceImpl.class);

        Authentication auth = fAuthBuilder.loadDefaultExample()
                .set(Authentication.LOGIN_ID, login.getId())
                .set(Authentication.TOKEN_EXPIRE, 0L)
                .build(false);
        auth = fAuthRepo.save(auth);

        Fields fields = new Fields("authToken", auth.get(Authentication.TOKEN));

        try {
            fAuthService.authenticate(fields, Collections.<String>emptyList());
            fail(AuthenticationFailureException.class + " not thrown");
        } catch (AuthenticationFailureException exception) {
            assertThat(exception.getErrors(), is(equalTo(CommandExceptionsFactory.createTokenExpiredException().getErrors())));
        }

        assertThat(fAuthRepo.retrieve(auth.getId()), is(nullValue()));
    }

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testFailedBecauseRoleExpected() throws ValidationException, RepositoryException, AuthenticationFailureException {
        Login login = fLoginBuilder.loadDefaultExample().build(false);
        login = fLoginRepo.save(login);

        fAuthService = fInjector.getInstance(TokenAuthenticationServiceImpl.class);

        Authentication auth = fAuthBuilder.loadDefaultExample()
                .set(Authentication.LOGIN_ID, login.getId())
                .build(false);
        auth = fAuthRepo.save(auth);

        Fields fields = new Fields("authToken", auth.get(Authentication.TOKEN));

        expected.expect(AuthenticationFailureException.class);
        fAuthService.authenticate(fields, Collections.singletonList("aRole"));
    }
}
