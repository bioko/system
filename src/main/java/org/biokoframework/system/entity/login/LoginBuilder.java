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

import javax.inject.Inject;

import org.biokoframework.utils.domain.EntityBuilder;

import com.google.inject.Injector;


public class LoginBuilder extends EntityBuilder<Login> {
	
	public static final String MATTIA = "mattia";
	private static final String MATTIA_JSON = 
			"{'id':'1', 'userEmail':'m.tortorelli@engaged.it','password':'1234'}";

	public static final String SIMONE = "simone";
	private static final String SIMONE_JSON = 
			"{'id':'2', 'userEmail':'s.mangano@engaged.it','password':'1234'}";

	public static final String MIKOL = "mikol";
	private static final String MIKOL_JSON = 
			"{'id':'3', 'userEmail':'mikol.faro@gmail.com','password':'1234'}";
	
	public static final String GENERIC_USER_WITHOUT_ROLE = "genericUserWORole";
	public static final String GENERIC_USER_WITH_ADMIN_ROLE = "genericUserWithAdminRole";
	public static final String GENERIC_USER_WITH_ANOTHER_ROLE = "genericUserWithAnotherRole";
	public static final String GENERIC_USER_WITH_BOTH_ROLES = "genericUserWithBothRoles";

	public static final String MATTIA_ENCRYPTED = "mattiaEncrypted";
	
	public LoginBuilder() {
		this(null);
	}
	
	@Inject
	public LoginBuilder(Injector injector) {
		super(Login.class, injector);

		putExample(MATTIA, MATTIA_JSON);
		putExample(SIMONE, SIMONE_JSON);
		putExample(MIKOL, MIKOL_JSON);
		
		putExample(GENERIC_USER_WITHOUT_ROLE, "{'id':'4', 'userEmail':'genericWithoutRole@gmail.com','password':'1234'}");
		putExample(GENERIC_USER_WITH_ADMIN_ROLE, "{'id':'5', 'userEmail':'genericWithAdminRole@gmail.com','password':'1234','roles':'admin'}");
		putExample(GENERIC_USER_WITH_ANOTHER_ROLE, "{'id':'6', 'userEmail':'genericWithAnotherRole@gmail.com','password':'1234','roles':'another'}");
		putExample(GENERIC_USER_WITH_BOTH_ROLES, "{'id':'7', 'userEmail':'genericWithBothRoles@gmail.com','password':'1234','roles':'admin|another'}");
		
		putExample(MATTIA_ENCRYPTED, "{'id':'1', 'userEmail':'m.tortorelli@engaged.it','password':'1234'}");
		
	}
	
	@Override
	public EntityBuilder<Login> loadDefaultExample() {
		return loadExample(MATTIA);
	}
}
