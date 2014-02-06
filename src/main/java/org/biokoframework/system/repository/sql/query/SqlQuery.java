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

package org.biokoframework.system.repository.sql.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.biokoframework.system.repository.sql.SqlConnector;
import org.biokoframework.system.repository.sql.SqlRepository;
import org.biokoframework.system.repository.sql.util.SqlStatementsHelper;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.query.Query;

public class SqlQuery<DE extends DomainEntity> implements Query<DE> {

	// SELECT, UPDATE, DELETE
	private SqlMethod _method;

	// FROM
	private SqlRepository<DE> _sqlRepository;
	private Class<DE> _entityClass;

	// WHERE
	private List<Entry<SqlLogicOperator, SqlConstraint<DE>>> _constraints = new LinkedList<Entry<SqlLogicOperator, SqlConstraint<DE>>>();

	private PreparedStatement _statement;
	private List<String> _placeholders;
	private Connection _connection;
	private final SqlConnector _dbConnector;

	public SqlQuery(SqlConnector helper) {
		_dbConnector = helper;
	}
	
	SqlConnector getConnector() {
		return _dbConnector;
	}
	

	@Override
	public SqlQuery<DE> from(Repository<DE> repository, Class<DE> entityClass) {
		if (repository instanceof SqlRepository) {
			_entityClass = entityClass;
			_sqlRepository = (SqlRepository<DE>) repository;
		}
		return this;
	}

	@Override
	public SqlConstraint<DE> where(String fieldName) {
		return createConstraint(SqlLogicOperator.NONE, fieldName);
	}

	@Override
	public SqlConstraint<DE> and(String fieldName) {
		return createConstraint(SqlLogicOperator.AND, fieldName);
	}

	@Override
	public SqlConstraint<DE> or(String fieldName) {
		return createConstraint(SqlLogicOperator.OR, fieldName);
	}

	private SqlConstraint<DE> createConstraint(SqlLogicOperator logicOperator, String fieldName) {
		SqlConstraint<DE> constraint = new SqlConstraint<DE>(this).setFieldName(fieldName);
		_constraints.add(new SimpleEntry<SqlLogicOperator, SqlConstraint<DE>>(logicOperator, constraint));
		return constraint;
	}

	@Override
	public ArrayList<DE> getAll() {

		ArrayList<DE> entities = new ArrayList<DE>();
		try {
			prepareStatement();
			_statement.execute();
			ResultSet result = null;
			if ((result = _statement.getResultSet()) != null) {
				entities = SqlStatementsHelper.retrieveEntities(result, _entityClass, _sqlRepository.getTranslator());
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		} finally {
			try {
				if (_statement != null && !_statement.isClosed()) {
					_statement.close();
				}
				if (_connection != null && !_connection.isClosed()) {
					_connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return entities;
	}

	@Override
	public SqlQuery<DE> select() {
		_method = SqlMethod.SELECT;
		return this;
	}

	@Override
	public SqlQuery<DE> update() {
		_method = SqlMethod.UPDATE;
		return this;
	}

	@Override
	public SqlQuery<DE> delete() {
		_method = SqlMethod.DELETE;
		return this;
	}

	@Override
	public void setValue(String placeholder, Object value) {
		
		retrievePlaceHolders();
		
		int sqlIndex = _placeholders.indexOf(placeholder) + 1;
		for (Entry<SqlLogicOperator, SqlConstraint<DE>> aConnectedConstraint : _constraints) {
			aConnectedConstraint.getValue().setValue(placeholder, value, sqlIndex);
		}
	}

	private void retrievePlaceHolders() {
		if (_placeholders == null || _placeholders.isEmpty()) {
			_placeholders = new ArrayList<String>();
			for (Entry<SqlLogicOperator, SqlConstraint<DE>> aConnectedConstraint : _constraints) {
				SqlConstraint<DE> aConstraint = aConnectedConstraint.getValue();
				_placeholders.addAll(aConstraint.getPlaceholders());
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(_method).append(" * ");
		
		s.append("from ");
		if (_sqlRepository != null) {
			s.append(_sqlRepository.getTableName());
		} else {
			s.append("?");
		}
		
		if (!_constraints.isEmpty()) {
			s.append(" where");
			for (Entry<SqlLogicOperator, SqlConstraint<DE>> aConnectedConstraint : _constraints) {
				s.append(aConnectedConstraint.getKey()).append(" (").append(aConnectedConstraint.getValue()).append(") ");
			}
		}
		s.append(";");
		return s.toString();
	}

	public String toSqlString() {
		StringBuilder s = new StringBuilder();
		s.append(_method).append(" * ");
		if (_sqlRepository != null) {
			s.append("from ").append(_sqlRepository.getTableName());
		}
		if (!_constraints.isEmpty()) {
			s.append(" where");
			for (Entry<SqlLogicOperator, SqlConstraint<DE>> aConnectedConstraint : _constraints) {
				s.append(aConnectedConstraint.getKey()).append(" (").append(aConnectedConstraint.getValue().toSqlString()).append(") ");
			}
		}
		s.append(";");
		return s.toString();
	}

	private void prepareStatement() throws SQLException {
		if (_statement == null || _statement.isClosed()) {
			_connection = _dbConnector.getConnection();
			_statement = _connection.prepareStatement(toSqlString());

			for (Entry<SqlLogicOperator, SqlConstraint<DE>> aConnectedConstraint : _constraints) {
				SqlConstraint<DE> aConstraint = aConnectedConstraint.getValue();
				aConstraint.prepareStatement(_statement, _sqlRepository.getTranslator(), _sqlRepository.getFieldNames());
			}
		}
	}

	

}
