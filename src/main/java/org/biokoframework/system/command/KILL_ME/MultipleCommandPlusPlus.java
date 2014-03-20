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
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.AbstractCommandHandler;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.command.ICommand;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

@Deprecated
public class MultipleCommandPlusPlus extends AbstractCommand {

	private static final Logger LOGGER = Logger.getLogger(MultipleCommandPlusPlus.class);
	
	private AbstractCommandHandler fCommandHandler;
	private LinkedHashMap<String, String> fSteps = new LinkedHashMap<String, String>();

	public MultipleCommandPlusPlus(String commandName, AbstractCommandHandler commandHandler) {
		fCommandHandler = commandHandler;
	}
	
	public MultipleCommandPlusPlus() {
	}
	
		
	

	@Override
	public final Fields execute(Fields input) throws CommandException, ValidationException {
		LOGGER.info("EXECUTING Multiple Command");
		LOGGER.info("STEPS: " + fSteps.size());
		LOGGER.info("INPUT: " + input.toString());
		
		if (fCommandHandler==null)
			fCommandHandler = fContext.getCommandHandler();
		
		Fields result = new Fields();
		ArrayList<Object> response = new ArrayList<Object>();
		
		Fields stepInput = input.copy();
		for (Entry<String, String> aStep: fSteps.entrySet()) {
			LOGGER.info("Executing step: " + aStep.getKey());
						
			ICommand stepCommand = fCommandHandler.getByName(aStep.getValue());
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
		
		LOGGER.info("MultipleCommand output: " + result.toString());
		LOGGER.info("END MultipleCommand");
		return result;
	}

	protected void postProcessingCleanUp(ArrayList<Object> response, Fields result) {
		result.put(GenericFieldNames.RESPONSE, response);
	}


	protected void prepareNextStepInput(String prevStepName, Fields prevStepOutput, Fields nextStepInput) {
		prevStepOutput.remove(GenericFieldNames.RESPONSE);
		nextStepInput.putAll(prevStepOutput);
	}
	
	public MultipleCommandPlusPlus addStep(String stepName, String commandName) {
		fSteps.put(stepName, commandName);
		return this;
	}

}
