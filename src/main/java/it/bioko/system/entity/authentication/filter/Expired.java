package it.bioko.system.entity.authentication.filter;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.entity.authentication.Authentication;
import it.bioko.utils.filter.Filter;

public class Expired implements Filter<Authentication> {

	private final long _now;

	public Expired() {
		_now = System.currentTimeMillis() / 1000;
	}
	
	@Override
	public boolean allows(Authentication entity) {
		long expiration = Long.parseLong(entity.get(GenericFieldNames.AUTH_TOKEN_EXPIRE));
		return _now > expiration ;
	}
	
	public static Filter<Authentication> expired() {
		return new Expired();
	}

}
