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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.logger.Loggers;
import org.biokoframework.system.command.AbstractCommandHandler;
import org.biokoframework.system.command.Command;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

public class MultipleCommandPlusPlus extends Command {

	private String _name;
	private AbstractCommandHandler _commandHandler;
	private LinkedHashMap<String, String> _steps = new LinkedHashMap<String, String>();

	public MultipleCommandPlusPlus(String commandName, AbstractCommandHandler commandHandler) {
		_name = commandName;
		_commandHandler = commandHandler;
	}
	
	public MultipleCommandPlusPlus() {
	}
	
		
	

	@Override
	public final Fields execute(Fields input) throws CommandException {
		Loggers.xsystem.info("EXECUTING Multiple Command");
		Loggers.xsystem.info("STEPS: " + _steps.size());
		Loggers.xsystem.info("INPUT: " + input.toString());
		
		if (_commandHandler==null)
			_commandHandler = _context.getCommandHandler();
		
		Fields result = Fields.empty();
		ArrayList<Object> response = new ArrayList<Object>();
		
		Fields stepInput = input.copy();
		for (Entry<String, String> aStep: _steps.entrySet()) {
			Loggers.xsystem.info("Executing step: " + aStep.getKey());
						
			Command stepCommand = _commandHandler.getByName(aStep.getValue());
			if (stepCommand == null) {
				throw CommandExceptionsFactory.createCommandNotFoundException(aStep.getValue());
			} else {
				stepInput.put(FieldNames.COMMAND_NAME, aStep.getValue());
				Fields stepOutput = stepCommand.execute(stepInput);
				
				Collection<?> stepResponse = stepOutput.get(GenericFieldNames.RESPONSE);
				prepareNextStepInput(aStep.getKey(), stepOutput, stepInput);
				
				if (stepResponse!=null)
					response.addAll(stepResponse);				
			}
		}
		
		postProcessingCleanUp(response, result);
		
		Loggers.xsystem.info("MultipleCommand output: " + result.toString());
		Loggers.xsystem.info("END MultipleCommand");
		return result;
	}

	protected void postProcessingCleanUp(ArrayList<Object> response, Fields result) {
		result.put(GenericFieldNames.RESPONSE, response);
	}


	protected void prepareNextStepInput(String prevStepName, Fields prevStepOutput, Fields nextStepInput) {
		prevStepOutput.remove(GenericFieldNames.RESPONSE);
		nextStepInput.putAll(prevStepOutput);
	}
	
	@Override
	public final String getName() {
		return _name;
	}

	public MultipleCommandPlusPlus addStep(String stepName, String commandName) {
		_steps.put(stepName, commandName);
		return this;
	}

}
