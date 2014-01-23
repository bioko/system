package it.bioko.system.entity.resolution;

import it.bioko.system.repository.core.Repository;
import it.bioko.utils.domain.DomainEntity;

public interface EntityResolver {

	public <T extends DomainEntity> T solve(DomainEntity targetEntity, Class<T> targetEntityClass) throws Exception;
//	public Fields solve(Fields fields) throws Exception;
	
	public <DE extends DomainEntity> EntityResolver with(Repository<DE> repository, Class<DE> domainEntityClass);
	
	public EntityResolver maxDepth(int depthLimit);
	
}
