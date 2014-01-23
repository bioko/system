package it.bioko.system.service.description.dummy;

import it.bioko.system.entity.login.Login;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.fields.Fields;

@SuppressWarnings("serial")
public class DummyReferencingEntity extends DomainEntity {

	@Field
	public static final String A_FIELD = "aField";
	
	@Field(type = Login.class)
	public static final String LOGIN_ID = "loginId";
	
	public DummyReferencingEntity(Fields input) {
		super(input);
	}

}
