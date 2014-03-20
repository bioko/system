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

package org.biokoframework.system.services.repository.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import org.biokoframework.system.entity.binary.BinaryEntity;
import org.biokoframework.system.factory.binary.BinaryEntityRepository;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.reflection.DummyParameterizedType;
import org.biokoframework.utils.repository.Repository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Mar 6, 2014
 *
 */
@Singleton
public class JitRepositoryService implements IRepositoryService {

	private final Injector fInjector;
	private final Class<?> fRepoClass;
	
	private Map<Class<?>, Repository<? extends DomainEntity>> fRepos = new LinkedHashMap<>();

	@SuppressWarnings("rawtypes")
	@Inject
	public JitRepositoryService(@Named("repository") Class repoClass, Injector injector) {
		fInjector = injector;
		fRepoClass = repoClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <DE extends DomainEntity, R extends Repository<DE>> R getRepository(final Class<DE> entityClass) {
		if (fRepos.containsKey(entityClass)) {
			return (R) fRepos.get(entityClass);
		}
		
		
		Injector child = fInjector.createChildInjector(new AbstractModule() {
			@SuppressWarnings("rawtypes")
			@Override
			protected void configure() {
				bind(new TypeLiteral<Class>(){}).toInstance(entityClass);
			}
		});
		
		final R repo = (R) child.getInstance(fRepoClass);

		if (BinaryEntity.class.isAssignableFrom(entityClass)) {
			child = child.createChildInjector(new AbstractModule() {
				@Override
				protected void configure() {
					((AnnotatedBindingBuilder<Repository<DE>>) bind(Key.get(new DummyParameterizedType(Repository.class, entityClass)))).toInstance(repo);
				}
			});
			R binaryRepo = (R) child.getInstance(BinaryEntityRepository.class);
			fRepos.put(entityClass, binaryRepo);
			return binaryRepo;
		}
				
		fRepos.put(entityClass, repo);
		return repo;
	}

	@Override
	public <DE extends DomainEntity, R extends Repository<DE>> R getRepository(Class<DE> entityClass, String name) {
		return null;
	}

}
