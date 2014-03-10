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

import static org.biokoframework.utils.matcher.Matchers.valid;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.entity.authentication.Authentication;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.login.LoginBuilder;
import org.biokoframework.system.repository.memory.InMemoryRepository;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.currenttime.CurrentTimeModule;
import org.biokoframework.system.services.currenttime.impl.TestCurrentTimeService;
import org.biokoframework.system.services.entity.EntityModule;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.system.services.random.RandomModule;
import org.biokoframework.system.services.random.impl.TestRandomGeneratorService;
import org.biokoframework.system.services.repository.RepositoryModule;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.EntityBuilder;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.validation.ValidationModule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

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
	private IEntityBuilderService fEntitiesBuilder;
	private EntityBuilder<Login> fLoginBuilder;
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
		fEntitiesBuilder = fInjector.getInstance(IEntityBuilderService.class);
		fTimeService = fInjector.getInstance(TestCurrentTimeService.class);
	}
	
	@Test
	public void testSimpleGetToken() throws Exception {
		Login login = fLoginBuilder.loadDefaultExample().build(false);
		login = fLoginRepo.save(login);

		String token = "thisIsTheToken123456";
		TestRandomGeneratorService.setSingleRandomValue("authToken", token);
		
		Fields inputFields = new Fields(
				Login.USER_EMAIL, login.get(Login.USER_EMAIL),
				Login.PASSWORD, login.get(Login.PASSWORD));

		Authentication expectedAuthentication = fEntitiesBuilder.getInstance(Authentication.class, new Fields(
				DomainEntity.ID, "1",
				Authentication.LOGIN_ID, login.getId(),
				Authentication.ROLES, login.get(Login.ROLES),
				Authentication.TOKEN, token,
				Authentication.TOKEN_EXPIRE, fTimeService.getCurrentTimeMillis() / 1000 + fTokenValiditySecs));
		
		fAuthService = fInjector.getInstance(TokenAuthenticationServiceImpl.class);
		
		Authentication authentication = fAuthService.requestToken(inputFields);
		assertThat(authentication, is(valid()));
		assertThat(authentication, is(equalTo(expectedAuthentication)));
		
	}

	@Ignore("not yet implemented")
	@Test
	public void testFailedAuthenticationBecauseTokenExpired() {
		
	}
	
	@Ignore("not yet implemented")
	@Test
	public void testSuccessfullAuthenticationUsingToken() {
		
	}
}
