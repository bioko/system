package it.bioko.system.service.crypto;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import it.bioko.system.entity.login.Login;
import it.bioko.system.entity.login.LoginBuilder;
import it.bioko.utils.fields.Fields;

import org.apache.commons.lang3.StringUtils;
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
		
		assertThat(BCrypt.checkpw(login.get(Login.PASSWORD), encryptedLogin.get(Login.PASSWORD)), is(true));
		assertThat(BCrypt.checkpw("A wrong password", encryptedLogin.get(Login.PASSWORD)), is(false));
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
				StringUtils.equals(plainEntity.get(aFieldName), encryptedEntity.get(aFieldName));
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
