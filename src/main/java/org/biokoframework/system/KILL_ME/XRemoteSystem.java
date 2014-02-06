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

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;
import org.biokoframework.system.KILL_ME.exception.StackTracePrinter;
import org.biokoframework.system.command.AbstractCommandHandler;
import org.biokoframework.system.command.ICommand;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;


public class XRemoteSystem extends UnicastRemoteObject implements XRmiServerInterface {
	
	private static final String SERVICE = "Service";
	private static final long serialVersionUID = 1L;
	private static int _port = 3232;
	private static String _address = "localhost";
	private static Registry _registry;
	private AbstractCommandHandler _commandHandler = null;
	private final Logger _logger;
	
	
	// TODO MATTO XRemotesystem dovrebbe usare i metodi di XSystem invece di reimplementarli
	public XRemoteSystem(AbstractCommandHandler commandHandler, Logger logger) throws RemoteException {
		_commandHandler = commandHandler;
		_logger = logger;
		start();
	}

	public Fields execute(Fields anInput) {
		Fields output = new Fields();
		try {
			String commandName = anInput.get(FieldNames.COMMAND_NAME);
			ICommand command = _commandHandler.getByName(commandName);
				_logger.info("EXECUTING command: " + commandName + " with INPUT " + anInput.toString());
				output = command.execute(anInput);
				_logger.info("EXECUTED command: " + commandName + " with OUTPUT " + output.toString());
		} catch (Exception e) {
			_logger.error("Exception occurred: " + StackTracePrinter.print(e));
			anInput.putAll(Fields.failed(FieldsErrors.EMPTY_COMMAND));
			output.putAll(anInput);
		}
		return output;
	}
	
	public void start() {
		System.out.println("STARTING RMI server @" + _address + ":" + _port);
//		   if (System.getSecurityManager() == null) {
//				System.setSecurityManager(new RMISecurityManager());
//			   }

		try {
//			_registry = LocateRegistry.getRegistry(_address, _port);
			if (_registry == null) { 
				_registry = LocateRegistry.createRegistry(_port);
				System.out.println("CREATED RMI server @" + _address + ":" + _port);
			}
			_registry.rebind(SERVICE, this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println("STARTED RMI server @" + _address + ":" + _port);
	}
	
	public void stop() {
		System.out.println("STOPPING RMI server @" + _address + ":" + _port);
		try {
//			_registry = LocateRegistry.getRegistry(_address, _port);
			System.out.println("Registry Lookup: " + _registry.lookup(SERVICE));
			System.out.println("Registry List: " + _registry.list());
			_registry.unbind(SERVICE);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		System.out.println("STOPPED RMI server @" + _address + ":" + _port);
	}
}
