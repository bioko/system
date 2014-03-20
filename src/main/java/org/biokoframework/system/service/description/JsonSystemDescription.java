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

package org.biokoframework.system.service.description;

import org.json.simple.JSONAware;
import org.json.simple.JSONValue;

import java.util.HashMap;

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
