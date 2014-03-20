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

package org.biokoframework.system.repository.memory.manytypes;

import com.google.inject.Injector;
import org.biokoframework.utils.domain.EntityBuilder;

import javax.inject.Inject;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 5, 2014
 *
 */
public class ManyTypesEntityBuilder extends EntityBuilder<ManyTypesEntity> {

	public static final String EXAMPLE_1 = "example1";
	public static final String EXAMPLE_2 = "example2";

	@Inject
	public ManyTypesEntityBuilder(Injector injector) {
		super(ManyTypesEntity.class, injector);
		
		putExample(EXAMPLE_1, "{'id':'1','intField':12, 'longField':30}");
		putExample(EXAMPLE_2, "{'id':'2','intField':24, 'longField':60}");
	}

	@Override
	public EntityBuilder<ManyTypesEntity> loadDefaultExample() {
		return loadExample(EXAMPLE_1);
	}

}
