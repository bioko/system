package it.bioko.system.service.description.dummy;

import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.domain.annotation.field.VirtualField;
import it.bioko.utils.domain.annotation.hint.Hint;
import it.bioko.utils.fields.Fields;

@SuppressWarnings("serial")
public class DummyEntityWithLocation extends DomainEntity {

	@Field
	public static final String A_FIELD = "aField";
	
	@Field(type = Double.class, hints = {
		@Hint(name = "cmsType", value = "hidden")
	})
	public static final String LATITUDE = "latitude";
	
	@Field(type = Double.class, hints = {
		@Hint(name = "cmsType", value = "hidden")
	})
	public static final String LONGITUDE = "longitude";
	
	@VirtualField(hints={
		@Hint(name = "latitudeField", value = LATITUDE),
		@Hint(name = "longitudeField", value = LONGITUDE),
		@Hint(name = "cmsType", value = "location")
	})
	public static final String LOCATION = "location";
	
	public DummyEntityWithLocation(Fields input) {
		super(input);
	}
	
}
