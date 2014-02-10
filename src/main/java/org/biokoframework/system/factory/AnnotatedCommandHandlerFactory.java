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

import java.lang.reflect.Field;

import org.apache.commons.lang3.ArrayUtils;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.KILL_ME.XSystemIdentityCard;
import org.biokoframework.system.KILL_ME.commons.GenericCommandNames;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.AbstractCommandHandler;
import org.biokoframework.system.command.CommandHandlerImpl;
import org.biokoframework.system.command.ICommand;
import org.biokoframework.system.command.annotation.Command;
import org.biokoframework.system.command.crud.annotation.CrudCommand;
import org.biokoframework.system.command.crud.binary.DeleteBinaryEntityCommand;
import org.biokoframework.system.command.crud.binary.GetBinaryEntityCommand;
import org.biokoframework.system.command.crud.binary.HeadBinaryEntityCommand;
import org.biokoframework.system.command.crud.binary.PostBinaryEntityCommand;
import org.biokoframework.system.command.crud.binary.PutBinaryEntityCommand;
import org.biokoframework.system.command.crud.binary.annotation.BlobCrudCommand;
import org.biokoframework.system.context.Context;
import org.biokoframework.system.entity.EntityClassNameTranslator;
import org.biokoframework.system.entity.binary.BinaryEntity;
import org.biokoframework.system.factory.binary.BinaryEntityRepository;
import org.biokoframework.system.repository.service.RepositoryService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.repository.Repository;

import com.google.inject.Injector;

public class AnnotatedCommandHandlerFactory {

	public static AbstractCommandHandler create(Class<?> annotatedSystemCommands, Context context, XSystemIdentityCard identityCard, Injector injector) 
			throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException {
		
		AbstractCommandHandler commandHandler = CommandHandlerImpl.empty(context.getSystemName(),  (String) context.get(Context.SYSTEM_VERSION));

		Field[] classFields = annotatedSystemCommands.getFields();

		for(Field classField: classFields) {
			searchForCommands(commandHandler, context, classField, identityCard.getSystemConfiguration(), injector);
			searchForCrudCommand(commandHandler, context, classField, identityCard.getSystemConfiguration(), injector);
			searchForBlobCrudCommand(commandHandler, context, classField, injector);
		}

		return commandHandler;
	}

	private static void searchForCommands(AbstractCommandHandler commandHandler, Context context, Field classField, ConfigurationEnum currentConfiguration, Injector injector) throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException {
		Command commandAnnotation = classField.getAnnotation(Command.class);
		if (commandAnnotation != null && !ArrayUtils.contains(commandAnnotation.hideOn(), currentConfiguration)) {
			
			String commandName = classField.get(null).toString();
			Class<? extends ICommand> commandClass = commandAnnotation.impl();				

			ICommand commandInstance = injector.getInstance(commandClass);
			commandInstance.setContext(context);
			commandInstance.setCommandName(commandName);

			if (!commandAnnotation.rest().equals(HttpMethod.NONE)) {  // ReST Command
				registerCommand(commandHandler, context, commandAnnotation.rest().name(), commandName, classField, commandInstance);
			} else {		// Standard command
				commandHandler.put(commandName, commandInstance);
			}

		}


	}


	@SuppressWarnings("unchecked")
	private static void searchForCrudCommand(AbstractCommandHandler commandHandler, Context context, Field classField, ConfigurationEnum currentConfiguration, Injector injector) throws IllegalArgumentException, IllegalAccessException {
		CrudCommand crudCommandAnnotation = classField.getAnnotation(CrudCommand.class);
		
		String crudName = classField.get(null).toString();

		if (crudCommandAnnotation!=null && !ArrayUtils.contains(crudCommandAnnotation.hideOn(), currentConfiguration)) {

			Class<DomainEntity> entity = (Class<DomainEntity>) crudCommandAnnotation.entity();
			Repository<DomainEntity> repo = context.getRepository(crudCommandAnnotation.repoName());

			AbstractCommand crudCommandInstance = new org.biokoframework.system.command.crud.CrudCommand<DomainEntity>(context, entity, repo);
			crudCommandInstance.setRepositoryService(injector.getInstance(RepositoryService.class));
			crudCommandInstance.setCommandName(crudName);
			
			if (crudCommandAnnotation.create())
				registerCommand(commandHandler, context, HttpMethod.POST.name(), crudName, classField, crudCommandInstance);
			if (crudCommandAnnotation.read()) 
				registerCommand(commandHandler, context, HttpMethod.GET.name(), crudName, classField, crudCommandInstance);
			if (crudCommandAnnotation.update())
				registerCommand(commandHandler, context, HttpMethod.PUT.name(), crudName, classField, crudCommandInstance);				
			if (crudCommandAnnotation.delete()) 
				registerCommand(commandHandler, context, HttpMethod.DELETE.name(), crudName, classField, crudCommandInstance);
			if (crudCommandAnnotation.describe())
				registerCommand(commandHandler, context, HttpMethod.OPTIONS.name(), crudName, classField, crudCommandInstance);

		}

	}


