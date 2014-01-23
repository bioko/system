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

package org.biokoframework.system.entity.login;

import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.service.crypto.EntityEncryptor;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.annotation.field.Field;
import org.biokoframework.utils.domain.annotation.hint.Hint;
import org.biokoframework.utils.fields.Fields;


@SuppressWarnings("serial")
public class Login extends DomainEntity {

	public static final String ENTITY_KEY = GenericFieldNames.LOGIN_ID;
	
	@Field(hints = {
			@Hint(name = "cmsType", value = "email")
		})
	public static final String USER_EMAIL = GenericFieldNames.USER_EMAIL;

	@Field(hints = {
		@Hint(name = EntityEncryptor.HINT, value = EntityEncryptor.ONE_WAY_HINT)
	})
	public static final String PASSWORD   = GenericFieldNames.PASSWORD;

	@Field(mandatory=false)
	public static final String ROLES = "roles";
	@Field(mandatory = false)
	public static final String FACEBOOK_ID = "facebookId";
	
	public Login(Fields input) {
		super(input);
	}
	
}