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

import org.apache.commons.lang3.StringUtils;
import org.biokoframework.system.repository.sql.SqlConnector;
import org.biokoframework.system.repository.sql.SqlRepository;
import org.biokoframework.system.repository.sql.util.SqlStatementsHelper;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.query.Query;

import java.sql.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public class SqlQuery<DE extends DomainEntity> implements Query<DE> {

	// SELECT, UPDATE, DELETE
	private SqlMethod fMethod;

	// FROM
	private SqlRepository<DE> fSqlRepository;
	private Class<DE> fEntityClass;

	// WHERE
	private List<Entry<SqlLogicOperator, SqlConstraint<DE>>> fConstraints = new LinkedList<Entry<SqlLogicOperator, SqlConstraint<DE>>>();

    private String fOrderBy;

    private PreparedStatement fStatement;
    private List<String> fPlaceholders;
    private Connection fConnection;
    private final SqlConnector fDbConnector;
    private final IEntityBuilderService fEntityBuilderService;

    public SqlQuery(SqlConnector helper, IEntityBuilderService entityBuilderService) {
		fEntityBuilderService = entityBuilderService;
		fDbConnector = helper;
	}
	
	SqlConnector getConnector() {
		return fDbConnector;
	}
	

	@Override
	public SqlQuery<DE> from(Repository<DE> repository, Class<DE> entityClass) {
		if (repository instanceof SqlRepository) {
			fEntityClass = entityClass;
			fSqlRepository = (SqlRepository<DE>) repository;
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
		fConstraints.add(new SimpleEntry<SqlLogicOperator, SqlConstraint<DE>>(logicOperator, constraint));
		return constraint;
	}

	@Override
	public ArrayList<DE> getAll() {

		ArrayList<DE> entities = new ArrayList<DE>();
        ResultSet result = null;
		try {
			prepareStatement();
			fStatement.execute();
			if ((result = fStatement.getResultSet()) != null) {
				entities = SqlStatementsHelper.retrieveEntities(result, fEntityClass, fSqlRepository.getTranslator(), fEntityBuilderService);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		} finally {
            closeDumbSql(fConnection, fStatement, result);
		}

		return entities;
	}

	@Override
	public SqlQuery<DE> select() {
		fMethod = SqlMethod.SELECT;
		return this;
	}

	@Override
	public SqlQuery<DE> update() {
		fMethod = SqlMethod.UPDATE;
		return this;
	}

	@Override
	public SqlQuery<DE> delete() {
		fMethod = SqlMethod.DELETE;
		return this;
	}

	@Override
	public void setValue(String placeholder, Object value) {
		
		retrievePlaceHolders();
		
		int sqlIndex = fPlaceholders.indexOf(placeholder) + 1;
		for (Entry<SqlLogicOperator, SqlConstraint<DE>> aConnectedConstraint : fConstraints) {
			aConnectedConstraint.getValue().setValue(placeholder, value, sqlIndex);
		}
	}

	private void retrievePlaceHolders() {
		if (fPlaceholders == null || fPlaceholders.isEmpty()) {
			fPlaceholders = new ArrayList<String>();
			for (Entry<SqlLogicOperator, SqlConstraint<DE>> aConnectedConstraint : fConstraints) {
				SqlConstraint<DE> aConstraint = aConnectedConstraint.getValue();
				fPlaceholders.addAll(aConstraint.getPlaceholders());
			}
		}
	}

    public SqlQuery<DE> orderBy(String fieldName) {
        fOrderBy = fieldName;
        return this;
    }

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(fMethod).append(" * ");
		
		s.append("from ");
		if (fSqlRepository != null) {
			s.append(fSqlRepository.getTableName());
		} else {
			s.append("?");
		}
		
		if (!fConstraints.isEmpty()) {
			s.append(" where");
			for (Entry<SqlLogicOperator, SqlConstraint<DE>> aConnectedConstraint : fConstraints) {
				s.append(aConnectedConstraint.getKey()).append(" (").append(aConnectedConstraint.getValue()).append(") ");
			}
		}

        if (!StringUtils.isEmpty(fOrderBy)) {
            s.append(" order by ").append(fOrderBy);
        }

		s.append(";");
		return s.toString();
	}

	public String toSqlString() {
		StringBuilder s = new StringBuilder();
		s.append(fMethod).append(" * ");
		if (fSqlRepository != null) {
			s.append("from ").append(fSqlRepository.getTableName());
		}
		if (!fConstraints.isEmpty()) {
			s.append(" where");
			for (Entry<SqlLogicOperator, SqlConstraint<DE>> aConnectedConstraint : fConstraints) {
				s.append(aConnectedConstraint.getKey()).append(" (").append(aConnectedConstraint.getValue().toSqlString()).append(") ");
			}
		}

        if (!StringUtils.isEmpty(fOrderBy)) {
            s.append(" order by ").append(fOrderBy);
        }

		s.append(";");
		return s.toString();
	}

	private void prepareStatement() throws SQLException {
		if (fStatement == null || fStatement.isClosed()) {
			fConnection = fDbConnector.getConnection();
			fStatement = fConnection.prepareStatement(toSqlString());

			for (Entry<SqlLogicOperator, SqlConstraint<DE>> aConnectedConstraint : fConstraints) {
				SqlConstraint<DE> aConstraint = aConnectedConstraint.getValue();
				aConstraint.prepareStatement(fStatement, fSqlRepository.getTranslator(), fSqlRepository.getFieldNames());
			}
		}
	}

    private void closeDumbSql(Connection connection, Statement statement, ResultSet set) {
        if (set != null) {
            try {
                set.close();
            } catch (SQLException e) { }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException closeException) { }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException closeException) { }
        }
    }

}
