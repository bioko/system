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

import org.biokoframework.system.KILL_ME.FieldsErrors;
import org.biokoframework.system.KILL_ME.commons.logger.Loggers;
import org.biokoframework.system.command.Command;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;


public class MultipleCommand extends Command {
	
	private LinkedHashMap<String, Command> _steps = new LinkedHashMap<String, Command>();
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		Fields overAllOutput = Fields.successful();
		Loggers.xsystem.info("Executing MultipleCommand: " + this.getClass().getSimpleName());
		Loggers.xsystem.info("Steps: " + _steps.size());
		for (Entry<String,Command> step : _steps.entrySet()) {
			Fields output = Fields.empty();
			Loggers.xsystem.info("Executing step: " + step.getKey());
			output = step.getValue().execute(input);
			if (output.contains(FieldsErrors.FAILURE)) {
				output.put(FieldNames.FAILED_COMMAND, step.getValue().getClass().getSimpleName());
				Loggers.xsystem.info("Step " + step.getKey() + " execution failed!");
				return overAllOutput.putAll(Fields.failed()).putAll(output);
			} else if (output.contains(FieldsErrors.ERROR)) {
				output.put(FieldNames.ERROR_COMMAND, step.getValue().getClass().getSimpleName());
				Loggers.xsystem.info("Step " + step.getKey() + " execution error!");
				return overAllOutput.putAll(Fields.failed()).putAll(output);
			}
			overAllOutput.putAll(output);

			input.putAll(output);

		}
		Loggers.xsystem.info("MultipleCommand output: " + overAllOutput.asString());
		Loggers.xsystem.info("End MultipleCommand: " + this.getClass().getSimpleName());
		return overAllOutput;
	}
	
	public void addStep(String aStepKey, Command aCommand) {
		Loggers.xsystem.info("Adding step: " + aStepKey + " to MultipleCommand " + this.getClass().getSimpleName());
		_steps.put(aStepKey, aCommand);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}