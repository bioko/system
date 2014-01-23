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

package org.biokoframework.system.entity.resolution;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.biokoframework.system.command.ValidationException;
import org.biokoframework.system.command.crud.CrudMethod;
import org.biokoframework.system.entity.EntityClassNameTranslator;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.repository.core.Repository;
import org.biokoframework.system.repository.core.RepositoryException;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.annotation.field.ForeignKeysFactory;
import org.biokoframework.utils.fields.Fields;

public class AnnotatedEntityDissolver implements EntityDissolver {

	private HashMap<String, Repository<? extends DomainEntity>> _repositories = 
			new HashMap<String, Repository<? extends DomainEntity>>();
	private HashMap<String, Class<? extends DomainEntity>> _entityClasses = new HashMap<String, Class<? extends DomainEntity>>();
		
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> dissolve(Fields fields) throws Exception {
		List<String> entityFieldNames = getEntityNames(fields);
		Map<String, Object> dissolvedEntities = new HashMap<String, Object>();
		Map<String, Object> dissolvedEntitiesIds = new HashMap<String, Object>();

		boolean dissolvedSomething = true;
		while (dissolvedSomething) {
			dissolvedSomething = false;
			
			for (Iterator<String> it = entityFieldNames.iterator(); it.hasNext(); ) {
				String anEntityFieldName = it.next();
				
				Class<? extends DomainEntity> entityClass = _entityClasses.get(EntityClassNameTranslator.fromFieldName(anEntityFieldName));
				Object object = fields.objectNamed(anEntityFieldName);
				if (object instanceof Map) {
					Fields entityFields = Fields.fromMap((Map<String, Object>) object);
					DomainEntity entity;
					if ((entity = tryToFill(entityClass, entityFields, dissolvedEntitiesIds)) != null) {
						// TODO validate
						entity = saveEntityWithFields(entity);
						dissolvedEntities.put(anEntityFieldName, entity);
						dissolvedEntitiesIds.put(anEntityFieldName, entity.getId());
						it.remove();
						dissolvedSomething = true;					
					}
				} else if(object instanceof List) {
					ArrayList<DomainEntity> entities = new ArrayList<DomainEntity>();
					ArrayList<String> entityIDs = new ArrayList<String>();
					for (Object objectComponent : (List<?>) object) {
						Fields entityFields = Fields.fromMap((Map<String, Object>) objectComponent);
						DomainEntity entity;
						if ((entity = tryToFill(entityClass, entityFields, dissolvedEntitiesIds)) != null) {
							// TODO validate
							entity = saveEntityWithFields(entity);
							entities.add(entity);
							entityIDs.add(entity.getId());
						}
					}
					
					if (!entities.isEmpty()) {
						dissolvedEntities.put(anEntityFieldName, entities);
						dissolvedEntitiesIds.put(anEntityFieldName, entityIDs);
						it.remove();
						dissolvedSomething = true;
					}
				}
				
			}
		}
		
		if (!entityFieldNames.isEmpty()) {
			// TODO rollbackSavedEntities();
			throw CommandExceptionsFactory.createDissolutionIncompleteException(entityFieldNames);
		}
		
		return dissolvedEntities;
	}

	private DomainEntity tryToFill(Class<? extends DomainEntity> entityClass, Fields entityFields, Map<String, Object> dissolvedEntitiesId) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		DomainEntity entity = entityClass.getConstructor(Fields.class).newInstance(entityFields);
		for (Entry<String, Class<? extends DomainEntity>> aForeignKey : ForeignKeysFactory.create(entityClass)) {
			if (!entity.fields().containsKey(aForeignKey.getKey())) {
				if (!dissolvedEntitiesId.containsKey(EntityClassNameTranslator.toFieldName(aForeignKey.getValue().getSimpleName()))) {
					return null;
				} else {
					entity.fields().put(aForeignKey.getKey(), dissolvedEntitiesId.get(
							EntityClassNameTranslator.toFieldName(aForeignKey.getValue().getSimpleName())));
				}
			}
		}
		return entity;
	}

	protected DomainEntity saveEntityWithFields(DomainEntity entity)
			throws InstantiationException, IllegalAccessException, 	InvocationTargetException, NoSuchMethodException, ValidationException, RepositoryException {
		
		Repository<? extends DomainEntity> repo = _repositories.get(entity.getClass().getSimpleName());
		List<? extends DomainEntity> result = repo.call(entity, CrudMethod.POST.value());
		DomainEntity savedEntity = result.get(0);
		return savedEntity;
	}
	
	private List<String> getEntityNames(Fields fields) {
		List<String> entityNames = new LinkedList<String>();
		for (String aKey : fields.keys()) {
			if (containsEntity(fields.valueFor(aKey))) {
				entityNames.add(aKey);
			}
		}
		return entityNames;
	}
	
	private boolean containsEntity(Object fieldValue) {
		if (fieldValue instanceof Map) {
			return true;
		} else if (fieldValue instanceof List) {
			List<?> list = (List<?>) fieldValue;
			if (list.get(0) instanceof Map) {
				return true;
			}
		} else if (fieldValue instanceof DomainEntity) {
			return true;
		}
		
		return false;
	}

	
	@Override
	public <DE extends DomainEntity> EntityDissolver savingIn(Repository<DE> repository, Class<DE> domainEntityClass) {
		String entityName = domainEntityClass.getSimpleName();
		_repositories.put(entityName, repository);
		_entityClasses.put(entityName, domainEntityClass);
		return this;
	}
}
