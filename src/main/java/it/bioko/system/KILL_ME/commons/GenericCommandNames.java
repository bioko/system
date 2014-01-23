package it.bioko.system.KILL_ME.commons;

import it.bioko.system.entity.EntityClassNameTranslator;


public class GenericCommandNames {

	public static final String COMMAND_LIST = "command-list";
	public static final String COMMAND_INVOCATION_INFO = "command-invocation-info";
	
	public static final String composeRestCommandName(HttpMethod restOperation, String entityName) {
		return composeCommandName(restOperation.name(), EntityClassNameTranslator.toHyphened(entityName));
	}
	
	public static final String composeCommandName(String operationName, String entityName) {
		return new StringBuilder().append(operationName).append("_").append(entityName).toString();
	}
	
	public static final String retriveOperation(String commandName) {
		return commandName.split("_")[0];
	}
	
	public static final String retrieveEntityName(String commandName) {
		return commandName.split("_")[1];
	}

	
	public static final String CRUD_METHOD = "crudMethod";
	public static final String FULL_REGISTRATION = "full-registration";
	public static final String GET_COMMAND_LIST = "getCommandList";
	public static final String MASTER = "master";
	public static final String PRINT_REPOSITORY = "printRepository";
	public static final String PRINT_REPOSITORIES = "printRepositories";
	public static final String SEND_TEST_EMAIL = "sendTestEmail";
	
	public static final String ENGAGED_CHECK_IN = "engaged-check-in";
	public static final String BASIC_CHECK_IN = "check-in";
	public static final String AUTHENTICATED = "authenticated";
	
	public static final String RESOLVABLE = "resolvable";
	public static final String DISSOLVABLE = "dissolvable";
	public static final String UNIQUE_CHECKER = "unique-checker";
	public static final String LOGIN_UNIQUE_CHECK = "login-unique-check";
	
}
