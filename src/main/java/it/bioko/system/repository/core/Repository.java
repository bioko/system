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

package it.bioko.system.repository.core;

import it.bioko.system.command.ValidationException;
import it.bioko.system.repository.core.query.Query;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;
import java.util.List;

public abstract class Repository<T extends DomainEntity> {

	public abstract T save(T anEntity) throws ValidationException, RepositoryException;

	public abstract T delete(String anEntityKey);

	public abstract T retrieve(String anEntityKey);

	public abstract T retrieveByForeignKey(String foreignKeyName, String foreignKeyValue);
	
//	public abstract T retrieveByForeignKey(String foreignKeyName, String foreignKeyValue, boolean ignoreCase);

	public abstract ArrayList<T> getEntitiesByForeignKey(String foreignKeyName, String foreignKeyValue);
	
//	public abstract ArrayList<T> getEntitiesByForeignKey(String foreignKeyName, String foreignKeyValue, boolean ignoreCase);

	@Deprecated
	public final boolean contains(T anEntity) {
		return false;
	}

	public abstract String report();

	public abstract ArrayList<T> getAll();

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
				System.out.println(repoEntity.fields().report());
				String filledFieldKey = getFirstNotVoidFieldKey(repoEntity.fields());
				if (filledFieldKey==null) {
					result = getAll();
				} else {
					result = getEntitiesByForeignKey(filledFieldKey, repoEntity.get(filledFieldKey));
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

	public abstract T retrieve(T anEntityT);
	
	// TODO togliere quando Mattia farà il query builder,
	// per ora restringo solo sul primo campo pieno
	private String getFirstNotVoidFieldKey(Fields fields) {
		String foundKey=null;
		for (String fieldKey: fields.keys()) {
			if (fields.contains(fieldKey)) {
				foundKey=fieldKey;
				break;
			}
		}
		
		return foundKey;
	}
	public abstract Query<T> createQuery();

}