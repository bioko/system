package it.bioko.system.repository.sql.query;

import it.bioko.system.repository.core.query.AbstractConstraint;
import it.bioko.system.repository.core.query.Constraint;
import it.bioko.system.repository.core.query.Query;
import it.bioko.system.repository.sql.translator.SqlTypesTranslator;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;

import java.rmi.server.UID;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SqlConstraint<DE extends DomainEntity> extends AbstractConstraint<DE> implements Constraint<DE> {

	private static final String START_PLACEHOLDER = "<";
	private static final String END_PLACEHOLDER = ">";

	private SqlQuery<DE> _baseQuery;
	private String _fieldName;
	private SqlOperator _operator;
	private boolean _negateOperator;

	private String _placeholder;
	private int _sqlIndex;
	
	private String _value;
	
	public SqlConstraint(SqlQuery<DE> baseQuery) {
		_baseQuery = baseQuery;
	}
	
	@Override
	public SqlConstraint<DE> setFieldName(String fieldName) {
		_fieldName = fieldName;
		return this;
	}

	public SqlConstraint<DE> isEqual() {
		_operator = SqlOperator.EQUALS;
		return this;
	}
	
	@Override
	public SqlQuery<DE> isEqual(String value) {
		_placeholder = new UID().toString();
		isEqual().placeholder(_placeholder);
		_value = value;
		return _baseQuery;
	}
	
	@Override
	public SqlConstraint<DE> like() {
		_operator = SqlOperator.LIKE;
		return this;
	}
	
	@Override
	public SqlQuery<DE> like(String value) {
		_placeholder = new UID().toString();
		like().placeholder(_placeholder);
		_value = value;
		return _baseQuery;
	}
	
	@Override
	public SqlQuery<DE> ilike(String value) {
		_placeholder = new UID().toString();
		ilike().placeholder(_placeholder);
		_value = value;
		return _baseQuery;		
	}
	
	@Override
	public SqlConstraint<DE> ilike() {
		_operator = SqlOperator.ILIKE;
		return this;
	}
	
	@Override
	public Query<DE> slike(String value) {
		_placeholder = new UID().toString();
		slike().placeholder(_placeholder);
		_value = value;
		return _baseQuery;	
	}

	@Override
	public Constraint<DE> slike() {
		_operator = SqlOperator.SLIKE;
		return this;
	}
	
	@Override
	public Query<DE> lt(String value) {
		_placeholder = new UID().toString();
		lt().placeholder(_placeholder);
		_value = value;
		return _baseQuery;
	}

	@Override
	public Constraint<DE> lt() {
		_operator = SqlOperator.LESS_THAN;
		return this;
	}
	
	@Override
	public Query<DE> lte(String value) {
		_placeholder = new UID().toString();
		lte().placeholder(_placeholder);
		_value = value;
		return _baseQuery;
	}

	@Override
	public Constraint<DE> lte() {
		_operator = SqlOperator.LESS_OR_EQUAL_THAN;
		return this;
	}
	
	
	
	
	@Override
	public Query<DE> gt(String value) {
		_placeholder = new UID().toString();
		gt().placeholder(_placeholder);
		_value = value;
		return _baseQuery;
	}

	@Override
	public Constraint<DE> gt() {
		_operator = SqlOperator.GREATER_THAN;
		return this;
	}
	
	@Override
	public Query<DE> gte(String value) {
		_placeholder = new UID().toString();
		gte().placeholder(_placeholder);
		_value = value;
		return _baseQuery;
	}

	@Override
	public Constraint<DE> gte() {
		_operator = SqlOperator.GREATER_OR_EQUAL_THAN;
		return this;
	}
	
	
	
	
	
	
	
	
	

	
	@Override
	public SqlConstraint<DE> not() {
		_negateOperator = true;
		return this;
	}

	@Override
	public SqlQuery<DE> placeholder(String placeholderName) {
		_placeholder = placeholderName;
		_value = null;
		return _baseQuery;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();		
		s.append(_fieldName).append(" ").append(_operator.toString(_negateOperator)).append(" ");
		if (_value != null) {
			s.append(_value);
		} else {
			s.append(START_PLACEHOLDER).append(_placeholder).append(END_PLACEHOLDER);
		}
		return s.toString(); 
	}
	
	public String toSqlString() {
		StringBuilder s = new StringBuilder();
		s.append(_baseQuery.getConnector().createQueryFragment(_fieldName, _operator, _negateOperator));
//		s.append(_fieldName).append(" ").append(_operator.toString(_negateOperator)).append(" ?");
		return s.toString(); 
	}

	public List<String> getPlaceholders() {
		List<String> list = new ArrayList<String>();
		list.add(_placeholder);
		return list;
	}

	public void prepareStatement(PreparedStatement statement, SqlTypesTranslator translator, LinkedHashMap<String, Field> fieldAnnotations) throws SQLException {
		if (_sqlIndex == 0) {
			_baseQuery.setValue(_placeholder, _value);
		}
		
		translator.insertIntoStatement(_fieldName, _value, fieldAnnotations.get(_fieldName), statement, _sqlIndex);
	}
	
	public void setValue(String placeholder, String value, int sqlIndex) {
		if (_placeholder.equals(placeholder)) {
			_value = value;
			_sqlIndex = sqlIndex;
		}
	}

	
	
	

	

	
	
}
