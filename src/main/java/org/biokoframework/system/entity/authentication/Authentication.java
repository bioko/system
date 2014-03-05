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

package org.biokoframework.system.entity.authentication;

import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.annotation.field.Field;

public class Authentication extends DomainEntity {

	private static final long serialVersionUID = 884203019812835755L;

	public static final String ENTITY_KEY = GenericFieldNames.AUTHENTICATION_ID;
	
	@Field(type=Login.class)
	public static final String LOGIN_ID     = Login.ENTITY_KEY;
	@Field
	public static final String TOKEN        = GenericFieldNames.AUTH_TOKEN;
	@Field(type=Long.class)
	public static final String TOKEN_EXPIRE = GenericFieldNames.AUTH_TOKEN_EXPIRE;	
	@Field(mandatory=false)
	public static final String ROLES = Login.ROLES;

}
