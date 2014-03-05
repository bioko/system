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

import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.biokoframework.system.repository.memory.dummy1.ASDummyEntity1;
import org.biokoframework.system.repository.memory.dummy1.ASDummyEntity1Builder;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.repository.RepositoryException;
import org.biokoframework.utils.validation.ValidationModule;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;


public class MemoryRepositoryTest {

	private InMemoryRepository<ASDummyEntity1> fRepo;
	private Injector fInjector;

	@Before
	public void fillExampleRepo() throws ValidationException, RepositoryException {
		fInjector = Guice.createInjector(new ValidationModule());
		
		fRepo = new InMemoryRepository<ASDummyEntity1>(ASDummyEntity1.class);
		ASDummyEntity1Builder builder = fInjector.getInstance(ASDummyEntity1Builder.class);

		fRepo.save(builder.loadExample(ASDummyEntity1Builder.EXAMPLE_GINO).build(false));
		fRepo.save(builder.loadExample(ASDummyEntity1Builder.EXAMPLE_PINO).build(false));
		fRepo.save(builder.loadExample(ASDummyEntity1Builder.EXAMPLE_RINO).build(false));
		fRepo.save(builder.loadExample(ASDummyEntity1Builder.EXAMPLE_DINO).build(false));

	}
	
	@Test
	public void retriveByForeignKeyTest() throws ValidationException, RepositoryException {
		ASDummyEntity1Builder builder = fInjector.getInstance(ASDummyEntity1Builder.class);

		// case sensitive correct case
		ASDummyEntity1 ginoEntity = fRepo.retrieveByForeignKey(ASDummyEntity1.VALUE, "gino");
		assertEquals(builder.loadExample(ASDummyEntity1Builder.EXAMPLE_GINO).build(true).get(ASDummyEntity1.ID), ginoEntity.get(ASDummyEntity1.ID));
		// case sensitive, with wrong case
		ASDummyEntity1 ginoEntity2 = fRepo.retrieveByForeignKey(ASDummyEntity1.VALUE, "Gino");
		assertEquals(null, ginoEntity2);
//		// case insensitive, with correct case
//		asdummyentity1 ginoentity3 = repo.retrievebyforeignkey(asdummyentity1.value, "gino", true);
//		assertequals(builder.loadexample(asdummyentity1builder.example_gino).build(true).get(asdummyentity1.id), ginoentity3.get(asdummyentity1.id));
//		// case insensitive, with wrong case
//		asdummyentity1 ginoentity4 = repo.retrievebyforeignkey(asdummyentity1.value, "gino", true);
//		assertequals(builder.loadexample(asdummyentity1builder.example_gino).build(true).get(asdummyentity1.id), ginoentity4.get(asdummyentity1.id));

	}


	@Test
	public void getEntitiesByForeignKeyTest()  {

		try {
			ASDummyEntity1Builder builder = fInjector.getInstance(ASDummyEntity1Builder.class);

			// case sensitive correct case
			List<ASDummyEntity1> gruppo1Entities = fRepo.getEntitiesByForeignKey(ASDummyEntity1.GROUP, "gruppo1");
			
			System.out.println(JSONValue.toJSONString(gruppo1Entities));
			System.out.println("[");
			System.out.println(builder.loadExample(ASDummyEntity1Builder.EXAMPLE_GINO).build(true).toJSONString());
			System.out.println(builder.loadExample(ASDummyEntity1Builder.EXAMPLE_PINO).build(true).toJSONString());
			System.out.println("]");
			
			
			assertThat(gruppo1Entities.toArray(new ASDummyEntity1[0]), arrayContainingInAnyOrder(
						builder.loadExample(ASDummyEntity1Builder.EXAMPLE_GINO).build(true),
						builder.loadExample(ASDummyEntity1Builder.EXAMPLE_PINO).build(true)
					));


			// case sensitive, with wrong case
			List<ASDummyEntity1> gruppo1Entities2 = fRepo.getEntitiesByForeignKey(ASDummyEntity1.GROUP, "Gruppo1");
			assertEquals(0, gruppo1Entities2.size());

			// case insensitive, with correct case
			List<ASDummyEntity1> gruppo1Entities3 = fRepo.getEntitiesByForeignKey(ASDummyEntity1.GROUP, "gruppo1");
			assertThat(gruppo1Entities3.toArray(new ASDummyEntity1[0]), arrayContainingInAnyOrder(
					builder.loadExample(ASDummyEntity1Builder.EXAMPLE_GINO).build(true),
					builder.loadExample(ASDummyEntity1Builder.EXAMPLE_PINO).build(true)
					));

			// case insensitive, with wrong case
//			List<ASDummyEntity1> gruppo1Entities4 = repo.getEntitiesByForeignKey(ASDummyEntity1.GROUP, "Gruppo1", true);
//			assertThat(gruppo1Entities4.toArray(new ASDummyEntity1[0]), arrayContainingInAnyOrder(
//					builder.loadExample(ASDummyEntity1Builder.EXAMPLE_GINO).build(true),
//					builder.loadExample(ASDummyEntity1Builder.EXAMPLE_PINO).build(true)
//					));


		} catch (Exception e) {
			if (e instanceof ValidationException) {
				System.err.println(">>> validation ex >>>");
				ValidationException ve = (ValidationException) e;
				System.err.println(JSONValue.toJSONString(ve.getErrors()));
				
			} else {
				e.printStackTrace();			
			}
			 
		}


		//		
		//		assertEquals(builder.loadExample(ASDummyEntity1Builder.EXAMPLE_GINO).build(true).get(ASDummyEntity1.ID), ginoEntity.get(ASDummyEntity1.ID));
		//		// case sensitive, with wrong case
		//		ASDummyEntity1 ginoEntity2 = repo.retrieveByForeignKey(ASDummyEntity1.VALUE, "Gino");
		//		assertEquals(null, ginoEntity2);
		//		// case insensitive, with correct case
		//		ASDummyEntity1 ginoEntity3 = repo.retrieveByForeignKey(ASDummyEntity1.VALUE, "gino", true);
		//		assertEquals(builder.loadExample(ASDummyEntity1Builder.EXAMPLE_GINO).build(true).get(ASDummyEntity1.ID), ginoEntity3.get(ASDummyEntity1.ID));
		//		// ,case insensitive, with wrong case
		//		ASDummyEntity1 ginoEntity4 = repo.retrieveByForeignKey(ASDummyEntity1.VALUE, "Gino", true);
		//		assertEquals(builder.loadExample(ASDummyEntity1Builder.EXAMPLE_GINO).build(true).get(ASDummyEntity1.ID), ginoEntity4.get(ASDummyEntity1.ID));
	}
	
}
