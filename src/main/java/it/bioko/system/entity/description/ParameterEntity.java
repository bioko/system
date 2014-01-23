package it.bioko.system.entity.description;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;


@SuppressWarnings("serial")
public class ParameterEntity extends DomainEntity {

	@Field
	public static final String NAME = FieldNames.NAME;
	
	@Field(mandatory = false)
	public static final String DEFAULT = GenericFieldNames.DEFAULT;
	
	// This parameter is composed by the content
	@Field(mandatory = false)
	public static final String CONTENT = "content";
	
	// The parameter is expected in a specific http part
	@Field(mandatory = false)
	public static final String HTTP_PARAMETER_TYPE = GenericFieldNames.HTTP_PARAMETER_TYPE;
	public static final String HTTP_BODY           = GenericFieldValues.BODY;
	public static final String HTTP_QUERY_STRING   = GenericFieldValues.QUERY_STRING;
	public static final String HTTP_HEADER         = GenericFieldValues.HEADER;
	public static final String HTTP_URL_PATH       = GenericFieldValues.URL_PATH;

	// This parameter is an array
	@Field(mandatory = false, type = Integer.class)
	public static final String MINIMUM = GenericFieldNames.MINIMUM;
	@Field(mandatory = false, type = Integer.class)
	public static final String MAXIMUM = GenericFieldNames.MAXIMUM;
	
	public ParameterEntity(Fields input) {
		super(input);
	}

	public static final String ENTITY_KEY = GenericFieldNames.NOT_EXPECTED_ID;
	
}
