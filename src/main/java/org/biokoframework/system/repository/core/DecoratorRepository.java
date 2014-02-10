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

import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.RepositoryException;
import org.biokoframework.utils.repository.query.Query;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 10, 2014
 *
 * @param <DE> domain entity
 * @param <R> base repository type
 */
public class DecoratorRepository<DE extends DomainEntity, R extends Repository<DE>> extends AbstractRepository<DE> {

	private R fBaseRepository;

	public DecoratorRepository(R baseRepository) {
		fBaseRepository = baseRepository;
	}
	
	@Override
	public DE save(DE anEntity) throws ValidationException, RepositoryException {
		return fBaseRepository.save(anEntity);
	}

	@Override
	public DE delete(String anEntityKey) {
		return fBaseRepository.delete(anEntityKey);
	}

	@Override
	public DE retrieve(String anEntityKey) {
		return fBaseRepository.retrieve(anEntityKey);
	}

	@Override
	public DE retrieveByForeignKey(String foreignKeyName, String foreignKeyValue) {
		return fBaseRepository.retrieveByForeignKey(foreignKeyName, foreignKeyValue);
	}

	@Override
	public ArrayList<DE> getEntitiesByForeignKey(String foreignKeyName, Object foreignKeyValue) {
		return fBaseRepository.getEntitiesByForeignKey(foreignKeyName, foreignKeyValue);
	}

	@Override
	public ArrayList<DE> getAll() {
		return fBaseRepository.getAll();
	}

	@Override
	public DE retrieve(DE anEntityT) {
		return fBaseRepository.retrieve(anEntityT);
	}

	@Override
	public Query<DE> createQuery() {
		return fBaseRepository.createQuery();
	}

	protected R getBaseRepository() {
		return fBaseRepository;
	}
}
