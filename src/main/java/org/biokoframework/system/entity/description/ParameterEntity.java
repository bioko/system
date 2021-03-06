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

package org.biokoframework.system.entity.description;

import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.annotation.field.Field;
import org.biokoframework.utils.fields.FieldNames;


public class ParameterEntity extends DomainEntity {

	private static final long serialVersionUID = 6955754958001873949L;

	@Field
	public static final String NAME = FieldNames.NAME;
	
	@Field(mandatory = false)
	public static final String DEFAULT = GenericFieldNames.DEFAULT;
	
	// This parameter is composed by the content
	@Field(mandatory = false)
	public static final String CONTENT = "content";
	
	// The parameter is expected in a specific http part
	@Field(mandatory = false)
	public static final String HTTP_PARAMETER_TYPE = GenericFieldNames.HTTP_PARAMETER_TYPE;
	public static final String HTTP_BODY           = GenericFieldValues.BODY;
	public static final String HTTP_QUERY_STRING   = GenericFieldValues.QUERY_STRING;
	public static final String HTTP_HEADER         = GenericFieldValues.HEADER;
	public static final String HTTP_URL_PATH       = GenericFieldValues.URL_PATH;

	// This parameter is an array
	@Field(mandatory = false, type = Integer.class)
	public static final String MINIMUM = GenericFieldNames.MINIMUM;
	@Field(mandatory = false, type = Integer.class)
	public static final String MAXIMUM = GenericFieldNames.MAXIMUM;

	public static final String ENTITY_KEY = GenericFieldNames.NOT_EXPECTED_ID;
	
}