	private static void registerCommand(AbstractCommandHandler commandHandler, Context context,
			String httpMethod, String commandName, Field classField, ICommand commandInstance) {

		commandHandler.putRest(composeCrudCommandName(httpMethod, commandName), commandInstance);
//		Auth authAnnotation = classField.getAnnotation(Auth.class);
//		if (authAnnotation==null) {
//			commandHandler.putRest(composeCrudCommandName(httpMethod, commandName), commandInstance);
//		} else {
//			it.engaged.system.application.command.Command authenticatedCommandDecorator = new AuthenticatedCommand(context, commandInstance, authAnnotation.mandatory(), authAnnotation.roles());
//			commandHandler.putRest(composeCrudCommandName(httpMethod, commandName), authenticatedCommandDecorator);
//		}
		
	}

	private static void searchForBlobCrudCommand(AbstractCommandHandler commandHandler, Context context, Field classField, Injector injector) throws IllegalArgumentException, IllegalAccessException {
		BlobCrudCommand blobCrudCommandAnnotation = classField.getAnnotation(BlobCrudCommand.class);

		String blobName = classField.get(null).toString();

		if (blobCrudCommandAnnotation!=null) {
			Repository<BinaryEntity> blobRepoAsGeneric = context.getRepository(blobCrudCommandAnnotation.repoName());
			BinaryEntityRepository blobRepo = (BinaryEntityRepository) blobRepoAsGeneric;
			

			if (blobCrudCommandAnnotation.create()) {
				AbstractCommand commandInstance = new PostBinaryEntityCommand(context, blobRepo, blobName);
				commandInstance.setRepositoryService(injector.getInstance(RepositoryService.class));
				commandInstance.setCommandName(blobName);
				commandHandler.putRest(composeCrudCommandName(HttpMethod.POST.name(), blobName), commandInstance);
			}
			if (blobCrudCommandAnnotation.read()) {
				AbstractCommand commandInstance = new GetBinaryEntityCommand(context, blobRepo);
				commandInstance.setRepositoryService(injector.getInstance(RepositoryService.class));
				commandInstance.setCommandName(blobName);
				commandHandler.putRest(composeCrudCommandName(HttpMethod.GET.name(), blobName), commandInstance);
			}
			if (blobCrudCommandAnnotation.update()) {
				AbstractCommand commandInstance = new PutBinaryEntityCommand(context, blobRepo, blobName);
				commandInstance.setRepositoryService(injector.getInstance(RepositoryService.class));
				commandInstance.setCommandName(blobName);
				commandHandler.putRest(composeCrudCommandName(HttpMethod.PUT.name(), blobName), commandInstance);
			}
			if (blobCrudCommandAnnotation.delete()) {
				AbstractCommand commandInstance = new DeleteBinaryEntityCommand(context, blobRepo);
				commandInstance.setRepositoryService(injector.getInstance(RepositoryService.class));
				commandInstance.setCommandName(blobName);
				commandHandler.putRest(composeCrudCommandName(HttpMethod.DELETE.name(), blobName), commandInstance);
			}
			if (blobCrudCommandAnnotation.head()) {
				AbstractCommand commandInstance = new HeadBinaryEntityCommand(context, blobRepo);
				commandInstance.setRepositoryService(injector.getInstance(RepositoryService.class));
				commandInstance.setCommandName(blobName);
				commandHandler.putRest(composeCrudCommandName(HttpMethod.HEAD.name(), blobName), commandInstance);
			}
				
		}

	}


	private static String composeCrudCommandName(String commandNamePrefix, String domainEntityClassName) {
		return GenericCommandNames.composeCommandName(commandNamePrefix, EntityClassNameTranslator.toHyphened(domainEntityClassName));
	}

}
