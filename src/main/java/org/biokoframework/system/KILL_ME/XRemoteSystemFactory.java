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

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.command.CommandHandlerImpl;


public class XRemoteSystemFactory {

	private static Hashtable<ConfigurationEnum, CommandHandlerImpl> _commandHandlers = new Hashtable<ConfigurationEnum, CommandHandlerImpl>();

	public XRemoteSystem createOn(ConfigurationEnum aConfiguration, Logger logger) {
		logger.info(report());
		try {
			return new XRemoteSystem(_commandHandlers.get(aConfiguration), logger);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void put(ConfigurationEnum configurationEnum, CommandHandlerImpl aCommandHandler) {
		_commandHandlers.put(configurationEnum, aCommandHandler);
	}
	
	public static String report() {
		StringBuffer result = new StringBuffer(XRemoteSystemFactory.class.getSimpleName());
		result.append(": \n");
		for (Entry<ConfigurationEnum, CommandHandlerImpl> each : _commandHandlers.entrySet()) {
			result.append(each.getKey());
			result.append("=");
			result.append(each.getValue());
			result.append("\n");
		}
		return result.toString();
	}
}