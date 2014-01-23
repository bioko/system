package it.bioko.system.entity.authentication;

import it.bioko.system.entity.login.Login;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.fields.Fields;
import it.bioko.utils.validator.Validator;

import org.joda.time.DateTime;

@SuppressWarnings("serial")
public class EmailConfirmation extends DomainEntity {

	@Field(type = Login.class)
	public static final String LOGIN_ID = Login.ENTITY_KEY;
	
	@Field
	public static final String TOKEN = "token";
	
	@Field(type = Boolean.class)
	public static final String CONFIRMED = "confirmed";
	
	@Field(type = DateTime.class, mandatory = false, format = Validator.ISO_TIMESTAMP)
	public static final String CONFIRMATION_TIMESTAMP = "confirmationTimestamp";
	
	public EmailConfirmation(Fields input) {
		super(input);
	}

}
