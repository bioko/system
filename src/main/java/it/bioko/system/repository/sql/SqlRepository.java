package it.bioko.system.repository.sql;

import it.bioko.system.command.ValidationException;
import it.bioko.system.repository.core.Repository;
import it.bioko.system.repository.core.RepositoryException;
import it.bioko.system.repository.sql.query.SqlQuery;
import it.bioko.system.repository.sql.translator.SqlTypesTranslator;
import it.bioko.system.repository.sql.util.SqlStatementsHelper;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.ComponingFieldsFactory;
import it.bioko.utils.domain.annotation.field.Field;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class SqlRepository<DE extends DomainEntity> extends Repository<DE> {
	
	protected final SqlConnector _dbConnector;
	protected final Class<DE> _entityClass;
	protected final String _tableName; 

	private LinkedHashMap<String, Field> _fieldNames;
	private SqlTypesTranslator _translator;
	

	public SqlRepository(Class<DE> entityClass, String tableName, SqlConnector connector) throws RepositoryException {
		_entityClass = entityClass;
		_tableName = tableName;
		try {
			_fieldNames = ComponingFieldsFactory.createWithAnnotation(_entityClass);
		} catch (Exception exception) {
			// Should never happen
			_fieldNames = null;
			exception.printStackTrace();
		}
		_dbConnector = connector;
		_translator = connector.getTypesTranslator();
		ensureTable();
	}
	
	
	public SqlRepository(Class<DE> entityClass, SqlConnector connectionHelper) throws RepositoryException {
		this(entityClass, entityClass.getSimpleName(), connectionHelper);
	}
	
	@Override
	public DE save(DE entity) throws RepositoryException, ValidationException {
		if (!entity.isValid()) {
			throw new ValidationException(entity.getValidationErrors());
		}
		
		if (entity.getId() != null && !entity.getId().isEmpty()) {
			return update(entity);
		} else {
			return insert(entity);
		}
	}

	private DE insert(DE entity) throws RepositoryException {
		String id = null;
		try {
			Connection connection = _dbConnector.getConnection();
			PreparedStatement insertStatement = SqlStatementsHelper.preparedCreateStatement(_entityClass, _tableName, connection);
			
			int i = 1;
			for (Entry<String, Field> anEntry : _fieldNames.entrySet()) {
				String aFieldName = anEntry.getKey();			
				_translator.insertIntoStatement(aFieldName, entity.get(aFieldName), anEntry.getValue(), insertStatement, i);				
				i++;
			}
			insertStatement.execute();
						
			
			//id = SqlStatementsHelper.retrieveId(insertStatement.getGeneratedKeys());
			id = _dbConnector.getLastInsertId(connection).toString();
			
			connection.close();
		} catch (SQLException exception) {
			Logger.getLogger("engagedServer").error("Error in insert", exception);
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
		try {
			Connection connection = _dbConnector.getConnection();
			PreparedStatement updateStatement = SqlStatementsHelper.preparedUpdateStatement(_entityClass, _tableName, connection);
			int i = 1;
			for (Entry<String, Field> anEntry : _fieldNames.entrySet()) {
				String aFieldName = anEntry.getKey();
				_translator.insertIntoStatement(aFieldName, entity.get(aFieldName), anEntry.getValue(), updateStatement, i);
				i++;
			}
			updateStatement.setString(_fieldNames.size() + 1, entity.getId());
			updateStatement.execute();
			
			updated = updateStatement.getUpdateCount() > 0;

			updateStatement.close();
			connection.close();
		} catch (SQLException exception) {
			Logger.getLogger("engagedServer").error("Error in retrieve", exception);
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
		try {
			connection = _dbConnector.getConnection();
			PreparedStatement retrieveStatement = SqlStatementsHelper.preparedRetrieveByIdStatement(_entityClass, _tableName, connection);
			retrieveStatement.setString(1, anEntityKey);
			retrieveStatement.execute();
			
			entities = SqlStatementsHelper.retrieveEntities(retrieveStatement.getResultSet(), _entityClass, _translator);
			connection.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		} finally {
			try {
				if (connection != null && !connection.isClosed())
					connection.close();
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		}
		if (entities.isEmpty()) {
			return null;
		} else {
			return entities.get(0);
		}
	}

	@Override
	public DE retrieve(DE anEntityT) {
		return retrieve(anEntityT.getId());
	}

	@Override
	public DE delete(String anEntityKey) {
		DE toBeDeleted = retrieve(anEntityKey);
		boolean deleted = false;
		try {
			Connection connection = _dbConnector.getConnection();
			PreparedStatement deleteStatement = SqlStatementsHelper.preparedDeleteByIdStatement(_entityClass, _tableName, connection);
			deleteStatement.setString(1, anEntityKey);
			deleteStatement.execute();
			
			deleted = deleteStatement.getUpdateCount() > 0;
			connection.close();
		} catch (SQLException exception) {
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
		try {
			Connection connection = _dbConnector.getConnection();
			PreparedStatement retrieveStatement = SqlStatementsHelper.preparedRetrieveAllStatement(_entityClass, _tableName, connection);
			retrieveStatement.execute();
			
			entities = SqlStatementsHelper.retrieveEntities(retrieveStatement.getResultSet(), _entityClass, _translator);
			connection.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return entities;
	}

	@Override
	public ArrayList<DE> getEntitiesByForeignKey(String foreignKeyName, String foreignKeyValue) {
		ArrayList<DE> entities = new ArrayList<DE>();
		Connection connection = null;
		PreparedStatement retrieveStatement = null;
		try {
			connection = _dbConnector.getConnection();
			retrieveStatement = SqlStatementsHelper.prepareRetrieveByForeignKey(_entityClass, _tableName, connection, foreignKeyName);

			retrieveStatement.setString(1, foreignKeyValue);
			retrieveStatement.execute();
			
			entities = SqlStatementsHelper.retrieveEntities(retrieveStatement.getResultSet(), _entityClass, _translator);
		} catch (SQLException exception) {
			exception.printStackTrace();
		} finally {
			try {
				if (retrieveStatement != null && !retrieveStatement.isClosed()) {
					retrieveStatement.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return entities;
	}

	@Override
	public DE retrieveByForeignKey(String foreignKeyName, String foreignKeyValue) {
		ArrayList<DE> entities = new ArrayList<DE>();
		try {
			Connection connection = _dbConnector.getConnection();
			PreparedStatement retrieveStatement = SqlStatementsHelper.prepareRetrieveOneByForeignKey(_entityClass, _tableName, connection, foreignKeyName);
			retrieveStatement.setString(1, foreignKeyValue);
			retrieveStatement.execute();
			
			entities = SqlStatementsHelper.retrieveEntities(retrieveStatement.getResultSet(), _entityClass, _translator);
			retrieveStatement.close();
			connection.close();
		} catch (SQLException exception) {
			Logger.getLogger("engagedServer").error("Retrieve:", exception);
			exception.printStackTrace();
		}
		if (entities.isEmpty()) {
			return null;
		} else {
			return entities.get(0);			
		}
	}
	
	@Override
	public SqlQuery<DE> createQuery() {
		return new SqlQuery<DE>(_dbConnector);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String report() {
		// TODO Auto-generated method stub
		return null;
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getTableName() {
		return _tableName;
	}
	
	public SqlTypesTranslator getTranslator() {
		return _translator;
	}
	
	public LinkedHashMap<String,Field> getFieldNames() {
		return _fieldNames;
	}
	
	private void ensureTable() throws RepositoryException {
		try {
			if (!_dbConnector.tableExist(_tableName)) {
				createTableFor(_entityClass, _dbConnector);
			} 
			
			
		} catch (Exception exception) {
			Logger.getLogger("engagedServer").error("DB table check", exception);
			throw new RepositoryException(exception);
		}
	}
	
	
	
	private void createTableFor(Class<DE> entityClass, SqlConnector helper) throws SQLException {
		
		Connection connection = helper.getConnection();
		
		ArrayList<String> fieldEntries = new ArrayList<String>();
		try {
			for (Entry<String, Field> entry : ComponingFieldsFactory.createWithAnnotation(entityClass).entrySet()) {
				fieldEntries.add(entry.getKey() + " " + _translator.getSqlType(entry.getKey(), entry.getValue()));
			}
			fieldEntries.add(DomainEntity.ID + " " + _translator.getSqlType(DomainEntity.ID, null));
			fieldEntries.addAll(_translator.getAllConstraints());
			_translator.clearConstraintsList();
		} catch (Exception exception) {
			// THIS SHOULD NEVER HAPPEN
			System.err.println("[EASY MAN] - cannot retrieve annotation stuff in create SQL table");
			exception.printStackTrace();
		}
		
		
		StringBuilder sql = new StringBuilder().
				append("CREATE TABLE ").append(_tableName).
				append(" (").append(StringUtils.join(fieldEntries, ", ")).append(") ").append(_dbConnector.getCreateTableTail());
		
//		System.out.println(sql);
		Statement statement = connection.createStatement();
		statement.execute(sql.toString());
		statement.close();
		connection.close();
	}

}
