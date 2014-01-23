package it.bioko.system.repository.core;

import it.bioko.system.command.CommandException;
import it.bioko.system.context.Context;
import it.bioko.utils.domain.DomainEntity;

import java.util.List;

public class SafeRepositoryHelper {
	
	public static <T extends DomainEntity> List<T> call(Repository<T> repository, DomainEntity aDomainEntity, String aMethod, Context context) throws CommandException {
		try {
			return repository.call(aDomainEntity, aMethod);
		} catch (Exception exception) {
//			context.getLogger().error("Not very safe call", exception);
			throw new CommandException(exception);
		}
	}

	public static <T extends DomainEntity> T save(Repository<T> repository, T anEntity, Context context) throws CommandException {
		try {
			return repository.save(anEntity);
		} catch (Exception exception) {
//			context.getLogger().error("Not very safe call", exception);
			throw new CommandException(exception);
		}
	}

}
