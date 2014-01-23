package it.bioko.system.entity.description;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;


@SuppressWarnings("serial")
public class CommandEntity extends DomainEntity {

	@Field
	public static final String NAME   = FieldNames.NAME;
	@Field(mandatory = false)
	public static final String INPUT  = GenericFieldNames.INPUT;
	@Field(mandatory = false)
	public static final String OUTPUT = GenericFieldNames.OUTPUT;
	
	public CommandEntity(Fields input) {
		super(input);
	}
	
	public static final String ENTITY_KEY = GenericFieldNames.NOT_EXPECTED_ID;
	// TODO rinominare in entityIdKey

}
