package it.bioko.system.entity.description;

import it.bioko.utils.domain.EntityBuilder;

import java.util.ArrayList;


public class CommandEntityBuilder extends EntityBuilder<CommandEntity> {

	public static final String EXAMPLE = "example";
	private static final String EXAMPLE_JSON = "{'name':'command_list','input':[],'output':[]}";
	
	public CommandEntityBuilder() {
		super(CommandEntity.class);
		putExample(EXAMPLE, EXAMPLE_JSON);
	}
	
	@Override
	public EntityBuilder<CommandEntity> loadDefaultExample() {
		return loadExample(EXAMPLE);
	}
	
	@Override
	public CommandEntity build(boolean exists) {
		if (exists) {
			throw new UnsupportedOperationException(
					"Command entities are not supposed to be stored in repositories"
			);
		} else { 
			return super.build(exists);
		}
	}
	
	public void setInput(ArrayList<ParameterEntity> input) {
		_currentFields.put(CommandEntity.INPUT, input);
	}
	
	public void setOutput(ArrayList<ParameterEntity> output) {
		_currentFields.put(CommandEntity.OUTPUT, output);
	}
}
