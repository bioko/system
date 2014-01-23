package it.bioko.system.KILL_ME;

import it.bioko.system.KILL_ME.exception.StackTracePrinter;
import it.bioko.utils.fields.Fields;

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;


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
			_logger.info("Invoking XRemoteServer - input: " + input.asString());
			Fields output = _server.execute(input);
			_logger.info("Invoking XRemoteServer - output: " + output.asString());
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