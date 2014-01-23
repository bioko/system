package it.bioko.system.entity.resolution;

import it.bioko.system.repository.core.Repository;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.fields.Fields;

import java.util.Map;

public interface EntityDissolver {

	public Map<String, Object> dissolve(Fields fields) throws Exception;
	
	public <DE extends DomainEntity> EntityDissolver savingIn(Repository<DE> repository, Class<DE> domainEntityClass);

}
