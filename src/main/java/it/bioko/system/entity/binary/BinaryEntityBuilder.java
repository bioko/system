package it.bioko.system.entity.binary;

import it.bioko.utils.domain.EntityBuilder;


public class BinaryEntityBuilder extends EntityBuilder<BinaryEntity> {

	public static final String UNNAMED_PNG = "unnamedPng";
	private static final String UNNAMED_PNG_JSON = 
			"{'id':'1','sizeBytes':'0','mediaType':'image/png'}";
	
	public static final String PNG = "png";
	private static final String PNG_JSON = 
			"{'id':'1','name':'prova.png','sizeBytes':'0','mediaType':'image/png'}";

	public BinaryEntityBuilder() {
		super(BinaryEntity.class);

		putExample(UNNAMED_PNG, UNNAMED_PNG_JSON);
		putExample(PNG, PNG_JSON);
	}

	@Override
	public EntityBuilder<BinaryEntity> loadDefaultExample() {
		return loadExample(UNNAMED_PNG);
	}

}
