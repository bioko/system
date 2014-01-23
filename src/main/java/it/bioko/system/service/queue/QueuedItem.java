package it.bioko.system.service.queue;

import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.domain.annotation.hint.Hint;
import it.bioko.utils.domain.annotation.hint.HintNames;
import it.bioko.utils.fields.Fields;

@SuppressWarnings("serial")
public class QueuedItem extends DomainEntity {

	public static final String ENTITY_KEY = "queuedItemId";
	
	@Field(hints = {
		@Hint(name = HintNames.MAX_LENGTH, value = "65536")
	})
	public static final String CONTENT = "content";
	
	@Field(type=Long.class)
	public static final String IDX = "idx";
	
	public QueuedItem(Fields input) {
		super(input);
	}

}
