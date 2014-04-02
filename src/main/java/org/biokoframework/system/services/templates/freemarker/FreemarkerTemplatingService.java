/*
 * Copyright (c) 2014.
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

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.apache.log4j.Logger;
import org.biokoframework.system.entity.template.Template;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.system.services.templates.ITemplatingService;
import org.biokoframework.system.services.templates.TemplatingException;

import javax.inject.Inject;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014 Apr 01
 */
public class FreemarkerTemplatingService implements ITemplatingService {

    private static final Logger LOGGER = Logger.getLogger(FreemarkerTemplatingService.class);

    private final IEntityBuilderService fEntityBuilder;

    @Inject
    public FreemarkerTemplatingService(IEntityBuilderService entityBuilder) {
        fEntityBuilder = entityBuilder;
    }

    @Override
    public Template compileTemplate(Template template, Map<String, Object> values) throws TemplatingException {
        Template compiled = fEntityBuilder.getInstance(Template.class);

        String title = template.get(Template.TITLE);
        if (title != null) {
            compiled.set(Template.TITLE, compile(title, values));
        }

        String body = template.get(Template.BODY);
        if (body != null) {
            compiled.set(Template.BODY, compile(body, values));
        }

        return compiled;
    }

    private String compile(String template, Map<String, Object> content) throws TemplatingException {
        StringReader stringReader = new StringReader(template);
        StringWriter stringWriter = new StringWriter();

        Configuration config = new Configuration();

        freemarker.template.Template freemarkerTemplate;
        try {
            freemarkerTemplate = new freemarker.template.Template("", stringReader, config);
            freemarkerTemplate.process(content, stringWriter);
        } catch (IOException|TemplateException exception) {
            LOGGER.error("Cannot compile template", exception);
            throw new TemplatingException(exception);
        }

        return stringWriter.toString();
    }

}
