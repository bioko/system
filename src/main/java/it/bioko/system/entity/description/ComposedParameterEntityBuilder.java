package it.bioko.system.entity.description;

import java.util.List;

public class ComposedParameterEntityBuilder extends ParameterEntityBuilder {

	public ComposedParameterEntityBuilder() {
		super();
	}

	public void setContent(List<ParameterEntity> content) {
		_currentFields.put(ParameterEntity.CONTENT, content);
	}

}
