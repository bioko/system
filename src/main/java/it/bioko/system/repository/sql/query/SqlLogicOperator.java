package it.bioko.system.repository.sql.query;

public enum SqlLogicOperator {

	NONE(""), 
	AND("and"), 
	OR("or");
	
	private String _value;

	SqlLogicOperator(String value) {
		_value = value;
	}

	@Override
	public String toString() {
		return _value;
	}
	
}
