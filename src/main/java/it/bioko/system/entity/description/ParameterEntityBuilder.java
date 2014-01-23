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

package it.bioko.system.entity.description;

import it.bioko.utils.domain.EntityBuilder;



public class ParameterEntityBuilder extends EntityBuilder<ParameterEntity> {
	
	public static final String EXAMPLE = "example";
	private static final String EXAMPLE_JSON = "{'name':'the_parameter'}";

	public ParameterEntityBuilder() {
		super(ParameterEntity.class);
		putExample(EXAMPLE, EXAMPLE_JSON);
	}

	@Override
	public ParameterEntity build(boolean exists) {
		if (exists) {
			throw new UnsupportedOperationException(
					"Command entities are not supposed to be stored in repositories"
			);
		} else { 
			return super.build(exists);
		}
	}

	@Override
	public EntityBuilder<ParameterEntity> loadDefaultExample() {
		return loadExample(EXAMPLE);
	}

}
