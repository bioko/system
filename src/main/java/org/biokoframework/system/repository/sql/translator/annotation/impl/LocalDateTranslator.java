/*
 * Copyright (c) 2014																 
 *	Mikol Faro			<mikol.faro@gmail.com>
 *	Simone Mangano		<simone.mangano@ieee.org>
 *	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package org.biokoframework.system.repository.sql.translator.annotation.impl;

import org.biokoframework.system.repository.sql.translator.annotation.Translator;
import org.biokoframework.utils.domain.annotation.field.Field;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LocalDateTranslator implements Translator {

	private static final String MYSQL_DATE = "yyyy-MM-dd";
	
	@Override
	public String selectDBType(String fieldName, Field fieldAnnotation, List<String> additionalConstraints) {
		return "date";
	}

	@Override
	public void insertIntoStatement(String fieldName, Object fieldValue, Field fieldAnnotation, PreparedStatement statement, int sqlIndex) throws SQLException {
		if (fieldValue == null) {
			statement.setObject(sqlIndex, null);
		} else {
			LocalDate timestamp = LocalDate.parse((String) fieldValue);
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
