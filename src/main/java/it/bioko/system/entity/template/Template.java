package it.bioko.system.entity.template;

import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.domain.annotation.hint.Hint;
import it.bioko.utils.domain.annotation.hint.HintNames;
import it.bioko.utils.fields.Fields;


@SuppressWarnings("serial")
public class Template extends DomainEntity{
	
	public static final String ENTITY_KEY = "templateId";

	@Field
	public static final String TITLE = "title";
	@Field(hints = {
			@Hint(name = HintNames.MAX_LENGTH, value = "65536")
	})
	public static final String BODY  = "body";
	@Field(mandatory = false)
	public static final String TRACK = "track";
	@Field(mandatory = false)
	public static final String CATEGORY = "category";
	
	public Template(Fields input) {
		super(input);
	}

}