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

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.fields.Fields;

public class CommandHandlerImpl extends AbstractCommandHandler {

	private LinkedHashMap<String, ICommand> fCommands = new LinkedHashMap<String, ICommand>();
	private String fSystem;
	private String fVersion;

	public CommandHandlerImpl(String system, String version) {
		fSystem = system;
		fVersion = version;
	}

	public static CommandHandlerImpl empty(String aSystem, String aVersion) {
		return new CommandHandlerImpl(aSystem, aVersion);
	}
	
	@Override
	public void put(String aCommandName, ICommand aCommand){
		fCommands.put(aCommandName, aCommand);
	}

	@Override
	public ICommand getByName(String aCommandName) throws CommandException {
		try {
			return fCommands.get(aCommandName);
		} catch (NullPointerException npe) {
			Fields fields = new Fields(ErrorEntity.ERROR_MESSAGE, "Command name not found: " + aCommandName);
			ErrorEntity error = new ErrorEntity();
			error.setAll(fields);
			throw new CommandException(error, npe);
		}
	}

	@Override
	public String report() {
		StringBuffer result = new StringBuffer(CommandHandlerImpl.class.getSimpleName());
		result.append(": System=" + fSystem);
		result.append(" Version=" + fVersion);
		result.append(": \n");
		for (Entry<String, ICommand> each : fCommands.entrySet()) {
			result.append(each.getKey());
			result.append("=");
			result.append(each.getValue());
			result.append("\n");
		}
		return result.toString();
	}

	@Override
	public Set<String> keys() {
		return fCommands.keySet();
	}

	@Override
	public void putRest(String aRestCommandName, ICommand aCommand) {
		fCommands.put(aRestCommandName, aCommand);
	}
}