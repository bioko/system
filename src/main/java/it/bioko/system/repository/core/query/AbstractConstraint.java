package it.bioko.system.repository.core.query;

import it.bioko.utils.domain.DomainEntity;

public abstract class AbstractConstraint<DE extends DomainEntity> implements Constraint<DE> {

	@Override
	public Constraint<DE> isNotEqual() {
		return not().isEqual();
	}

	@Override
	public Query<DE> isNotEqual(String value) {
		return not().isEqual(value);
	}
	
	@Override
	public Constraint<DE> notLike() {
		return not().like();
	}
	
	@Override
	public Query<DE> notLike(String value) {
		return not().like(value);
	}

}
