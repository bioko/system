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

package it.bioko.system.repository.sql.translator;

import it.bioko.system.repository.sql.translator.annotation.Translate;
import it.bioko.system.repository.sql.translator.annotation.Translator;
import it.bioko.system.repository.sql.translator.annotation.Translators;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.Field;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class SqlTypesTranslator {

	private Map<Class<?>, ? extends Translator> _typeMap;
	private Translator _idTranslator;
	private List<String> _additionalConstraints = new LinkedList<String>();
	
	public SqlTypesTranslator(Class<?> translatorDefinitions) throws InstantiationException, IllegalAccessException {
		_typeMap = createTranslatorsMap(translatorDefinitions);
		_idTranslator = getIDTranslator(translatorDefinitions);
	}
	
	public String getSqlType(String fieldName, Field fieldAnnotation) {
		if (fieldName.equals(DomainEntity.ID) || DomainEntity.class.isAssignableFrom(fieldAnnotation.type())) {
			return _idTranslator.selectDBType(fieldName, fieldAnnotation, _additionalConstraints);
		} else {
			Translator translator = _typeMap.get(fieldAnnotation.type());
			
			if (translator == null) {
				translator = _typeMap.get(String.class);
				Logger.getLogger("engagedServer").warn("Cannot find translator for " + fieldAnnotation.type() + " default to String translator");
			}
			return translator.selectDBType(fieldName, fieldAnnotation, _additionalConstraints);
		}
	}

	public void insertIntoStatement(String fieldName, String fieldValue, Field fieldAnnotation, PreparedStatement statement, int queryIndex) throws SQLException {
		if (fieldName.equals(DomainEntity.ID) || DomainEntity.class.isAssignableFrom(fieldAnnotation.type())) {
			_idTranslator.insertIntoStatement(fieldName, fieldValue, fieldAnnotation, statement, queryIndex);
		} else {
			_typeMap.get(fieldAnnotation.type()).insertIntoStatement(fieldName, fieldValue, fieldAnnotation, statement, queryIndex);
		}
	}
	
	public String convertFromDBValue(String fieldName, ResultSet resultset, Field fieldAnnotation) throws SQLException {
		
		if (fieldName.equals(DomainEntity.ID) || DomainEntity.class.isAssignableFrom(fieldAnnotation.type())) {
			return _idTranslator.convertFromDBValue(fieldName, resultset, fieldAnnotation);
		} else {
			return _typeMap.get(fieldAnnotation.type()).convertFromDBValue(fieldName, resultset, fieldAnnotation);
		}
	}
	
	private static Map<Class<?>, Translator> createTranslatorsMap(Class<?> translatorDefinitions) throws InstantiationException, IllegalAccessException {
		Map<Class<?>, Translator> translatorsMap = new HashMap<Class<?>, Translator>();
		
		Translators translators = translatorDefinitions.getAnnotation(Translators.class);
		for (Translate aTranslation : translators.collection()) {
			Translator aTranslator = aTranslation.using().newInstance();
			aTranslator.setTo(aTranslation.to());
			translatorsMap.put(aTranslation.from(), aTranslator);
		}
		return translatorsMap;
	}
	

	private static Translator getIDTranslator(Class<?> translatorDefinitions) throws InstantiationException, IllegalAccessException {
		Translators translators = translatorDefinitions.getAnnotation(Translators.class);
		Translator idTranslator = translators.idTranslator().newInstance();
		return idTranslator;
	}

	public void clearConstraintsList() {
		_additionalConstraints.clear();
	}
	
	public List<String> getAllConstraints() {
		return Collections.unmodifiableList(_additionalConstraints);
	}
}
