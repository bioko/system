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

package org.biokoframework.system.repository.sql;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.biokoframework.system.repository.core.AbstractRepository;
import org.biokoframework.system.repository.sql.query.SqlQuery;
import org.biokoframework.system.repository.sql.translator.SqlTypesTranslator;
import org.biokoframework.system.repository.sql.util.SqlStatementsHelper;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.annotation.field.ComponingFieldsFactory;
import org.biokoframework.utils.domain.annotation.field.Field;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.repository.RepositoryException;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class SqlRepository<DE extends DomainEntity> extends AbstractRepository<DE> {
	
	private static final Logger LOGGER = Logger.getLogger(SqlRepository.class);
	
	protected final SqlConnector fDbConnector;
	protected final Class<DE> fEntityClass;
	protected final String fTableName; 

	private LinkedHashMap<String, Field> fFieldNames;
	private SqlTypesTranslator fTranslator;

	public SqlRepository(Class entityClass, String tableName, SqlConnector connector, IEntityBuilderService entityBuilderService) throws RepositoryException {
		super(entityBuilderService);
		fEntityClass = (Class<DE>) entityClass;
		fTableName = tableName;
		try {
			fFieldNames = ComponingFieldsFactory.createWithAnnotation(fEntityClass);
		} catch (Exception exception) {
			// Should never happen
			fFieldNames = null;
			exception.printStackTrace();
		}
		fDbConnector = connector;
		fTranslator = connector.getTypesTranslator();
		ensureTable();
	}


    @SuppressWarnings("rawtypes")
    @Inject
	public SqlRepository(Class entityClass, SqlConnector connectionHelper, IEntityBuilderService entityBuilderService) throws RepositoryException {
		this(entityClass, entityClass.getSimpleName(), connectionHelper, entityBuilderService);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DE save(DomainEntity entity) throws RepositoryException, ValidationException {
		DE castedEntity = (DE) entity;
		if (!entity.isValid()) {
			throw new ValidationException(entity.getValidationErrors());
		}
		
		if (entity.getId() != null && !entity.getId().isEmpty()) {
			return update(castedEntity);
		} else {
			return insert(castedEntity);
		}
	}

	private DE insert(DE entity) throws RepositoryException {
		String id = null;
        Connection connection = null;
        PreparedStatement insertStatement = null;
		try {
			connection = fDbConnector.getConnection();
			insertStatement = SqlStatementsHelper.preparedCreateStatement(fEntityClass, fTableName, connection);
			
			int i = 1;
			for (Entry<String, Field> anEntry : fFieldNames.entrySet()) {
				String aFieldName = anEntry.getKey();
				fTranslator.insertIntoStatement(aFieldName, entity.get(aFieldName), anEntry.getValue(), insertStatement, i);
				i++;
			}
			insertStatement.execute();
						
			
			//id = SqlStatementsHelper.retrieveId(insertStatement.getGeneratedKeys());
			id = fDbConnector.getLastInsertId(connection).toString();
			
			connection.close();
		} catch (SQLException exception) {
			LOGGER.error("Error in insert", exception);
            closeDumbSql(connection, insertStatement, null);
		}
		
		if (!StringUtils.isEmpty(id) && !id.equals("0")) {
			entity.setId(id);
			return entity;			
		} else {
			return null;
		}
	}

	private DE update(DE entity) {
		boolean updated = false;
		Connection connection = null;
		PreparedStatement updateStatement = null;
		try {
			connection = fDbConnector.getConnection();
			updateStatement = SqlStatementsHelper.preparedUpdateStatement(fEntityClass, fTableName, connection);
			int i = 1;
			for (Entry<String, Field> anEntry : fFieldNames.entrySet()) {
				String aFieldName = anEntry.getKey();
				fTranslator.insertIntoStatement(aFieldName, entity.get(aFieldName), anEntry.getValue(), updateStatement, i);
				i++;
			}
			updateStatement.setString(fFieldNames.size() + 1, entity.getId());
			updateStatement.execute();
			
			updated = updateStatement.getUpdateCount() > 0;

			updateStatement.close();
			connection.close();
		} catch (SQLException exception) {
			LOGGER.error("Error in retrieve", exception);
            closeDumbSql(connection, updateStatement, null);
		}
		if (updated) {
			return entity;
		} else {
			return null;
		}
	}
	
	@Override
	public DE retrieve(String anEntityKey) {
		ArrayList<DE> entities = new ArrayList<DE>();
		Connection connection = null;
		PreparedStatement retrieveStatement = null;
		try {
			connection = fDbConnector.getConnection();
			retrieveStatement = SqlStatementsHelper.preparedRetrieveByIdStatement(fEntityClass, fTableName, connection);
			retrieveStatement.setObject(1, anEntityKey);
			retrieveStatement.execute();
			
			entities = SqlStatementsHelper.retrieveEntities(retrieveStatement.getResultSet(), fEntityClass, fTranslator, fEntityBuilderService);
		} catch (SQLException exception) {
			exception.printStackTrace();
		} finally {
            closeDumbSql(connection, retrieveStatement, null);
		}
		if (entities.isEmpty()) {
			return null;
		} else {
			return entities.get(0);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public DE retrieve(DomainEntity anEntity) {
		DE castedEntity = (DE) anEntity;
		return retrieve(castedEntity.getId());
	}

	@Override
	public DE delete(String anEntityKey) {
		DE toBeDeleted = retrieve(anEntityKey);
		boolean deleted = false;

        Connection connection = null;
        PreparedStatement deleteStatement = null;
		try {
			connection = fDbConnector.getConnection();
			deleteStatement = SqlStatementsHelper.preparedDeleteByIdStatement(fEntityClass, fTableName, connection);
			deleteStatement.setString(1, anEntityKey);
			deleteStatement.execute();
			
			deleted = deleteStatement.getUpdateCount() > 0;
			deleteStatement.close();
			connection.close();
		} catch (SQLException exception) {
            closeDumbSql(connection, deleteStatement, null);
			exception.printStackTrace();
		}
		
		if (deleted) {
			return toBeDeleted;
		} else {
			return null;
		}
	}

	@Override
	public ArrayList<DE> getAll() {
		ArrayList<DE> entities = new ArrayList<DE>();
		Connection connection = null;
		PreparedStatement retrieveStatement = null;
		try {
			connection = fDbConnector.getConnection();
			retrieveStatement = SqlStatementsHelper.preparedRetrieveAllStatement(fEntityClass, fTableName, connection);
			retrieveStatement.execute();
			
			entities = SqlStatementsHelper.retrieveEntities(retrieveStatement.getResultSet(), fEntityClass, fTranslator, fEntityBuilderService);
			retrieveStatement.close();
			connection.close();
		} catch (SQLException exception) {
            closeDumbSql(connection, retrieveStatement, null);
			exception.printStackTrace();
		}
		return entities;
	}

	@Override
	public ArrayList<DE> getEntitiesByForeignKey(String foreignKeyName, Object foreignKeyValue) {
		ArrayList<DE> entities = new ArrayList<DE>();
		Connection connection = null;
		PreparedStatement retrieveStatement = null;
		try {
			connection = fDbConnector.getConnection();
			retrieveStatement = SqlStatementsHelper.prepareRetrieveByForeignKey(fEntityClass, fTableName, connection, foreignKeyName);

			retrieveStatement.setObject(1, foreignKeyValue);
			retrieveStatement.execute();
			
			entities = SqlStatementsHelper.retrieveEntities(retrieveStatement.getResultSet(), fEntityClass, fTranslator, fEntityBuilderService);
		} catch (SQLException exception) {
			exception.printStackTrace();
		} finally {
			closeDumbSql(connection, retrieveStatement, null);
		}
		return entities;
	}

	@Override
	public DE retrieveByForeignKey(String foreignKeyName, String foreignKeyValue) {
		ArrayList<DE> entities = new ArrayList<DE>();
		Connection connection = null;
		PreparedStatement retrieveStatement = null;
		try {
			connection = fDbConnector.getConnection();
			retrieveStatement = SqlStatementsHelper.prepareRetrieveOneByForeignKey(fEntityClass, fTableName, connection, foreignKeyName);
			retrieveStatement.setString(1, foreignKeyValue);
			retrieveStatement.execute();
			
			entities = SqlStatementsHelper.retrieveEntities(retrieveStatement.getResultSet(), fEntityClass, fTranslator, fEntityBuilderService);
			retrieveStatement.close();
			connection.close();
		} catch (SQLException exception) {
			LOGGER.error("Retrieve:", exception);
			exception.printStackTrace();
		} finally {
			closeDumbSql(connection, retrieveStatement, null);
		}
		if (entities.isEmpty()) {
			return null;
		} else {
			return entities.get(0);			
		}
	}
	
	@Override
	public SqlQuery<DE> createQuery() {
		return new SqlQuery<DE>(fDbConnector, fEntityBuilderService);
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getTableName() {
		return fTableName;
	}
	
	public SqlTypesTranslator getTranslator() {
		return fTranslator;
	}
	
	public LinkedHashMap<String,Field> getFieldNames() {
		return fFieldNames;
	}
	
	private void ensureTable() throws RepositoryException {
		try {
			if (!fDbConnector.tableExist(fTableName)) {
				createTableFor(fEntityClass, fDbConnector);
			} 
			
			
		} catch (Exception exception) {
			LOGGER.error("DB table check", exception);
			throw new RepositoryException(exception);
		}
	}
	
	
	
	private void createTableFor(Class<DE> entityClass, SqlConnector helper) throws SQLException {
		Connection connection = null;
        Statement statement = null;
        try {

            connection = helper.getConnection();
		
		ArrayList<String> fieldEntries = new ArrayList<String>();
		try {
			for (Entry<String, Field> entry : ComponingFieldsFactory.createWithAnnotation(entityClass).entrySet()) {
				fieldEntries.add(entry.getKey() + " " + fTranslator.getSqlType(entry.getKey(), entry.getValue()));
			}
			fieldEntries.add(DomainEntity.ID + " " + fTranslator.getSqlType(DomainEntity.ID, null));
			fieldEntries.addAll(fTranslator.getAllConstraints());
			fTranslator.clearConstraintsList();
		} catch (Exception exception) {
			// THIS SHOULD NEVER HAPPEN
			System.err.println("[EASY MAN] - cannot retrieve annotation stuff in create SQL table");
			exception.printStackTrace();
		}
		
		
		StringBuilder sql = new StringBuilder().
				append("CREATE TABLE ").append(fTableName).
				append(" (").append(StringUtils.join(fieldEntries, ", ")).append(") ").append(fDbConnector.getCreateTableTail());
		
//		System.out.println(sql);
			statement = connection.createStatement();
			statement.execute(sql.toString());
			statement.close();
			connection.close();
        } catch (SQLException exception) {
            closeDumbSql(connection, statement, null);
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
