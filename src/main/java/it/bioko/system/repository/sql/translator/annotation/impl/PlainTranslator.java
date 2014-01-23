package it.bioko.system.repository.sql.translator.annotation.impl;

import it.bioko.system.repository.sql.translator.annotation.Translator;
import it.bioko.utils.domain.annotation.field.Field;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PlainTranslator implements Translator {

	private String _to;
	
	@Override
	public void setTo(String to) {
		_to = to;
	}
	
	@Override
	public String selectDBType(String fieldName, Field fieldAnnotation, List<String> additionalConstraints) {
		StringBuilder dbType = new StringBuilder(_to);
		if (fieldAnnotation.mandatory()) {
			dbType.append(" not null");
		}
		return dbType.toString();
	}

	@Override
	public void insertIntoStatement(String fieldName, String fieldValue, Field fieldAnnotation, PreparedStatement statement, int sqlIndex) throws SQLException {
		statement.setString(sqlIndex, fieldValue);
	}

	@Override
	public String convertFromDBValue(String fieldName, ResultSet resultset, Field fieldAnnotation) throws SQLException {
		return resultset.getString(fieldName);
	}

}
