package it.bioko.system.repository.memory.dummy1;

import it.bioko.utils.domain.EntityBuilder;


public class ASDummyEntity1Builder extends EntityBuilder<ASDummyEntity1> {

	public static final String EXAMPLE_GINO = "exampleGino";
	public static final String EXAMPLE_PINO = "examplePino";
	public static final String EXAMPLE_RINO = "exampleRino";
	public static final String EXAMPLE_DINO = "exampleDino";

	public ASDummyEntity1Builder() {
		super(ASDummyEntity1.class);

		putExample(EXAMPLE_GINO, "{'id':'1','value':'gino','entityGroup':'gruppo1'}");
		putExample(EXAMPLE_PINO, "{'id':'2','value':'pino','entityGroup':'gruppo1'}");
		putExample(EXAMPLE_RINO, "{'id':'3','value':'rino','entityGroup':'gruppo2'}");
		putExample(EXAMPLE_DINO, "{'id':'4','value':'dino','entityGroup':'gruppo2'}");
	}

	@Override
	public EntityBuilder<ASDummyEntity1> loadDefaultExample() {
		return loadExample(EXAMPLE_GINO);
	}

}
