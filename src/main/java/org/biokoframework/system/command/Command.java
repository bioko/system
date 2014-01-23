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

package org.biokoframework.system.command;

import org.apache.log4j.Logger;
import org.biokoframework.system.context.Context;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.json.JSonBuilder;


public abstract class Command {
	
	protected Context _context;
	protected String _commandName;
	
	public abstract Fields execute(Fields input) throws CommandException;
	
	public void fillInvocationInfo(Fields output) {
		try {
			output.put(FieldNames.COMMAND_INVOCATION_INPUT_INFO, new JSonBuilder().buildFrom(componingInputKeys()));
			output.put(FieldNames.COMMAND_INVOCATION_OUTPUT_INFO, new JSonBuilder().buildFrom(componingOutputKeys()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Fields componingInputKeys() {
		return Fields.empty();
	}

	public Fields componingOutputKeys() {
		return Fields.empty();
	}

	
	public String getName() {
		return _commandName;
	}
	
	public void setCommandName(String commandName) {
		_commandName = commandName;
	}
	
	public void setContext(Context context) {
		_context = context;
	}
	
	public void onContextInitialized() {
		
	}
	
	protected void logInput(Fields input) { 
		Logger logger = _context.getLogger();
		logger.info("EXECUTING Command:" + this.getClass().getSimpleName());
		logger.info("INPUT: " + input.asString());
	}
	
	protected void logOutput(Fields output) { 
		Logger logger = _context.getLogger();
		
		if (output==null)		
			logger.info("OUTPUT after execution: (nothing)");
		else
			logger.info("OUTPUT after execution: "+output.asString());
		
		logger.info("END Command:" + this.getClass().getSimpleName());
	}
	
	protected void logOutput() {
		logOutput(null);
	}
	
	
	
}