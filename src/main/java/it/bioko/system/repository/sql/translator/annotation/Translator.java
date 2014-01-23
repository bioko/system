package it.bioko.system.repository.sql.translator.annotation;

import it.bioko.utils.domain.annotation.field.Field;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface Translator {

	public void setTo(String to);
	
	public String selectDBType(String fieldName, Field fieldAnnotation, List<String> additionalConstraints);

	public String convertFromDBValue(String fieldName, ResultSet resultset, Field fieldAnnotation) throws SQLException;
	public void insertIntoStatement(String fieldName, String fieldValue, Field fieldAnnotation, PreparedStatement statement, int queryIndex) throws SQLException;


}
