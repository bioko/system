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

package org.biokoframework.system.repository.memory;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.biokoframework.system.repository.memory.manytypes.ManyTypesEntity;
import org.biokoframework.system.repository.memory.manytypes.ManyTypesEntityBuilder;
import org.biokoframework.system.services.entity.EntityModule;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.utils.domain.EntityBuilder;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.validation.ValidationModule;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 5, 2014
 *
 */
public class ManyTypesTest {
	
	private Injector fInjector;

	@Before
	public void prepareInjector() {
		fInjector = Guice.createInjector(new ValidationModule(), new EntityModule());
	}
	
	@Test
	public void integerTypes() throws Exception {
		Repository<ManyTypesEntity> repository = new InMemoryRepository<ManyTypesEntity>(ManyTypesEntity.class, fInjector.getInstance(IEntityBuilderService.class));

		EntityBuilder<ManyTypesEntity> entityBuilder = fInjector.getInstance(ManyTypesEntityBuilder.class).loadDefaultExample();
		
		ManyTypesEntity entity1 = entityBuilder.build(false);
		entity1 = repository.save(entity1);
		assertThat(entity1, is(equalTo(entityBuilder.build(true))));
		
		ManyTypesEntity entity2 = entityBuilder.loadExample(ManyTypesEntityBuilder.EXAMPLE_2).build(false);
		entity2 = repository.save(entity2);
		assertThat(entity2, is(equalTo(entityBuilder.build(true))));
		
		assertThat(repository.retrieve("1"), is(equalTo(entity1)));
		assertThat(repository.retrieve("2"), is(equalTo(entity2)));
		assertThat(repository.getAll(), contains(entity1, entity2));
		
	}

}
