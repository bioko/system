package it.bioko.system.repository.memory.dummy1;

import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.fields.Fields;

@SuppressWarnings("serial")
public class ASDummyEntity1 extends DomainEntity {

	@Field
	public static final String VALUE = "value";
	@Field
	public static final String GROUP = "entityGroup";

	public ASDummyEntity1(Fields input) {
		super(input);
	}
	
}
