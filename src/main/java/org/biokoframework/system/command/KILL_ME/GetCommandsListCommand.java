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

import org.apache.log4j.Logger;
import org.biokoframework.system.KILL_ME.commons.GenericCommandNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.AbstractCommandHandler;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.description.CommandEntity;
import org.biokoframework.system.entity.description.ParameterEntity;
import org.biokoframework.system.entity.description.ParameterEntityBuilder;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

import java.util.ArrayList;

@Deprecated
public class GetCommandsListCommand extends AbstractCommand {
	
	private static final Logger LOGGER = Logger.getLogger(GetCommandsListCommand.class);
	
	private AbstractCommandHandler _commandHandler;

	public GetCommandsListCommand(AbstractCommandHandler commandHandler){
		_commandHandler = commandHandler;
	}
	
	public GetCommandsListCommand() {		
	}
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		LOGGER.info("EXECUTING Command:" + this.getClass().getSimpleName());
		Fields result = new Fields();
		
		// Meta linguaggio? Il comando descrive il command handler con lo stesso meccanismo con cui
		// descriverebbe qualunque altra cosa
		ArrayList<DomainEntity> response = new ArrayList<DomainEntity>();
		for (String aCommandName : _commandHandler.keys()) {
			CommandEntity commandEntity = new CommandEntity();
			commandEntity.set(CommandEntity.NAME, aCommandName);
			response.add(commandEntity);
		}
		result.put(GenericFieldNames.RESPONSE, response);
		result.putAll(input);
		
		LOGGER.info("END Command:" + this.getClass().getSimpleName());
		return result;
	}

	@Override
	public Fields componingInputKeys() {
		ArrayList<ParameterEntity> request = new ArrayList<ParameterEntity>(); 
		
		Fields fields = new Fields(
				FieldNames.COMMAND_NAME, GenericCommandNames.GET_COMMAND_LIST,
				GenericFieldNames.INPUT, request);
		return fields;
	}

	@Override
	public Fields componingOutputKeys() {
		ArrayList<DomainEntity> response = new ArrayList<DomainEntity>();
		ParameterEntityBuilder builder = new ParameterEntityBuilder();
		
		builder.set(ParameterEntity.NAME, ParameterEntity.NAME);
		response.add(builder.build(false));
		
		return new Fields(GenericFieldNames.OUTPUT, response);
	}

}