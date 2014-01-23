package it.bioko.system.repository.sql.translator.annotation.impl;

import it.bioko.system.repository.sql.translator.annotation.Translator;
import it.bioko.utils.domain.annotation.field.Field;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class DoubleTranslator implements Translator {

	@Override
	public void setTo(String to) { }

	@Override
	public String selectDBType(String fieldName, Field fieldAnnotation, List<String> additionalConstraints) {
		return "double";
	}

	@Override
	public String convertFromDBValue(String fieldName, ResultSet resultset, Field fieldAnnotation) throws SQLException {
		
		if (StringUtils.isEmpty(fieldAnnotation.format())) {
			DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH); 
			decimalFormat.setDecimalSeparatorAlwaysShown(false);
			decimalFormat.setMaximumFractionDigits(50);
			
			return decimalFormat.format(resultset.getDouble(fieldName));
		}
		
		DecimalFormat decimalFormat = new DecimalFormat(fieldAnnotation.format());
//		DecimalFormat decimalFormat = new DecimalFormat();
//		decimalFormat.setDecimalSeparatorAlwaysShown(false);
		
		return decimalFormat.format(resultset.getDouble(fieldName));
	}

	@Override
	public void insertIntoStatement(String fieldName, String fieldValue, Field fieldAnnotation, PreparedStatement statement, int sqlIndex) throws SQLException {
		statement.setDouble(sqlIndex, Double.parseDouble(fieldValue));
	}

}
