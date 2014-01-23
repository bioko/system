package it.bioko.system.entity.login;

import it.bioko.utils.domain.EntityBuilder;


public class LoginBuilder extends EntityBuilder<Login> {
	
	public static final String MATTIA = "mattia";
	private static final String MATTIA_JSON = 
			"{'id':'1', 'userEmail':'m.tortorelli@engaged.it','password':'1234'}";

	public static final String SIMONE = "simone";
	private static final String SIMONE_JSON = 
			"{'id':'2', 'userEmail':'s.mangano@engaged.it','password':'1234'}";

	public static final String MIKOL = "mikol";
	private static final String MIKOL_JSON = 
			"{'id':'3', 'userEmail':'mikol.faro@gmail.com','password':'1234'}";
	
	public static final String GENERIC_USER_WITHOUT_ROLE = "genericUserWORole";
	public static final String GENERIC_USER_WITH_ADMIN_ROLE = "genericUserWithAdminRole";
	public static final String GENERIC_USER_WITH_ANOTHER_ROLE = "genericUserWithAnotherRole";
	public static final String GENERIC_USER_WITH_BOTH_ROLES = "genericUserWithBothRoles";

	public static final String MATTIA_ENCRYPTED = "mattiaEncrypted";
	
	public LoginBuilder() {
		super(Login.class);

		putExample(MATTIA, MATTIA_JSON);
		putExample(SIMONE, SIMONE_JSON);
		putExample(MIKOL, MIKOL_JSON);
		
		putExample(GENERIC_USER_WITHOUT_ROLE, "{'id':'4', 'userEmail':'genericWithoutRole@gmail.com','password':'1234'}");
		putExample(GENERIC_USER_WITH_ADMIN_ROLE, "{'id':'5', 'userEmail':'genericWithAdminRole@gmail.com','password':'1234','roles':'admin'}");
		putExample(GENERIC_USER_WITH_ANOTHER_ROLE, "{'id':'6', 'userEmail':'genericWithAnotherRole@gmail.com','password':'1234','roles':'another'}");
		putExample(GENERIC_USER_WITH_BOTH_ROLES, "{'id':'7', 'userEmail':'genericWithBothRoles@gmail.com','password':'1234','roles':'admin|another'}");
		
		putExample(MATTIA_ENCRYPTED, "{'id':'1', 'userEmail':'m.tortorelli@engaged.it','password':'1234'}");
		
	}
	
	@Override
	public EntityBuilder<Login> loadDefaultExample() {
		return loadExample(MATTIA);
	}
}
