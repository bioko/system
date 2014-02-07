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

package org.biokoframework.system.services;

import org.biokoframework.system.repository.service.RepositoryService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.reflection.DummyParameterizedType;
import org.biokoframework.utils.repository.Repository;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 6, 2014
 *
 */
public class DefaultRepositoryService implements RepositoryService {

	private final Injector fInjector;

	@Inject
	public DefaultRepositoryService(Injector injector) {
		fInjector = injector;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <DE extends DomainEntity, R extends Repository<DE>> R getRepository(Class<DE> entityClass) {
		Key<R> key = (Key<R>) Key.get(new DummyParameterizedType(Repository.class, entityClass));
		
		return fInjector.getInstance(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <DE extends DomainEntity, R extends Repository<DE>> R getRepository(Class<DE> entityClass, String name) {
		Key<R> key = (Key<R>) Key.get(new DummyParameterizedType(Repository.class, entityClass), Names.named(name));
		
		return fInjector.getInstance(key);
	}
	
}
