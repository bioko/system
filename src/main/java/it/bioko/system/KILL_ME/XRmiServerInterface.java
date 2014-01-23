package it.bioko.system.KILL_ME;

import it.bioko.utils.fields.Fields;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface XRmiServerInterface extends Remote {
	public Fields execute(Fields anInput) throws RemoteException;
}
