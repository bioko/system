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