package it.bioko.system.entity.description;


public class ListParameterEntityBuilder extends ParameterEntityBuilder {

	public ListParameterEntityBuilder() {
		super();
	}
	
	public void setContent(ParameterEntity content) {
		_currentFields.put(ParameterEntity.CONTENT, content);
	}

}
