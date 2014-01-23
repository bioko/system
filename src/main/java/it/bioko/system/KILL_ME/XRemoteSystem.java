package it.bioko.system.KILL_ME;

import it.bioko.system.KILL_ME.exception.StackTracePrinter;
import it.bioko.system.command.AbstractCommandHandler;
import it.bioko.system.command.Command;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;


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
		Fields output = Fields.empty();
		try {
			String commandName = (String)anInput.stringNamed(FieldNames.COMMAND_NAME);
			Command command = _commandHandler.getByName(commandName);
				_logger.info("EXECUTING command: " + commandName + " with INPUT " + anInput.asString());
				output = command.execute(anInput);
				_logger.info("EXECUTED command: " + commandName + " with OUTPUT " + output.asString());
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
