package it.bioko.system.command.crud;

public enum CrudMethod {
		POST("SAVE"),
		GET("RETRIEVE"),
		PUT("SAVE"),
		DELETE("DELETE"),
		OPTIONS("DESCRIBE");
		
	private final String _crudCommand;
	
	private CrudMethod(String aCrudCommand) {
		_crudCommand = aCrudCommand;
	}

	public static CrudMethod fromRestCommand(String aCommandName) {
		String restMethod = aCommandName.split("_")[0];
		return valueOf(restMethod);
	}
	
	public String value() {
		return _crudCommand;
	}
}