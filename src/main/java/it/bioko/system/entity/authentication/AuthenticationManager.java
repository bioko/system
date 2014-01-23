package it.bioko.system.entity.authentication;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.context.Context;
import it.bioko.system.entity.login.Login;
import it.bioko.utils.fields.Fields;

import java.util.UUID;


public class AuthenticationManager {

	public static Authentication createAuthenticationFor(Context context, Login login) {
		Long validityIntervalSecs = Long.parseLong(context.getSystemProperty(Context.AUTHENTICATION_VALIDITY_INTERVAL_SECS));
		
		Fields fields = Fields.empty();
		fields.put(GenericFieldNames.LOGIN_ID, login.get(Login.ID));
		fields.put(Authentication.ROLES, login.get(Login.ROLES));
		fields.put(GenericFieldNames.AUTH_TOKEN, UUID.randomUUID().toString());
		long utcTimeSecs = System.currentTimeMillis() / 1000 + validityIntervalSecs;
		fields.put(GenericFieldNames.AUTH_TOKEN_EXPIRE, Long.toString(utcTimeSecs));
		return new Authentication(fields);
	}
	
	public static boolean isExpired(Authentication authentication) {
		long expireTimeSecs = Long.parseLong(authentication.get(GenericFieldNames.AUTH_TOKEN_EXPIRE));
		long nowSecs = System.currentTimeMillis() / 1000;
		return nowSecs < expireTimeSecs;
	}
	
	public static void renewAuthentication(Context context, Authentication authentication) {
		Long validityIntervalSecs = Long.parseLong(context.getSystemProperty(Context.AUTHENTICATION_VALIDITY_INTERVAL_SECS));
		
		long nowSecs = System.currentTimeMillis() / 1000;
		authentication.fields().put(GenericFieldNames.AUTH_TOKEN_EXPIRE, Long.toString(nowSecs + validityIntervalSecs));
	}
}
