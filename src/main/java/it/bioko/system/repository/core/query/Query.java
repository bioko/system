package it.bioko.system.repository.core.query;

import it.bioko.system.repository.core.Repository;
import it.bioko.utils.domain.DomainEntity;

import java.util.ArrayList;

public interface Query<DE extends DomainEntity> {
	
	public Query<DE> from(Repository<DE> repository, Class<DE> entityClass);

	public Constraint<DE> where(String fieldName);
	
	public ArrayList<DE> getAll();

	
	public Query<DE> select();
	public Query<DE> update();
	public Query<DE> delete();

	public void setValue(String placeholder, String value);

	public Constraint<DE> and(String fieldName);
	public Constraint<DE> or(String fieldName);


	
}
