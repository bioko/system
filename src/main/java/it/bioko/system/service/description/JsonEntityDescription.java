package it.bioko.system.service.description;

import java.util.HashMap;

import org.json.simple.JSONAware;
import org.json.simple.JSONValue;

public class JsonEntityDescription implements JSONAware {

	private String _entityName;
	private HashMap<String, JsonFieldDescription> _fields = new HashMap<String, JsonFieldDescription>();
	
	@Override
	public String toJSONString() {
		return JSONValue.toJSONString(_fields);
	}
	
	@Override
	public String toString() {
		return new StringBuilder().
				append('\"').append(_entityName).append("\":").
				append(toJSONString()).
				toString();
	}

	public String getEntityName() {
		return _entityName;
	}
	
	protected void setEntityName(String entityName) {
		_entityName = entityName;
	}
	
	protected void addField(String fieldName, JsonFieldDescription fieldDescription) {
		_fields.put(fieldName, fieldDescription);
	}
	
}
