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
