package it.bioko.system.factory;

import it.bioko.system.KILL_ME.commons.GenericCommandNames;
import it.bioko.system.KILL_ME.commons.HttpMethod;
import it.bioko.system.command.AbstractCommandHandler;
import it.bioko.system.command.Command;
import it.bioko.system.command.crud.CrudCommand;
import it.bioko.system.command.crud.binary.DeleteBinaryEntityCommand;
import it.bioko.system.command.crud.binary.GetBinaryEntityCommand;
import it.bioko.system.command.crud.binary.HeadBinaryEntityCommand;
import it.bioko.system.command.crud.binary.PostBinaryEntityCommand;
import it.bioko.system.command.crud.binary.PutBinaryEntityCommand;
import it.bioko.system.command.entityDependencies.ResolvableCommand;
import it.bioko.system.context.Context;
import it.bioko.system.entity.EntityClassNameTranslator;
import it.bioko.system.entity.authentication.Authentication;
import it.bioko.system.factory.binary.BinaryEntityRepository;
import it.bioko.system.repository.core.Repository;
import it.bioko.utils.domain.DomainEntity;

@Deprecated // Replaced by annotation
public abstract class GenericCommandHandlerFactory {

	private Repository<Authentication> _authenticationRepository;

	public GenericCommandHandlerFactory(Repository<Authentication> authenticationRepository) {
		_authenticationRepository = authenticationRepository;
	}

	protected <T extends DomainEntity> void registerCrudRestOn(Class<T> domainEntityClass, CrudCommand<T> crudCommand, AbstractCommandHandler commandHandler) {
		commandHandler.putRest(composeCrudCommandName(HttpMethod.POST.name(), domainEntityClass), crudCommand);
		commandHandler.putRest(composeCrudCommandName(HttpMethod.GET.name(), domainEntityClass), crudCommand);
		commandHandler.putRest(composeCrudCommandName(HttpMethod.PUT.name(), domainEntityClass), crudCommand);
		commandHandler.putRest(composeCrudCommandName(HttpMethod.DELETE.name(), domainEntityClass), crudCommand);
	}

	protected <T extends DomainEntity> String composeCrudCommandName(String commandNamePrefix, Class<T> domainEntityClass) {
		return GenericCommandNames.composeCommandName(commandNamePrefix, EntityClassNameTranslator.toHyphened(domainEntityClass.getSimpleName()));
	}

	protected void registerServiceCommandsRest(AbstractCommandHandler commandHandler) {
//		commandHandler.put(GenericCommandNames.OPTIONS_COMMAND_INVOCATION_INFO, new GetCommandInvocationInfoCommand(commandHandler));
//		commandHandler.putRest(GenericCommandNames.OPTIONS_COMMAND_LIST, new GetCommandsListCommand(commandHandler));
	}
	
	protected ResolvableCommand resolvable(Command command) {
		return new ResolvableCommand(command);
	}

	protected void registerBlobCrudRestOn(String entityName, AbstractCommandHandler commandHandler, Context context, BinaryEntityRepository blobRepo) {
		commandHandler.putRest(GenericCommandNames.composeRestCommandName(
				HttpMethod.POST, entityName), new PostBinaryEntityCommand(context, blobRepo, entityName));
		commandHandler.putRest(GenericCommandNames.composeRestCommandName(
				HttpMethod.GET, entityName), new GetBinaryEntityCommand(context, blobRepo));
		commandHandler.putRest(GenericCommandNames.composeRestCommandName(
				HttpMethod.PUT, entityName), new PutBinaryEntityCommand(context, blobRepo, entityName));
		commandHandler.putRest(GenericCommandNames.composeRestCommandName(
				HttpMethod.DELETE, entityName), new DeleteBinaryEntityCommand(context, blobRepo));
		commandHandler.putRest(GenericCommandNames.composeRestCommandName(
				HttpMethod.HEAD, entityName), new HeadBinaryEntityCommand(context, blobRepo));
	}
}
