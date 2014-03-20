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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import org.apache.commons.io.FileUtils;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.entity.authentication.Authentication;
import org.biokoframework.system.entity.binary.BinaryEntity;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.factory.binary.BinaryEntityRepository;
import org.biokoframework.system.repository.memory.InMemoryRepository;
import org.biokoframework.system.services.entity.EntityModule;
import org.biokoframework.system.services.repository.RepositoryModule;
import org.biokoframework.utils.repository.Repository;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Mar 6, 2014
 *
 */
public class JitRepositoryServiceTest {

	@Test
	public void jitRepositoryTest() {
		Injector injector = Guice.createInjector(new DynamicModule(), new EntityModule());
		JitRepositoryService service = new JitRepositoryService(InMemoryRepository.class, injector);
		
		Repository<Login> loginRepo = service.getRepository(Login.class);
		assertThat(loginRepo, is(instanceOf(InMemoryRepository.class)));
		
		Repository<Authentication> authRepo = service.getRepository(Authentication.class);
		assertThat(authRepo, is(instanceOf(InMemoryRepository.class)));
		
		Repository<BinaryEntity> binaryRepo = service.getRepository(BinaryEntity.class);
		assertThat(binaryRepo, is(instanceOf(BinaryEntityRepository.class)));
	}

	private static final class DynamicModule extends RepositoryModule {
		public DynamicModule() {
			super(ConfigurationEnum.DEV);
		}

		@Override
		protected void configureForDev() {
			bind(File.class).annotatedWith(Names.named("fileBaseDirectory")).toInstance(FileUtils.getTempDirectory());
			bindRepositoryTo(InMemoryRepository.class);			
		}
		@Override
		protected void configureForDemo() {
		}
		@Override
		protected void configureForProd() {
		}
	}
}
