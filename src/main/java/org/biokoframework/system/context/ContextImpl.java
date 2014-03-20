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

package org.biokoframework.system.context;

import org.apache.log4j.Logger;
import org.biokoframework.system.command.AbstractCommandHandler;
import org.biokoframework.system.event.SystemListener;
import org.biokoframework.system.repository.core.AbstractRepository;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ContextImpl extends Context {

	private static final String REPOSITORIES = "repositories";
	private static final String COMMAND_HANDLER = "commandHandler";
	private static final String SYSTEM_PROPERTIES = "systemProperties";
	private static final String SYSTEM_LISTENERS = "systemListeners";
	
	private Fields _contextMap = new Fields();	

	public ContextImpl(Fields contextFields) {
		_contextMap = contextFields.copy();
		_contextMap.put(REPOSITORIES, new HashMap<String, AbstractRepository<?>>());
		_contextMap.put(SYSTEM_PROPERTIES, new HashMap<String, String>());
		_contextMap.put(SYSTEM_LISTENERS, new LinkedList<SystemListener>());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(String name) {		
		return (T) _contextMap.get(name);
	}
	
	@Override
	public void put(String name, Object value) {
		_contextMap.put(name, value);
	}
	
	@Override
	public Logger getLogger() {
		return _contextMap.get(LOGGER);
	}
	
	@Override
	public String getSystemName() {
		return _contextMap.get(SYSTEM_NAME);
	}
	
	@Override
	public void addRepository(String repoName, AbstractRepository<?> repo) {
		HashMap<String, AbstractRepository<?>> repoMap = _contextMap.get(REPOSITORIES);
		repoMap.put(repoName, repo);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <DE extends DomainEntity> Repository<DE> getRepository(String repoName) {
				
		HashMap<String, AbstractRepository<?>> repoMap = _contextMap.get(REPOSITORIES);
		Repository<?> repo = repoMap.get(repoName);
		
		return (Repository<DE>) repo;
	}

	@Override
	public void setCommandHandler(AbstractCommandHandler commandHandler) {
		_contextMap.put(COMMAND_HANDLER, commandHandler);		
	}

	@Override
	public AbstractCommandHandler getCommandHandler() {		
		return _contextMap.get(COMMAND_HANDLER);
	}
	
	@Override 
	public void setSystemProperty(String name, String value) {
		HashMap<String, String> properties = _contextMap.get(SYSTEM_PROPERTIES);
		properties.put(name, value);
	}
	
	@Override
	public String getSystemProperty(String name) {
		HashMap<String, String> properties = _contextMap.get(SYSTEM_PROPERTIES);
		return properties.get(name);
	}
	
	@Override
	public void addSystemListener(SystemListener listener) {
		List<SystemListener> listeners = _contextMap.get(SYSTEM_LISTENERS);
		listeners.add(listener);
	}
	
	@Override
	public List<SystemListener> getSystemListeners() {
		return _contextMap.get(SYSTEM_LISTENERS);
	}
	
}
