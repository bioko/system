package it.bioko.system.exceptions;

import it.bioko.system.command.CommandException;
import it.bioko.utils.domain.DomainEntity;

public interface EntityValidator<T extends DomainEntity> {

	public void validate(T entity) throws CommandException;
	
}
