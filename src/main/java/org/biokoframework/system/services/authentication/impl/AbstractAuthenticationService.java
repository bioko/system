/*
 * Copyright (c) $year.
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

package org.biokoframework.system.services.authentication.impl;

import org.apache.commons.lang3.StringUtils;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.services.authentication.AuthenticationFailureException;
import org.biokoframework.system.services.authentication.IAuthenticationService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-03-12
 */
public abstract class AbstractAuthenticationService implements IAuthenticationService {

    protected void ensureRoles(List<String> requiredRoles, String userRolesString) throws AuthenticationFailureException {
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return;
        }
        if (StringUtils.isEmpty(userRolesString)) {
            throw CommandExceptionsFactory.createInsufficientPrivilegesException();
        }

        List<String> userRoles = new LinkedList<>(Arrays.asList(userRolesString.split("\\|")));
        userRoles.retainAll(requiredRoles);
        if (userRoles.isEmpty()) {
            throw CommandExceptionsFactory.createInsufficientPrivilegesException();
        }
    }

}
