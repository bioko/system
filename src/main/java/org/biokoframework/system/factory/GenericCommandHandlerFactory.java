/*
 * Copyright (c) 2014																 
 *	Mikol Faro			<mikol.faro@gmail.com>
 *	Simone Mangano		<simone.mangano@ieee.org>
 *	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package org.biokoframework.system.factory;

import org.biokoframework.system.KILL_ME.commons.GenericCommandNames;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.biokoframework.system.command.AbstractCommandHandler;
import org.biokoframework.system.command.Command;
import org.biokoframework.system.command.crud.CrudCommand;
import org.biokoframework.system.command.crud.binary.DeleteBinaryEntityCommand;
import org.biokoframework.system.command.crud.binary.GetBinaryEntityCommand;
import org.biokoframework.system.command.crud.binary.HeadBinaryEntityCommand;
import org.biokoframework.system.command.crud.binary.PostBinaryEntityCommand;
import org.biokoframework.system.command.crud.binary.PutBinaryEntityCommand;
import org.biokoframework.system.command.entityDependencies.ResolvableCommand;
import org.biokoframework.system.context.Context;
import org.biokoframework.system.entity.EntityClassNameTranslator;
import org.biokoframework.system.entity.authentication.Authentication;
import org.biokoframework.system.factory.binary.BinaryEntityRepository;
import org.biokoframework.system.repository.core.Repository;
import org.biokoframework.utils.domain.DomainEntity;

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