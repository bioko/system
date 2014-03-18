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

package org.biokoframework.system.services.authentication.all;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.services.authentication.AuthResponse;
import org.biokoframework.system.services.authentication.AuthenticationFailureException;
import org.biokoframework.system.services.authentication.IAuthenticationService;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

/**
 * This authentication service actually tries to authenticate with every 
 * possible {@link IAuthenticationService} it knowns.
 * 
 * Designed to be used with <a href="http://code.google.com/p/google-guice/wiki/Multibindings">Guice multibindings</a>
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Mar 7, 2014
 *
 */
public class AllAuthenticationService implements IAuthenticationService {

	private Set<IAuthenticationService> fAuthServices;

	@Inject
	public AllAuthenticationService(Set<IAuthenticationService> authServices) {
		fAuthServices = authServices;
	}
	
	@Override
	public AuthResponse authenticate(Fields fields, List<String> requiredRoles) throws AuthenticationFailureException {
		for (IAuthenticationService anAuthService : fAuthServices) {
			if (!(anAuthService instanceof AllAuthenticationService)) {
                try {
                    AuthResponse authResponse = anAuthService.authenticate(fields, requiredRoles);
                    if (authResponse != null) {
                        return authResponse;
                    }
                } catch (AuthenticationFailureException exception) {
                    if (exception.getErrors().get(0).get(ErrorEntity.ERROR_CODE) != FieldNames.AUTHENTICATION_REQUIRED_CODE) {
                        throw exception;
                    }
                }
			}
		}
		
		throw CommandExceptionsFactory.createUnauthorisedAccessException();
	}

}
