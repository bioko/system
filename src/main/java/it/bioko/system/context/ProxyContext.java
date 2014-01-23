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

package it.bioko.system.context;

import it.bioko.system.command.AbstractCommandHandler;
import it.bioko.system.event.SystemListener;
import it.bioko.system.repository.core.Repository;
import it.bioko.utils.domain.DomainEntity;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

public class ProxyContext extends Context {

	private Context _context;

	public ProxyContext(Context context) {
		_context = context;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String name) {
		return (T) _context.get(name);
	}

	@Override
	public void put(String name, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Logger getLogger() {
		return _context.getLogger();
	}

	@Override
	public String getSystemName() {
		return _context.getSystemName();
	}

	@Override
	public void addRepository(String repoName, Repository<?> repo) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public <DE extends DomainEntity> Repository<DE> getRepository(String repoName) {
		return _context.getRepository(repoName);
	}

	@Override
	public void setCommandHandler(AbstractCommandHandler commandHandler) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public AbstractCommandHandler getCommandHandler() {
		return _context.getCommandHandler();
	}
	
	@Override 
	public void setSystemProperty(String name, String value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getSystemProperty(String name) {
		return _context.getSystemProperty(name);
	}

	@Override
	public void addSystemListener(SystemListener listener) {
		_context.addSystemListener(listener);
	}
	
	@Override
	public List<SystemListener> getSystemListeners() {
		return Collections.unmodifiableList(_context.getSystemListeners());
	}
	
}
