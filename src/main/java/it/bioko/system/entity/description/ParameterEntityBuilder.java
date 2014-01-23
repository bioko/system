package it.bioko.system.entity.description;

import it.bioko.utils.domain.EntityBuilder;



public class ParameterEntityBuilder extends EntityBuilder<ParameterEntity> {
	
	public static final String EXAMPLE = "example";
	private static final String EXAMPLE_JSON = "{'name':'the_parameter'}";

	public ParameterEntityBuilder() {
		super(ParameterEntity.class);
		putExample(EXAMPLE, EXAMPLE_JSON);
	}

	@Override
	public ParameterEntity build(boolean exists) {
		if (exists) {
			throw new UnsupportedOperationException(
					"Command entities are not supposed to be stored in repositories"
			);
		} else { 
			return super.build(exists);
		}
	}

	@Override
	public EntityBuilder<ParameterEntity> loadDefaultExample() {
		return loadExample(EXAMPLE);
	}

}
