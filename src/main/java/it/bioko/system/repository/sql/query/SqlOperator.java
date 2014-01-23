package it.bioko.system.repository.sql.query;

public enum SqlOperator {
	EQUALS("=", "!="),
	LIKE("like", "not like"),
	ILIKE("ilike", null),
	SLIKE("slike", null),
	LESS_THAN("<", ">="),
	LESS_OR_EQUAL_THAN("<=", ">"),
	GREATER_THAN(">","<="),
	GREATER_OR_EQUAL_THAN(">=","<")
	;
	
	private String _symbol;
	private String _negation;

	SqlOperator(String symbol, String negation) {
		_symbol = symbol;
		_negation = negation;
	}
	
	public String toString(boolean negate) {
		if (negate) {
			return _negation;
		} else {
			return _symbol;
		}
	}
}
