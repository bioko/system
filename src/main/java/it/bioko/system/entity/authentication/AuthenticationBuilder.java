package it.bioko.system.entity.authentication;

import it.bioko.utils.domain.EntityBuilder;


public class AuthenticationBuilder extends EntityBuilder<Authentication> {	
	
	public static final String EXAMPLE = "example";
	public static final String EXAMPLE_JSON = 
			"{'id':'1','authToken':'00000000-0000-0000-0000-000000000000','authTokenExpire':'3185586732','loginId':'1'}";
		
	public AuthenticationBuilder() {
		super(Authentication.class);
		putExample(EXAMPLE, EXAMPLE_JSON);
	}

	@Override
	public EntityBuilder<Authentication> loadDefaultExample() {
		return loadExample(EXAMPLE);
	}

}
