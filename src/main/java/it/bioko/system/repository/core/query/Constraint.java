package it.bioko.system.repository.core.query;

import it.bioko.utils.domain.DomainEntity;

public interface Constraint<DE extends DomainEntity> {

	public Constraint<DE> setFieldName(String fieldName);

	public Query<DE> placeholder(String placeholder);
	
	public Constraint<DE> isEqual();
	public Query<DE> isEqual(String value);
	public Constraint<DE> isNotEqual();
	public Query<DE> isNotEqual(String value);

	public Constraint<DE> like();
	public Query<DE> like(String value);
	public Constraint<DE> notLike();
	public Query<DE> notLike(String value);

	public Constraint<DE> not();

	public Query<DE> ilike(String value);
	public Constraint<DE> ilike();

	public Query<DE> slike(String value);
	public Constraint<DE> slike();

	public Query<DE> lt(String string);
	public Constraint<DE> lt();

	public Query<DE> lte(String string);
	public Constraint<DE> lte();
	
	public Query<DE> gt(String string);
	public Constraint<DE> gt();

	public Query<DE> gte(String string);
	public Constraint<DE> gte();
	
}
