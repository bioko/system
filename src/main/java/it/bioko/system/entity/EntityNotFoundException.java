package it.bioko.system.entity;

import it.bioko.system.command.CommandException;
import it.bioko.utils.domain.ErrorEntity;

@SuppressWarnings("serial")
public class EntityNotFoundException extends CommandException {

	public EntityNotFoundException(ErrorEntity error) {
		super(error);
		// TODO Auto-generated constructor stub
	}

}
