package it.bioko.system.service.description;

import java.util.HashMap;

import org.json.simple.JSONAware;
import org.json.simple.JSONValue;

public class JsonFieldDescription implements JSONAware {

	private static final String TYPE = "type";
	private static final String MANDATORY = "mandatory";
	private static final String HINTS = "hints";
	private static final String REFERENCE = "reference";
	private static final String REFERENCE_FIELD = "referenceField";
	private static final String VIRTUAL = "virtual";

	private HashMap<String, Object> _internalRepresentation = new HashMap<String, Object>();
	private HashMap<String, Object> _hints = new HashMap<String, Object>();
	
	@Override
	public String toJSONString() {
		if (!_hints.isEmpty()) {
			_internalRepresentation.put(HINTS, _hints);
		}
		return JSONValue.toJSONString(_internalRepresentation);
	}

	protected void setType(String typeName) {
		_internalRepresentation.put(TYPE, typeName);
	}

	protected void setMandatory(boolean mandatory) {
		if (_internalRepresentation.containsKey(MANDATORY) || !mandatory) {
			_internalRepresentation.put(MANDATORY, mandatory);
		}
	}

	protected void addHint(String name, String value) {
		_hints.put(name, value);
	}

	public void setReference(String referencedType, String referencedField) {
		_internalRepresentation.put(REFERENCE, referencedType);
		_internalRepresentation.put(REFERENCE_FIELD, referencedField);
	}
	
	protected void setVirtual(boolean virtual) {
		_internalRepresentation.put(VIRTUAL, virtual);
	}

}
