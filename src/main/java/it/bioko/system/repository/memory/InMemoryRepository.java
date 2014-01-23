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

package it.bioko.system.repository.memory;

import it.bioko.system.repository.core.RepositoryException;
import it.bioko.system.repository.sql.SqlRepository;
import it.bioko.utils.domain.DomainEntity;

import java.sql.SQLException;
import java.util.List;

public class InMemoryRepository<T extends DomainEntity> extends SqlRepository<T> {


	public InMemoryRepository(Class<T> entityClass) throws RepositoryException {		
		super(entityClass, entityClass.getSimpleName(), HsqldbMemConnector.getInstance());		
		
		try {
			_dbConnector.emptyTable(_tableName);
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
		
	}

	public String getContentAsPrettyString() {
		StringBuilder builder = new StringBuilder();
		
		List<T> all = getAll();
		for (T e: all) {
			builder.append(e.toJSONString());
			builder.append("\n");
		}
		
		return builder.toString();
		
	}
	

}