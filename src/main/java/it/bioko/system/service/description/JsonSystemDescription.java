package it.bioko.system.service.description;

import java.util.HashMap;

import org.json.simple.JSONAware;
import org.json.simple.JSONValue;

public class JsonSystemDescription implements JSONAware {

	private static final String ENTITIES = "entities";
//	private static final String COMMANDS = "commands";
	
	private HashMap<String, JsonEntityDescription> _entityDescriptions = new HashMap<String, JsonEntityDescription>();
//	private HashMap<String, Object> _commandDescriptions;
	
	@Override
	public String toJSONString() {
		HashMap<String, Object> internalRepresentation = new HashMap<String, Object>();
		internalRepresentation.put(ENTITIES, _entityDescriptions);
//		internalRepresentation.put(COMMANDS, _commandDescriptions);
		
		return JSONValue.toJSONString(internalRepresentation);
	}

	@Override
	public String toString() {
		return toJSONString();
	}

	public void addEntity(String entityName, JsonEntityDescription entityDescription) {
		_entityDescriptions.put(entityName, entityDescription);		
	}
	
}
