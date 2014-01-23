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

package it.bioko.system.service.description.dummy;

import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.domain.annotation.field.VirtualField;
import it.bioko.utils.domain.annotation.hint.Hint;
import it.bioko.utils.fields.Fields;

@SuppressWarnings("serial")
public class DummyEntityWithLocation extends DomainEntity {

	@Field
	public static final String A_FIELD = "aField";
	
	@Field(type = Double.class, hints = {
		@Hint(name = "cmsType", value = "hidden")
	})
	public static final String LATITUDE = "latitude";
	
	@Field(type = Double.class, hints = {
		@Hint(name = "cmsType", value = "hidden")
	})
	public static final String LONGITUDE = "longitude";
	
	@VirtualField(hints={
		@Hint(name = "latitudeField", value = LATITUDE),
		@Hint(name = "longitudeField", value = LONGITUDE),
		@Hint(name = "cmsType", value = "location")
	})
	public static final String LOCATION = "location";
	
	public DummyEntityWithLocation(Fields input) {
		super(input);
	}
	
}
