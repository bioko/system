package it.bioko.system.service.context;

import it.bioko.system.KILL_ME.XSystemIdentityCard;
import it.bioko.system.KILL_ME.exception.SystemException;
import it.bioko.system.context.Context;

public interface ContextFactory {
	
	public Context create(XSystemIdentityCard identityCard) throws SystemException;

}
