package it.bioko.system.service.description;

import it.bioko.system.command.crud.annotation.CrudCommand;
import it.bioko.system.context.Context;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.ComponingFieldsFactory;
import it.bioko.utils.domain.annotation.field.VirtualField;
import it.bioko.utils.domain.annotation.field.VirtualFieldsFactory;
import it.bioko.utils.domain.annotation.hint.Hint;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;


public class JsonSystemDescriptor {
	
	public JsonSystemDescription describeSystem(Context context) {
		JsonSystemDescription description = new JsonSystemDescription();

		for (Class<? extends DomainEntity> anEntityClass : findEntities((Class<?>)context.get(Context.COMMANDS_CLASS))) {
			description.addEntity(anEntityClass.getSimpleName(), describeEntity(anEntityClass));
		}
		
		return description;
	}

	private List<Class<? extends DomainEntity>> findEntities(Class<?> commandsClass) {
		List<Class<? extends DomainEntity>> entityClasses = new LinkedList<Class<? extends DomainEntity>>();
		for (Field aField : commandsClass.getFields()) {
			if (aField.isAnnotationPresent(CrudCommand.class)) {
				entityClasses.add(aField.getAnnotation(CrudCommand.class).entity());
			}
		}
		return entityClasses;
	}

	public JsonEntityDescription describeEntity(Class<? extends DomainEntity> entityClass) {
		JsonEntityDescription description = new JsonEntityDescription();

		description.setEntityName(entityClass.getSimpleName());

		try {
			description.addField(DomainEntity.ID, describeId());
			for (Entry<String, it.bioko.utils.domain.annotation.field.Field> anEntry : ComponingFieldsFactory.createWithAnnotation(entityClass).entrySet()) {
				description.addField(anEntry.getKey(), describeField(anEntry.getValue()));
			}
			
			for (Entry<String, VirtualField> anEntry : VirtualFieldsFactory.createWithAnnotation(entityClass).entrySet()) {
				description.addField(anEntry.getKey(), describeField(anEntry.getValue()));
			}
		} catch (IllegalAccessException exception) {
			System.err.println("[easymen]: this should not happen");
			exception.printStackTrace();
		}
		
		return description;
	}

	private JsonFieldDescription describeField(VirtualField fieldAnnotation) {
		JsonFieldDescription fieldDescription = new JsonFieldDescription();
		fieldDescription.setVirtual(true);
		for(Hint anHint : fieldAnnotation.hints()) {
			fieldDescription.addHint(anHint.name(), anHint.value());
		}
		return fieldDescription;
	}

	private JsonFieldDescription describeField(it.bioko.utils.domain.annotation.field.Field fieldAnnotation) {
		JsonFieldDescription fieldDescription = new JsonFieldDescription();
		
		if (DomainEntity.class.isAssignableFrom(fieldAnnotation.type())) {
			fieldDescription.setType(Integer.class.getSimpleName());
			fieldDescription.setReference(fieldAnnotation.type().getSimpleName(), DomainEntity.ID);
		} else {
			fieldDescription.setType(fieldAnnotation.type().getSimpleName());
		}
		
		fieldDescription.setMandatory(fieldAnnotation.mandatory());
		for(Hint anHint : fieldAnnotation.hints()) {
			fieldDescription.addHint(anHint.name(), anHint.value());
		}
		return fieldDescription;
	}

	private JsonFieldDescription describeId() {
		JsonFieldDescription fieldDescription = new JsonFieldDescription();
		fieldDescription.setType(Integer.class.getSimpleName());
		return fieldDescription;
	}
}
