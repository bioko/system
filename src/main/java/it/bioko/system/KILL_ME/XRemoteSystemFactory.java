package it.bioko.system.KILL_ME;

import it.bioko.system.ConfigurationEnum;
import it.bioko.system.command.CommandHandlerImpl;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.apache.log4j.Logger;


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