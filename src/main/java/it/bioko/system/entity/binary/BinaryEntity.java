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

package it.bioko.system.entity.binary;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.fields.Fields;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

@SuppressWarnings("serial")
public class BinaryEntity extends DomainEntity {

	private static final String MEDIA_TYPE_REGEXP = "^[a-z]+\\/[\\S]+$";
	
	public static final String ENTITY_KEY = "blobId";
	
	@Field(mandatory = false)
	public static final String NAME = GenericFieldNames.NAME;
	
	@Field(type = Long.class)
	public static final String SIZE_BYTES = "sizeBytes";	
	
	@Field(pattern = MEDIA_TYPE_REGEXP)
	public static final String MEDIA_TYPE = "mediaType";
	
	@Field(mandatory = false)
	public static final String PATH = "path";

	private byte[] _bytes;

	public BinaryEntity(Fields input) {
		super(input);
	}
	
	public InputStream getStream() {
		return new ByteArrayInputStream(_bytes);
	}
	
	public void setStream(InputStream stream) throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		IOUtils.copy(stream, outStream);
		_bytes = outStream.toByteArray();
		stream.close();
	}
	
}
