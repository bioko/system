package it.bioko.system.repository.sql.translator.annotation.impl;

import it.bioko.system.repository.sql.translator.annotation.Translator;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class MySQLIDTranslator implements Translator {

	@Override
	public void setTo(String to) {
		
	}

	@Override
	public String selectDBType(String fieldName, Field fieldAnnotation, List<String> additionalConstraints) {
		if (fieldName.equals(DomainEntity.ID)) {
			return "int not null auto_increment primary key";
		} else {
			StringBuilder dbType = new StringBuilder("int");
			if (fieldAnnotation.mandatory()) { 
				dbType.append(" not null");
			}
			
			StringBuilder foreignKeyConstraint = new StringBuilder().
					append(" constraint foreign key (").append(fieldName).append(") references ").
					append(fieldAnnotation.type().getSimpleName()).append("(").append(DomainEntity.ID).append(")");
			additionalConstraints.add(foreignKeyConstraint.toString());
			
			return dbType.toString();
		}
	}

	@Override
	public void insertIntoStatement(String fieldName, String fieldValue, Field fieldAnnotation, PreparedStatement statement, int sqlIndex) throws NumberFormatException, SQLException {
		if (fieldValue != null) {
			statement.setInt(sqlIndex, Integer.parseInt(fieldValue));
		} else {
			statement.setNull(sqlIndex, Types.INTEGER);
		}
	}

	@Override
	public String convertFromDBValue(String fieldName, ResultSet resultset, Field fieldAnnotation) throws SQLException {
		return resultset.getString(fieldName);
	}
}
