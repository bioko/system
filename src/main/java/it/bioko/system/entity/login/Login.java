package it.bioko.system.entity.login;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.service.crypto.EntityEncryptor;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.domain.annotation.hint.Hint;
import it.bioko.utils.fields.Fields;


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