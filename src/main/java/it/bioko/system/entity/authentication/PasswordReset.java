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

package it.bioko.system.entity.authentication;

import it.bioko.system.entity.login.Login;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.fields.Fields;
import it.bioko.utils.validator.Validator;

import org.joda.time.DateTime;

@SuppressWarnings("serial")
public class PasswordReset extends DomainEntity {

	@Field(type = Login.class)
	public static final String LOGIN_ID = Login.ENTITY_KEY;
	
	@Field(type = DateTime.class, dateFormat = Validator.ISO_TIMESTAMP)
	public static final String TOKEN_EXPIRATION = "tokenExpiration";
	
	@Field
	public static final String TOKEN = "token";
	
	public PasswordReset(Fields input) {
		super(input);
	}

}
