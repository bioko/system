package it.bioko.system.repository.sql.translator.annotation.impl;

import it.bioko.system.repository.sql.translator.annotation.Translator;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.domain.annotation.hint.Hint;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StringTranslator implements Translator {

	private static final String MAX_LENGTH = "maxLength";

	@Override
	public void setTo(String to) { }

	@Override
	public String selectDBType(String fieldName, Field fieldAnnotation, List<String> additionalConstraints) {
		StringBuilder dbType = new StringBuilder();
		
		for (Hint aHint : fieldAnnotation.hints()) {
			if (aHint.name().equals(MAX_LENGTH) && Integer.parseInt(aHint.value()) > 255) {
				dbType.append("text");
			}
		}
		if (dbType.length() == 0) {
			dbType.append("varchar(255)");
		}
		
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
