package it.bioko.system.service.crypto;

import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.domain.annotation.hint.Hint;
import it.bioko.utils.fields.Fields;

@SuppressWarnings("serial")
public class DummyDecryptableEntity extends DomainEntity {

	@Field
	public static final String A_PLAIN_FIELD = "aPlainField";
	
	@Field(hints = {
			@Hint(name = EntityEncryptor.HINT, value = EntityEncryptor.TWO_WAY_HINT)
	})
	public static final String A_TWO_WAY_ENCRYPTED_FIELD = "aTwoWayEncryptedField";
	
	public DummyDecryptableEntity(Fields input) {
		super(input);
	}
	
}
