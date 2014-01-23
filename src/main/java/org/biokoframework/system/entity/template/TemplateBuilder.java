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

package org.biokoframework.system.entity.template;

import org.biokoframework.utils.domain.EntityBuilder;


public class TemplateBuilder extends EntityBuilder<Template> {

	public static String DEFAULT_ID = "555";
	public static String DEFAULT_TEMPLATE_TITLE = "";
	public static String DEFAULT_TEMPLATE_HTML = "";
	public static String _templateTitle = DEFAULT_TEMPLATE_TITLE;
	public static String _templateEmailTrackId = "1";
	public static String _templateHtml = DEFAULT_TEMPLATE_HTML;

	public static final String EXAMPLE = "example";
	private static final String EXAMPLE_JSON = "{'id':'555','title':'','track':'1','body':''}";
	
	public static final String SYSTEM_A_TEMPLATE = "systemAtemplate";

	public TemplateBuilder() {
		super(Template.class);
		putExample(EXAMPLE, EXAMPLE_JSON);
		putExample(SYSTEM_A_TEMPLATE, "{'track':'passwordResetMailTemplate','title':'Password reset','body':'Abbiamo ricevuto una richiesta di reset della password. Clicca <a href=\\\"http://local.engaged.it/password-reset&token=${token}\\\">qui</a> per resettare la tua password'}");
	}

	@Override
	public EntityBuilder<Template> loadDefaultExample() {
	return loadExample(EXAMPLE);
	}
}
