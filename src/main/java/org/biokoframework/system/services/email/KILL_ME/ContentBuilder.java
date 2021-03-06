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

package org.biokoframework.system.services.email.KILL_ME;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.template.Template;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

@Deprecated
public class ContentBuilder {

	private Template _template;
	private Map<String, Object> _contentMap;

	public ContentBuilder(Template template, Map<String, Object> contentMap) {
		_template = template;
		_contentMap = contentMap;
	}

	public String buildBody() throws CommandException {
		return build(_template.get(Template.BODY).toString());
	}
	
	public String buildTitle() throws CommandException {
		return build(_template.get(Template.TITLE).toString());
	}

	private String build(String template) throws CommandException {
		// Freemarker
		StringReader stringReader = new StringReader(template);
		StringWriter stringWriter = new StringWriter();
		
		Configuration config = new Configuration();

		freemarker.template.Template freemarkerTemplate;
		try {
			freemarkerTemplate = new freemarker.template.Template("", stringReader, config);
			freemarkerTemplate.process(_contentMap, stringWriter);
		} catch (IOException exception) {
			throw CommandExceptionsFactory.createContainerException(exception);
		} catch (TemplateException exception) {
			throw CommandExceptionsFactory.createContainerException(exception);
		}
		
		return stringWriter.toString();
	}

}
