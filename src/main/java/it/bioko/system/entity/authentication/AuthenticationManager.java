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

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.context.Context;
import it.bioko.system.entity.login.Login;
import it.bioko.utils.fields.Fields;

import java.util.UUID;


public class AuthenticationManager {

	public static Authentication createAuthenticationFor(Context context, Login login) {
		Long validityIntervalSecs = Long.parseLong(context.getSystemProperty(Context.AUTHENTICATION_VALIDITY_INTERVAL_SECS));
		
		Fields fields = Fields.empty();
		fields.put(GenericFieldNames.LOGIN_ID, login.get(Login.ID));
		fields.put(Authentication.ROLES, login.get(Login.ROLES));
		fields.put(GenericFieldNames.AUTH_TOKEN, UUID.randomUUID().toString());
		long utcTimeSecs = System.currentTimeMillis() / 1000 + validityIntervalSecs;
		fields.put(GenericFieldNames.AUTH_TOKEN_EXPIRE, Long.toString(utcTimeSecs));
		return new Authentication(fields);
	}
	
	public static boolean isExpired(Authentication authentication) {
		long expireTimeSecs = Long.parseLong(authentication.get(GenericFieldNames.AUTH_TOKEN_EXPIRE));
		long nowSecs = System.currentTimeMillis() / 1000;
		return nowSecs < expireTimeSecs;
	}
	
	public static void renewAuthentication(Context context, Authentication authentication) {
		Long validityIntervalSecs = Long.parseLong(context.getSystemProperty(Context.AUTHENTICATION_VALIDITY_INTERVAL_SECS));
		
		long nowSecs = System.currentTimeMillis() / 1000;
		authentication.fields().put(GenericFieldNames.AUTH_TOKEN_EXPIRE, Long.toString(nowSecs + validityIntervalSecs));
	}
}
