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

package org.biokoframework.system.service.description;

import org.biokoframework.system.command.crud.annotation.CrudCommand;
import org.biokoframework.system.context.Context;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.annotation.field.ComponingFieldsFactory;
import org.biokoframework.utils.domain.annotation.field.VirtualField;
import org.biokoframework.utils.domain.annotation.field.VirtualFieldsFactory;
import org.biokoframework.utils.domain.annotation.hint.Hint;

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
			for (Entry<String, org.biokoframework.utils.domain.annotation.field.Field> anEntry : ComponingFieldsFactory.createWithAnnotation(entityClass).entrySet()) {
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

	private JsonFieldDescription describeField(org.biokoframework.utils.domain.annotation.field.Field fieldAnnotation) {
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
