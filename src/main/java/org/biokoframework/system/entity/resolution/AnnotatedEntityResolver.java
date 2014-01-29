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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.annotation.field.ForeignKeysFactory;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

public class AnnotatedEntityResolver implements EntityResolver {

	private HashMap<Class<? extends DomainEntity>, Repository<? extends DomainEntity>> _repos = 
			new HashMap<Class<? extends DomainEntity>, Repository<? extends DomainEntity>>();
	
	private int _depthLimit = -1;
	
	@Override
	public <T extends DomainEntity> T solve(DomainEntity targetEntity, Class<T> targetEntityClass) throws Exception {
		return solve(targetEntity, targetEntityClass, _depthLimit);
	}
	
	@SuppressWarnings("unchecked")
	private <T extends DomainEntity> T solve(DomainEntity targetEntity, Class<T> targetEntityClass, int depthLimit) throws Exception {
		if (depthLimit == 0) {
			return (T) targetEntity;
		}
		
		ArrayList<Entry<String, Class<? extends DomainEntity>>> foreignKeys = ForeignKeysFactory.create(targetEntityClass);
		List<String> currentKeys = targetEntity.fields().keys();
		
		Fields fields = targetEntity.fields().copy();
		
		for (Entry<String, Class<? extends DomainEntity>> aForeignKey : foreignKeys) {
			if (currentKeys.contains(aForeignKey.getKey())) {
				Object keyValue = fields.get(aForeignKey.getKey());
				if (!aForeignKey.getClass().isInstance(keyValue)) {
					if (keyValue instanceof List) {
						ArrayList<Object> targets = new ArrayList<Object>();
						for (Object aKeyValueItem : (List<Object>)keyValue) {
							if (aKeyValueItem instanceof DomainEntity) {
								targets.add(solve((DomainEntity) aKeyValueItem, aForeignKey.getValue(), depthLimit - 1));
							} else {
								String aKey = (String) aKeyValueItem;
								targets.add(retrieveForeignEntity(depthLimit, aForeignKey, aKey));
							}
						}
						fields.put(aForeignKey.getKey(), targets);
					} else {
						Object target = retrieveForeignEntity(depthLimit, aForeignKey, (String) keyValue);
						fields.put(aForeignKey.getKey(), target);
					}
				}
			}
		}
		
		return targetEntityClass.getConstructor(Fields.class).newInstance(fields);
	}

	private Object retrieveForeignEntity(int depthLimit, Entry<String, Class<? extends DomainEntity>> aForeignKey, String id) throws Exception {
		Object target;
		if (!_repos.containsKey(aForeignKey.getValue())) {
			target = id;
		} else {
			DomainEntity entity = _repos.get(aForeignKey.getValue()).retrieve(id);
			target = solve(entity, aForeignKey.getValue(), depthLimit - 1);
		}
		return target;
	}
	

//	@Override
//	public Fields solve(Fields fields) throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public <DE extends DomainEntity> EntityResolver with(Repository<DE> repository, Class<DE> domainEntityClass) {
		_repos.put(domainEntityClass, repository);
		return this;
	}
	
	@Override
	public EntityResolver maxDepth(int depthLimit) { 
		_depthLimit = depthLimit;
		return this;
	}

}
