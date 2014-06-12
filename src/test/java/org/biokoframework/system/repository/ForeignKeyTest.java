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

package org.biokoframework.system.repository;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.entity.dummy1.DummyEntity1;
import org.biokoframework.system.entity.dummy1.DummyEntity1Builder;
import org.biokoframework.system.entity.dummy2.DummyEntity2;
import org.biokoframework.system.entity.dummy2.DummyEntity2Builder;
import org.biokoframework.system.repository.memory.InMemoryRepository;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.entity.EntityModule;
import org.biokoframework.system.services.repository.RepositoryModule;
import org.biokoframework.utils.domain.EntityBuilder;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.repository.RepositoryException;
import org.biokoframework.utils.validation.ValidationModule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-06-11
 */
public class ForeignKeyTest {

    private InMemoryRepository<DummyEntity1> fRepo1;
    private InMemoryRepository<DummyEntity2> fRepo2;

    private EntityBuilder<DummyEntity2> fDummy2Builder;

    @Before
    public void createRepos() throws RepositoryException, ValidationException {
        Injector injector = Guice.createInjector(
                new EntityModule(),
                new ValidationModule(),
                new RepositoryModule(ConfigurationEnum.DEV) {
                    @Override
                    protected void configureForDev() {
                        bindRepositoryTo(InMemoryRepository.class);
                    }
                    @Override
                    protected void configureForDemo() {
                        configureForDev();
                    }
                    @Override
                    protected void configureForProd() {
                        configureForDev();
                    }
                }
                );

        IRepositoryService repositoryService = injector.getInstance(IRepositoryService.class);

        fRepo1 = repositoryService.getRepository(DummyEntity1.class);
        fRepo2 = repositoryService.getRepository(DummyEntity2.class);

        EntityBuilder<DummyEntity1> dummy1Builder = injector.getInstance(DummyEntity1Builder.class);
        dummy1Builder.loadDefaultExample();
        fRepo1.save(dummy1Builder.build(false));

        fRepo1.save(dummy1Builder.build(false));

        fDummy2Builder = injector.getInstance(DummyEntity2Builder.class);
        fDummy2Builder.loadDefaultExample();
    }

    @Test
    public void insertWithForeignKey() throws ValidationException, RepositoryException {

        fRepo2.save(fDummy2Builder.build(false));

    }

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void insertWithBrokenForeignKey() throws ValidationException, RepositoryException {
        expected.expect(ValidationException.class);

        fDummy2Builder.set(DummyEntity2.ENTITY1_ID, "3");

        fRepo2.save(fDummy2Builder.build(false));
    }

    @Test
    public void updateWithForeignKey() throws ValidationException, RepositoryException {

        fRepo2.save(fDummy2Builder.build(false));

        fDummy2Builder.set(DummyEntity2.ENTITY1_ID, "2");

        fRepo2.save(fDummy2Builder.build(false));

    }

    @Test
    public void updateWithBrokenForeignKey() throws ValidationException, RepositoryException {
        expected.expect(ValidationException.class);

        fRepo2.save(fDummy2Builder.build(false));

        fDummy2Builder.set(DummyEntity2.ENTITY1_ID, "3");

        fRepo2.save(fDummy2Builder.build(false));
    }


}
