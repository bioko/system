package it.bioko.system.repository.sql.query;

public enum SqlMethod {
	
	SELECT("select"),
	UPDATE("update"),
	DELETE("delete");
	
	private String _value;

	SqlMethod(String value) {
		_value = value;
	}
	
	public String toString() {
		return _value;
	}

}
