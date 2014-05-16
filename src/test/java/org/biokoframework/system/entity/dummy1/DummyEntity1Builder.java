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
package org.biokoframework.system.entity.dummy1;

import com.google.inject.Injector;
import org.biokoframework.utils.domain.EntityBuilder;

import javax.inject.Inject;

public class DummyEntity1Builder extends EntityBuilder<DummyEntity1> {

    public static final String EXAMPLE1 = "example1";
    public static final String EXAMPLE2 = "example2";
    public static final String EXAMPLE3 = "example3";

    public DummyEntity1Builder() {
        this(null);
    }

    @Inject
    public DummyEntity1Builder(Injector injector) {
        super(DummyEntity1.class, injector);

        putExample(EXAMPLE1, "{'id':'1','value':'pino1'}");
        putExample(EXAMPLE2, "{'id':'2','value':'Pino1'}");
        putExample(EXAMPLE3, "{'id':'3','value':'PINO1'}");
    }

    @Override
    public EntityBuilder<DummyEntity1> loadDefaultExample() {
        return loadExample(EXAMPLE1);
    }

}
