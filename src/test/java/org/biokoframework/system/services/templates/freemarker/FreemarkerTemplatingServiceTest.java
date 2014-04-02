/*
 * Copyright (c) $year.
 * 	Mikol Faro		<mikol.faro@gmail.com>
 * 	Simone Mangano	 	<simone.mangano@ieee.org>
 * 	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
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
 */

package org.biokoframework.system.services.templates.freemarker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.biokoframework.system.entity.template.Template;
import org.biokoframework.system.services.entity.EntityModule;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.system.services.templates.TemplatingException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.validation.ValidationModule;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.biokoframework.utils.matcher.Matchers.has;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014 Apr 01
 */
public class FreemarkerTemplatingServiceTest {

    private Injector fInjector;

    @Before
    public void createInjector() {
        fInjector = Guice.createInjector(new EntityModule(), new ValidationModule());
    }

    @Test
    public void simpleTest() throws TemplatingException {
        Template template = new Template();
        template.setAll(new Fields(Template.BODY, "${b}+${c}=3"));

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("b", "1");
        parameters.put("c", "2");

        FreemarkerTemplatingService service = new FreemarkerTemplatingService(fInjector.getInstance(IEntityBuilderService.class));

        Template resultTemplate = service.compileTemplate(template, parameters);

        assertThat(resultTemplate, has(Template.BODY, equalTo("1+2=3")));

    }

}
