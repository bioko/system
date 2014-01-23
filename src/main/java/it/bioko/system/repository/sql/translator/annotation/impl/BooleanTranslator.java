package it.bioko.system.repository.sql.translator.annotation.impl;

import it.bioko.system.repository.sql.translator.annotation.Translator;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.fields.FieldValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BooleanTranslator implements Translator {

	@Override
	public void setTo(String to) {
	}

	@Override
	public String selectDBType(String fieldName, Field fieldAnnotation, List<String> additionalConstraints) {
		return "boolean";
	}

	@Override
	public void insertIntoStatement(String fieldName, String fieldValue, Field fieldAnnotation, PreparedStatement statement, int sqlIndex) throws SQLException {
		statement.setBoolean(sqlIndex, Boolean.parseBoolean(fieldValue));
	}

	@Override
	public String convertFromDBValue(String fieldName, ResultSet resultset, Field fieldAnnotation) throws SQLException {
		boolean boolVal = resultset.getBoolean(fieldName);
		
		if (!boolVal) {
			return FieldValues.FALSE;
		} else {
			return FieldValues.TRUE;
		}
	}
}
