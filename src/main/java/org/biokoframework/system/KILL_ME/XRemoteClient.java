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
import org.biokoframework.system.KILL_ME.exception.StackTracePrinter;
import org.biokoframework.utils.fields.Fields;

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@Deprecated
public class XRemoteClient {

	private XRmiServerInterface _server;
	private final Logger _logger;
	
	public XRemoteClient(String serverAddress, int serverPort, Logger logger) {
		_logger = logger;
		try {
//			   if (System.getSecurityManager() == null) {
//					System.setSecurityManager(new RMISecurityManager());
//				   }
			_logger.info("Locating server: " + serverAddress + ":" + serverPort);
			_server = locateServer(serverAddress, serverPort);
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Fields invokeServer(Fields input) {
		try {
			_logger.info("Invoking XRemoteServer - input: " + input.toString());
			Fields output = _server.execute(input);
			_logger.info("Invoking XRemoteServer - output: " + output.toString());
			return output;
		} catch (RemoteException e) {
			_logger.error("Exception occurred: " + StackTracePrinter.print(e));
			return Fields.error("Remote Exception");
		}
	}

	private static XRmiServerInterface locateServer(String serverAddress,
			int serverPort) throws RemoteException, NotBoundException, AccessException, MalformedURLException {
		Registry registry = LocateRegistry.getRegistry(serverAddress, serverPort);
		XRmiServerInterface server = (XRmiServerInterface)registry.lookup("Service");
		
		return server;
	}
}