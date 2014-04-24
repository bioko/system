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

package org.biokoframework.system.KILL_ME;

import org.apache.log4j.Logger;
import org.biokoframework.utils.exception.BiokoException;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

@Deprecated
public class XSystem {

	private final Logger fLogger;

	public XSystem() {
		fLogger = Logger.getLogger(getClass());
	}
	
	public Fields execute(Fields input) throws BiokoException {
		Fields output = new Fields();
		String commandName = input.get(FieldNames.COMMAND_NAME);
//		try {
		fLogger.info("----- Executing Command: " + commandName + " -----");
		fLogger.info("Command input: " + input.toString());
			
//		} catch (BiokoException systemException) {
//			fLogger.error("System exception", systemException);
//			throw systemException;
//		} catch (Exception exception) {
//			fLogger.error("Generic exception", exception);
//			throw new CommandException(exception);
//		}
		fLogger.info("Command output: " + output.toString());
		fLogger.info("----- Command execution finished -----");
		return output;
	}

	public void shutdown() {
//		fLogger.info("System: " + fContext.getSystemName() + " is going down");
//		fLogger.info("Bye! Bye!");
//		List<SystemListener> listeners = fContext.getSystemListeners();
//		for (SystemListener aListener : listeners) {
//			aListener.systemShutdown();
//		}
	}
}
