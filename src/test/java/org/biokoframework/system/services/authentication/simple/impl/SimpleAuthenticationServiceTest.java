/*
 * Copyright (c) 2014.
 * 	Mikol Faro		<mikol.faro@gmail.com>
 * 	Simone Mangano	 	<simone.mangano@ieee.org>
 * 	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
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
 */

package org.biokoframework.system.services.authentication.simple.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.login.LoginBuilder;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.repository.memory.InMemoryRepository;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.authentication.AuthResponse;
import org.biokoframework.system.services.authentication.AuthenticationFailureException;
import org.biokoframework.system.services.entity.EntityModule;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.system.services.repository.RepositoryModule;
import org.biokoframework.utils.domain.EntityBuilder;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.RepositoryException;
import org.biokoframework.utils.validation.ValidationModule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;

import static org.biokoframework.utils.matcher.Matchers.error;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-03-12
 */
public class SimpleAuthenticationServiceTest {

    private Injector fInjector;
    private Repository<Login> fLoginRepo;
    private EntityBuilder<Login> fLoginBuilder;
    private IEntityBuilderService fEntitiesBuilder;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Before
    public void createInjector() {
        fInjector = Guice.createInjector(
                new EntityModule(),
                new ValidationModule(),
                new RepositoryModule(ConfigurationEnum.DEV) {
                    @Override
                    protected void configureForDev() {
                        bindRepositoryTo(InMemoryRepository.class);
                    }
                    @Override
                    protected void configureForDemo() {}
                    @Override
                    protected void configureForProd() {}
                }
        );

        fLoginRepo = fInjector.getInstance(IRepositoryService.class).getRepository(Login.class);
        fLoginBuilder = fInjector.getInstance(LoginBuilder.class);
        fEntitiesBuilder = fInjector.getInstance(IEntityBuilderService.class);
    }

    @Test
    public void simpleAuthenticationTest() throws AuthenticationFailureException, ValidationException, RepositoryException {
        Login login = fLoginBuilder.loadDefaultExample().build(false);
        login = fLoginRepo.save(login);

        SimpleAuthenticationService authenticationService = fInjector.getInstance(SimpleAuthenticationService.class);

        Fields fields = new Fields(
                Login.USER_EMAIL, login.get(Login.USER_EMAIL),
                Login.PASSWORD, login.get(Login.PASSWORD));

        AuthResponse response = authenticationService.authenticate(fields, Collections.<String>emptyList());

        assertThat(response, is(notNullValue()));
        assertThat((String) response.getMergeFields().get("authLoginId"), is(equalTo(login.getId())));
    }

    @Test
    public void failBecauseUserDoesNotExist() throws AuthenticationFailureException {
        Login login = fLoginBuilder.loadDefaultExample().build(false);

        SimpleAuthenticationService authenticationService = fInjector.getInstance(SimpleAuthenticationService.class);

        Fields fields = new Fields(
                Login.USER_EMAIL, login.get(Login.USER_EMAIL),
                Login.PASSWORD, login.get(Login.PASSWORD));

        expected.expect(AuthenticationFailureException.class);
        expected.expect(error(CommandExceptionsFactory.createInvalidLoginException().getErrors().get(0)));
        authenticationService.authenticate(fields, Collections.<String>emptyList());
    }

    @Test
    public void failBecauseBadPassword() throws ValidationException, RepositoryException, AuthenticationFailureException {
        Login login = fLoginBuilder.loadDefaultExample().build(false);
        login = fLoginRepo.save(login);

        SimpleAuthenticationService authenticationService = fInjector.getInstance(SimpleAuthenticationService.class);

        Fields fields = new Fields(
                Login.USER_EMAIL, login.get(Login.USER_EMAIL),
                Login.PASSWORD, "A Wrong Password");

        expected.expect(AuthenticationFailureException.class);
        expected.expect(error(CommandExceptionsFactory.createInvalidLoginException().getErrors().get(0)));
        authenticationService.authenticate(fields, Collections.<String>emptyList());
    }

    @Test
    public void failBecauseAuthInfosAreNotFound() throws AuthenticationFailureException {
        SimpleAuthenticationService authenticationService = fInjector.getInstance(SimpleAuthenticationService.class);

        Fields fields = new Fields();

        expected.expect(AuthenticationFailureException.class);
        expected.expect(error(CommandExceptionsFactory.createUnauthorisedAccessException().getErrors().get(0)));
        authenticationService.authenticate(fields, Collections.<String>emptyList());
    }

}
