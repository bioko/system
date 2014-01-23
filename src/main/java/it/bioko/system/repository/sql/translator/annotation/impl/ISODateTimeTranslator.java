package it.bioko.system.repository.sql.translator.annotation.impl;

import it.bioko.system.repository.sql.translator.annotation.Translator;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.validator.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class ISODateTimeTranslator implements Translator {

	private static final String MYSQL_TIMESTAMP = "yyyy-MM-dd HH:mm:ss.S";
	
	@Override
	public void setTo(String to) {
	}

	@Override
	public String selectDBType(String fieldName, Field fieldAnnotation, List<String> additionalConstraints) {
		return "datetime";
	}

	@Override
	public void insertIntoStatement(String fieldName, String fieldValue, Field fieldAnnotation, PreparedStatement statement, int sqlIndex) throws SQLException {
		if (fieldValue == null) {
			statement.setObject(sqlIndex, null);
		} else {
			DateTime timestamp = DateTimeFormat.forPattern(Validator.ISO_TIMESTAMP).parseDateTime(fieldValue);
			statement.setString(sqlIndex, timestamp.toString(DateTimeFormat.forPattern(MYSQL_TIMESTAMP)));
		}
	}

	@Override
	public String convertFromDBValue(String fieldName, ResultSet resultset, Field fieldAnnotation) throws SQLException {
		String sqlFieldValue = resultset.getString(fieldName);
		if (sqlFieldValue == null) {
			return null;
		} else {				
			DateTime timestamp = DateTimeFormat.forPattern(MYSQL_TIMESTAMP).parseDateTime(sqlFieldValue);
			return timestamp.toString(Validator.ISO_TIMESTAMP);
		}
	}

}
