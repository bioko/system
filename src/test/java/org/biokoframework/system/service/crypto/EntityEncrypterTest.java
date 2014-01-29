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

package org.biokoframework.system.service.crypto;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.login.LoginBuilder;
import org.biokoframework.system.service.crypto.EntityEncryptor;
import org.biokoframework.utils.fields.Fields;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class EntityEncrypterTest {

	@Test
	public void simpleEncryptionSaltyTest() {
		EntityEncryptor encrypter = new EntityEncryptor();
		
		Login login = new LoginBuilder().loadDefaultExample().build(false);
		Login encryptedLogin = encrypter.encryptEntity(login);

		assertThat(encryptedLogin, notNullValue());
		assertThat(encryptedLogin.fields().keys(), contains(login.fields().keys().toArray(new String[0])));
		
		for (String aFieldName : login.fields().keys()) {
			if (!aFieldName.equals(Login.PASSWORD)) {
				assertThat(encryptedLogin.get(aFieldName), is(equalTo(login.get(aFieldName))));
			}
		}
		
		assertThat(encryptedLogin.get(Login.PASSWORD), is(not(equalTo(login.get(Login.PASSWORD)))));
		
		assertThat(BCrypt.checkpw(login.get(Login.PASSWORD).toString(), encryptedLogin.get(Login.PASSWORD).toString()), is(true));
		assertThat(BCrypt.checkpw("A wrong password", encryptedLogin.get(Login.PASSWORD).toString()), is(false));
	}
	
	@Test
	public void simpleMatchTest() {
		EntityEncryptor encrypter = new EntityEncryptor();
		
		Login login = new LoginBuilder().loadDefaultExample().build("3");
		Login encryptedLogin = encrypter.encryptEntity(login);
		
		assertThat(encrypter.matchEncrypted(login, encryptedLogin, null), is(true));
	}
	
	@Test
	public void encryptionDecryptionTest() {
		String password = "GinoPoni";
		EntityEncryptor encrypter = new EntityEncryptor();
		
		DummyDecryptableEntity plainEntity = new DummyDecryptableEntity(Fields.empty());
		plainEntity.set(DummyDecryptableEntity.A_PLAIN_FIELD, "plain");
		plainEntity.set(DummyDecryptableEntity.A_TWO_WAY_ENCRYPTED_FIELD, "twoWayValue");
		
		
		DummyDecryptableEntity encryptedEntity = encrypter.encryptEntity(plainEntity);
		
		assertThat(encryptedEntity, notNullValue());
		assertThat(encryptedEntity.fields().keys(), contains(plainEntity.fields().keys().toArray(new String[0])));
		
		for (String aFieldName : plainEntity.fields().keys()) {
			if (!aFieldName.equals(DummyDecryptableEntity.A_TWO_WAY_ENCRYPTED_FIELD)) {
				StringUtils.equals(plainEntity.get(aFieldName).toString(), encryptedEntity.get(aFieldName).toString());
			}
		}
		
		assertThat(encryptedEntity.get(DummyDecryptableEntity.A_TWO_WAY_ENCRYPTED_FIELD), 
				is(not(equalTo(plainEntity.get(DummyDecryptableEntity.A_TWO_WAY_ENCRYPTED_FIELD)))));
		
		assertThat(encrypter.matchEncrypted(plainEntity, encryptedEntity, password), is(true));
		
		DummyDecryptableEntity decryptedEntity = encrypter.decryptEntity(encryptedEntity);
		
		assertThat(decryptedEntity, notNullValue());
		assertThat(decryptedEntity, is(equalTo(plainEntity)));
	}
}
