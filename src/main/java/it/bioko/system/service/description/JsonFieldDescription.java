/*
 * Copyright (c) 2014																 
 *	Mikol Faro			<mikol.faro@gmail.com>
 *	Simone Mangano		<simone.mangano@ieee.org>
 *	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

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
