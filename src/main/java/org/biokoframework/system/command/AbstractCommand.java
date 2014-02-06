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
import org.biokoframework.system.repository.service.RepositoryService;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.json.JSonBuilder;

import com.google.inject.Inject;


public abstract class AbstractCommand implements ICommand {
	
	protected Context fContext;
	protected String fCommandName;
	
	private RepositoryService fService;
	
	@Inject
	public final void setRepositoryService(RepositoryService service) {
		fService = service;
	}
	
	public void fillInvocationInfo(Fields output) {
		try {
			output.put(FieldNames.COMMAND_INVOCATION_INPUT_INFO, new JSonBuilder().buildFrom(componingInputKeys()));
			output.put(FieldNames.COMMAND_INVOCATION_OUTPUT_INFO, new JSonBuilder().buildFrom(componingOutputKeys()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Fields componingInputKeys() {
		return new Fields();
	}

	public Fields componingOutputKeys() {
		return new Fields();
	}

	
	public String getName() {
		return fCommandName;
	}
	
	public void setCommandName(String commandName) {
		fCommandName = commandName;
	}
	
	public void setContext(Context context) {
		fContext = context;
	}
	
	public void onContextInitialized() {
		
	}
	
	protected void logInput(Fields input) { 
		Logger logger = fContext.getLogger();
		logger.info("EXECUTING Command:" + this.getClass().getSimpleName());
		logger.info("INPUT: " + input.toString());
	}
	
	protected void logOutput(Fields output) { 
		Logger logger = fContext.getLogger();
		
		if (output == null)		
			logger.info("OUTPUT after execution: (nothing)");
		else
			logger.info("OUTPUT after execution: " + output.toString());
		
		logger.info("END Command:" + this.getClass().getSimpleName());
	}
	
	protected void logOutput() {
		logOutput(null);
	}
	
	
	
}