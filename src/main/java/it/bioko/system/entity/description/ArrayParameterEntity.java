package it.bioko.system.entity.description;
//package it.engaged.generic.server.core.meta;
//
//import it.engaged.generic.commons.GenericFieldNames;
//import it.engaged.utils.fields.FieldNames;
//import it.engaged.utils.fields.Fields;
//
//import java.util.ArrayList;
//
//@SuppressWarnings("serial")
//public class ArrayParameterEntity extends ParameterEntity {
//
//	public ArrayParameterEntity(Fields input) {
//		super(input, componingKeys());
//	}
//	
//	public ArrayParameterEntity(Fields input, ArrayList<String> componingFieldsKeys) {
//		super(input, componingFieldsKeys);
//		// TODO Auto-generated constructor stub
//	}
//	
//	public static ArrayList<String> componingKeys() {
//		ArrayList<String> componingFields = new ArrayList<String>();
//		componingFields.add(FieldNames.NAME);
//		componingFields.add(GenericFieldNames.MINIMUM);
//		componingFields.add(GenericFieldNames.MAXIMUM);
//		componingFields.add(GenericFieldNames.TYPE);
//		return componingFields;
//	}
//	
//	@Override
//	public String report() {
//		return new StringBuilder(this.getClass().getSimpleName()).append(" ").append(super.report()).toString();
//	}
//	
//	public static final String ENTITY_KEY = GenericFieldNames.NOT_EXPECTED_ID;
//	// TODO rinominare in entityIdKey
//	@Override
//	public String keyName() {
//		return ENTITY_KEY;
//	}
//	
//}
