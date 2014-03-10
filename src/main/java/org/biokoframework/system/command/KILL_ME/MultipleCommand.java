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

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.biokoframework.system.KILL_ME.FieldsErrors;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

@Deprecated
public class MultipleCommand extends AbstractCommand {
	
	private static final Logger LOGGER = Logger.getLogger(MultipleCommand.class);
	
	private LinkedHashMap<String, AbstractCommand> _steps = new LinkedHashMap<String, AbstractCommand>();
	
	@Override
	public Fields execute(Fields input) throws CommandException, ValidationException {
		Fields overAllOutput = Fields.successful();
		LOGGER.info("Executing MultipleCommand: " + this.getClass().getSimpleName());
		LOGGER.info("Steps: " + _steps.size());
		for (Entry<String,AbstractCommand> step : _steps.entrySet()) {
			Fields output = new Fields();
			LOGGER.info("Executing step: " + step.getKey());
			output = step.getValue().execute(input);
			if (output.containsKey(FieldsErrors.FAILURE)) {
				output.put(FieldNames.FAILED_COMMAND, step.getValue().getClass().getSimpleName());
				LOGGER.info("Step " + step.getKey() + " execution failed!");
				return overAllOutput.putAll(Fields.failed()).putAll(output);
			} else if (output.containsKey(FieldsErrors.ERROR)) {
				output.put(FieldNames.ERROR_COMMAND, step.getValue().getClass().getSimpleName());
				LOGGER.info("Step " + step.getKey() + " execution error!");
				return overAllOutput.putAll(Fields.failed()).putAll(output);
			}
			overAllOutput.putAll(output);

			input.putAll(output);

		}
		LOGGER.info("MultipleCommand output: " + overAllOutput.toString());
		LOGGER.info("End MultipleCommand: " + this.getClass().getSimpleName());
		return overAllOutput;
	}
	
	public void addStep(String aStepKey, AbstractCommand aCommand) {
		LOGGER.info("Adding step: " + aStepKey + " to MultipleCommand " + this.getClass().getSimpleName());
		_steps.put(aStepKey, aCommand);
	}

}