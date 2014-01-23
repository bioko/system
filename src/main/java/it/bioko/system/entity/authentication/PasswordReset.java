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
