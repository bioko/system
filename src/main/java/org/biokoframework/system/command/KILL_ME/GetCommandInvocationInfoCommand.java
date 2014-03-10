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

package org.biokoframework.system.command.KILL_ME;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.biokoframework.system.KILL_ME.commons.GenericCommandNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.AbstractCommandHandler;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.command.ICommand;
import org.biokoframework.system.entity.description.CommandEntity;
import org.biokoframework.system.entity.description.CommandEntityBuilder;
import org.biokoframework.system.entity.description.ParameterEntity;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

@Deprecated
public class GetCommandInvocationInfoCommand extends AbstractCommand {
	
	private static final Logger LOGGER = Logger.getLogger(GetCommandInvocationInfoCommand.class);
	private AbstractCommandHandler fCommandHandler;

	@Override
	public void onContextInitialized() {
		fCommandHandler = fContext.getCommandHandler();
	}

	// TODO MATTO I
	// usando componingInput e outputKey si possono implementare i controlli in ingresso e 
	// uscita verificando che i campi obbligatori siano presenti
	@Override
	public Fields execute(Fields input) throws CommandException {
		LOGGER.info("EXECUTING Command:" + this.getClass().getSimpleName());
		Fields result = new Fields();
		
		Fields commandEntityFields = new Fields();
		commandEntityFields.put(GenericFieldNames.NAME, input.get(GenericFieldNames.COMMAND));

		String commandName = input.get(GenericFieldNames.COMMAND);
		if (commandName == null) {
			throw CommandExceptionsFactory.createExpectedFieldNotFound(GenericFieldNames.COMMAND);
		}
		ICommand command = fCommandHandler.getByName(commandName);
		if (command == null) {
			LOGGER.error("Command " + commandName + " not found");
			throw CommandExceptionsFactory.createCommandNotFoundException(commandName);
		}
		
		ArrayList<ParameterEntity> inputParameters = extractInputParameters(commandName, command);
		commandEntityFields.put(GenericFieldNames.INPUT, inputParameters);
		
		ArrayList<ParameterEntity> parameters = extractOutputParameters(commandName, command);
		commandEntityFields.put(GenericFieldNames.OUTPUT, parameters);
		
		CommandEntity entity = new CommandEntity();
		entity.setAll(commandEntityFields);
		ArrayList<DomainEntity> response = new ArrayList<DomainEntity>();
		response.add(entity);
		result.put(GenericFieldNames.RESPONSE, response);
			
		LOGGER.info("END Command:" + this.getClass().getSimpleName());
		return input.putAll(result);
	}

	private ArrayList<ParameterEntity> extractInputParameters(String commandName, ICommand command) {
		ArrayList<ParameterEntity> parameters = null;
		if (command instanceof SetCommand) {
			parameters = ((SetCommand)command).componingInputKeys(commandName).get(GenericFieldNames.INPUT);
		} else {
			parameters = command.componingInputKeys().get(GenericFieldNames.INPUT);
		}
		return parameters;
	}

	private ArrayList<ParameterEntity> extractOutputParameters(String commandName, ICommand command) {
		ArrayList<ParameterEntity> parameters = null;
		if (command instanceof SetCommand) {
			parameters = ((SetCommand)command).componingOutputKeys(commandName).get(GenericFieldNames.OUTPUT);
		} else {
			parameters = command.componingOutputKeys().get(GenericFieldNames.OUTPUT);
		}
		return parameters;
	}
	
	@Override
	public Fields componingInputKeys() {
		ArrayList<ParameterEntity> request = new ArrayList<ParameterEntity>(); 
		
		ParameterEntity parameter = new ParameterEntity();
		parameter.set(ParameterEntity.NAME, GenericFieldNames.COMMAND);
		parameter.set(ParameterEntity.HTTP_PARAMETER_TYPE, GenericFieldValues.QUERY_STRING);
		request.add(parameter);
		
		Fields fields = new Fields(
				FieldNames.COMMAND_NAME, GenericCommandNames.GET_COMMAND_LIST,
				GenericFieldNames.INPUT, request);
		return fields;
	}

	@Override
	public Fields componingOutputKeys() {
		CommandEntity entity = new CommandEntityBuilder().build(false);
		List<String> keys = entity.fields().keys();
		
		ArrayList<DomainEntity> response = new ArrayList<DomainEntity>();
		CommandEntityBuilder builder = new CommandEntityBuilder();
		for (String aKey : keys) {
			builder.set(CommandEntity.NAME, aKey);
			response.add(builder.build(false));
		}		
		
		return new Fields(GenericFieldNames.OUTPUT, response);
	}

}