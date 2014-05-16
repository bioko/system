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

package org.biokoframework.system.entity.dummyWithDate;

import com.google.inject.Injector;
import org.biokoframework.utils.domain.EntityBuilder;

import javax.inject.Inject;

public class DummyEntityWithDateBuilder extends EntityBuilder<DummyEntityWithDate> {

    public static final String EXAMPLE1 = "example1";
    public static final String EXAMPLE2 = "example2";
    public static final String EXAMPLE3 = "example3";
    public static final String EXAMPLE4 = "example4";
    public static final String EXAMPLE5 = "example5";

    @Inject
    public DummyEntityWithDateBuilder(Injector injector) {
        super(DummyEntityWithDate.class, injector);

        putExample(EXAMPLE1, "{ 'id':'1', 'value':'2013-11-20T10:00:00+0100' } ");
        putExample(EXAMPLE2, "{ 'id':'2', 'value':'2013-11-20T10:05:00+0100' } ");
        putExample(EXAMPLE3, "{ 'id':'3', 'value':'2013-11-20T10:10:00+0100' } ");
        putExample(EXAMPLE4, "{ 'id':'4', 'value':'2013-11-21T10:00:00+0100' } ");
        putExample(EXAMPLE5, "{ 'id':'5', 'value':'2013-11-22T08:00:00+0100' } ");


    }

    @Override
    public EntityBuilder<DummyEntityWithDate> loadDefaultExample() {
        loadExample(EXAMPLE1);
        return this;
    }

}
