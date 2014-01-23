package it.bioko.system.entity.authentication;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.entity.login.Login;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.fields.Fields;

@SuppressWarnings("serial")
public class Authentication extends DomainEntity {

	public static final String ENTITY_KEY = GenericFieldNames.AUTHENTICATION_ID;
	
	@Field(type=Login.class)
	public static final String LOGIN_ID     = Login.ENTITY_KEY;
	@Field
	public static final String TOKEN        = GenericFieldNames.AUTH_TOKEN;
	@Field(type=Long.class)
	public static final String TOKEN_EXPIRE = GenericFieldNames.AUTH_TOKEN_EXPIRE;	
	@Field(mandatory=false)
	public static final String ROLES = Login.ROLES;
	
	public Authentication(Fields input) {
		super(input);
	}

}
