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

package org.biokoframework.system.services.repository;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.repository.population.IRepositoryPopulator;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.injection.ServiceModule;
import org.biokoframework.system.services.repository.impl.JitRepositoryService;
import org.biokoframework.utils.repository.Repository;

import java.util.Collections;
import java.util.Set;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 6, 2014
 *
 */
public abstract class RepositoryModule extends ServiceModule {

	public RepositoryModule(ConfigurationEnum config) {
		super(config);
	}

	@Override
	protected final void configure() {
		super.configure();
		
		bind(IRepositoryService.class)
			.to(JitRepositoryService.class);

        populateRepository();
	}

    @SuppressWarnings("rawtypes")
	protected void bindRepositoryTo(Class<? extends Repository> repositoryClass) {
		bind(new TypeLiteral<Class>(){}).annotatedWith(Names.named("repository")).toInstance(repositoryClass);
	}

    /**
     * Override this method if you are interested in populating the repository.<br/>
     * <b>Do not call {@code super.populateRepository()}</b>
     *
     * @see  #populateRepositoryWith(Class<? extends IRepositoryPopulator)
     */
    protected void populateRepository() {
        bind(new TypeLiteral<Set<IRepositoryPopulator>>() {}).toInstance(Collections.<IRepositoryPopulator>emptySet());
    }

    protected void populateRepositoryWith(Class<? extends IRepositoryPopulator> populator) {
        Multibinder<IRepositoryPopulator> repositoryPopulatorBinder = Multibinder.newSetBinder(binder(), IRepositoryPopulator.class);
        repositoryPopulatorBinder.addBinding().to(populator);
    }

}
