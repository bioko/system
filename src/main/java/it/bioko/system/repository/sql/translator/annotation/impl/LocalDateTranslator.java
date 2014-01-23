package it.bioko.system.repository.sql.translator.annotation.impl;

import it.bioko.system.repository.sql.translator.annotation.Translator;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.validator.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class LocalDateTranslator implements Translator {

	private static final String MYSQL_DATE = "yyyy-MM-dd";
	
	@Override
	public String selectDBType(String fieldName, Field fieldAnnotation, List<String> additionalConstraints) {
		return "date";
	}

	@Override
	public void insertIntoStatement(String fieldName, String fieldValue, Field fieldAnnotation, PreparedStatement statement, int sqlIndex) throws SQLException {
		if (fieldValue == null) {
			statement.setObject(sqlIndex, null);
		} else {
			LocalDate timestamp = DateTimeFormat.forPattern(Validator.ISO_DATE).parseLocalDate(fieldValue);
			statement.setString(sqlIndex, timestamp.toString(DateTimeFormat.forPattern(MYSQL_DATE)));
		}
	}

	@Override
	public String convertFromDBValue(String fieldName, ResultSet resultset, Field fieldAnnotation) throws SQLException {
		String sqlFieldValue = resultset.getString(fieldName);
		if (sqlFieldValue == null) {
			return null;
		} else {
			LocalDate timestamp = DateTimeFormat.forPattern(MYSQL_DATE).parseLocalDate(sqlFieldValue);
			return timestamp.toString();
		}
	}

	@Override
	public void setTo(String to) {
	}

}
