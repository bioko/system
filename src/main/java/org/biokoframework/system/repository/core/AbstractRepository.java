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

package org.biokoframework.system.repository.core;

import java.util.ArrayList;
import java.util.List;

import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.RepositoryException;
import org.biokoframework.utils.repository.query.Query;

public abstract class AbstractRepository<T extends DomainEntity> implements Repository<T> {
	
	protected final IEntityBuilderService fEntityBuilderService;

	public AbstractRepository(IEntityBuilderService entityBuilderService) {
		fEntityBuilderService = entityBuilderService;
	}
	
	public List<T> call(DomainEntity aDomainEntity, String aMethod) throws ValidationException, RepositoryException {
		T repoEntity = (T) aDomainEntity;
		
		ArrayList<T> result = new ArrayList<T>();
		// TODO colpetto di reflection per estrarre il nome del metodo 
		// da invocare, associato ai metodi rest
		if (aMethod.equals("SAVE")) {
			T entity = save(repoEntity);
			if (entity != null) { 
				result.add(entity);
			}
		} else if (aMethod.equals("RETRIEVE")) {
			if (repoEntity.getId() != null) {
				T entity = retrieve(repoEntity.getId());
				if (entity != null)
					result.add(entity);
			} else {
				System.out.println(repoEntity.fields());
				String filledFieldKey = getFirstNotVoidFieldKey(repoEntity.fields());
				if (filledFieldKey==null) {
					result = getAll();
				} else {
					result = getEntitiesByForeignKey(filledFieldKey, repoEntity.get(filledFieldKey).toString());
				}
				
//				result = getAll();
			}
		} else if (aMethod.equals("DELETE")) {
			T deleted = delete(repoEntity.getId());
			if (deleted != null)
				result.add(deleted);
		}
		return result;
	}

	// TODO togliere quando Mattia farà il query builder,
	// per ora restringo solo sul primo campo pieno
	private String getFirstNotVoidFieldKey(Fields fields) {
		String foundKey=null;
		for (String fieldKey: fields.keys()) {
			if (fields.containsKey(fieldKey)) {
				foundKey=fieldKey;
				break;
			}
		}
		
		return foundKey;
	}

	@Override
	public abstract Query<T> createQuery();

	public T getInstance(Class<T> entityClass) {
		return fEntityBuilderService.getInstance(entityClass);
	}

}