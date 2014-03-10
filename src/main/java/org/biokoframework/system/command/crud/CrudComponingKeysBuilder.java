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

package org.biokoframework.system.command.crud;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.biokoframework.system.KILL_ME.commons.GenericCommandNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.biokoframework.system.entity.EntityClassNameTranslator;
import org.biokoframework.system.entity.description.ParameterEntity;
import org.biokoframework.system.entity.description.ParameterEntityBuilder;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.annotation.field.ComponingFieldsFactory;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

@Deprecated
public class CrudComponingKeysBuilder {

	private static final Logger LOGGER = Logger.getLogger(CrudComponingKeysBuilder.class);

	public static <T extends DomainEntity> LinkedHashMap<String, Fields> inputKeys(Class<T> domainEntityClass) {
		
		LinkedHashMap<String, Fields> inputKeysMap = new LinkedHashMap<String, Fields>();
		
		String entityName = EntityClassNameTranslator.toHyphened(domainEntityClass.getSimpleName());
		
		inputKeysMap.put(
				GenericCommandNames.composeRestCommandName(HttpMethod.POST, entityName),
				postInputKeys(domainEntityClass));
		
		inputKeysMap.put(
				GenericCommandNames.composeRestCommandName(HttpMethod.PUT, entityName),
				putInputKeys(domainEntityClass));
				
		inputKeysMap.put(
				GenericCommandNames.composeRestCommandName(HttpMethod.GET, entityName),
				getInputKeys(domainEntityClass));
		
		inputKeysMap.put(
				GenericCommandNames.composeRestCommandName(HttpMethod.DELETE, entityName),
				deleteInputKeys(domainEntityClass));
		
		return inputKeysMap;
	}

	public static <T extends DomainEntity> LinkedHashMap<String, Fields> outputKeys(Class<T> domainEntityClass) {
		String entityName = EntityClassNameTranslator.toHyphened(domainEntityClass.getSimpleName());
		LinkedHashMap<String, Fields> outputKeysMap = new LinkedHashMap<String, Fields>();
		
		outputKeysMap.put(
				GenericCommandNames.composeRestCommandName(HttpMethod.POST, entityName),
				postOutputKeys(domainEntityClass));
	
		 outputKeysMap.put(
				 GenericCommandNames.composeRestCommandName(HttpMethod.PUT, entityName),
				 putOutputKeys(domainEntityClass));

		outputKeysMap.put(
				GenericCommandNames.composeRestCommandName(HttpMethod.GET, entityName),
				getOutputKeys(domainEntityClass));

		outputKeysMap.put(
				GenericCommandNames.composeRestCommandName(HttpMethod.DELETE, entityName),
				deleteOutputKeys(domainEntityClass));
	
	return outputKeysMap;
}
	
	private static <T extends DomainEntity> Fields postInputKeys(Class<T> domainEntityClass) {
		ArrayList<DomainEntity> parameters = new ArrayList<DomainEntity>();
		ArrayList<String> entityKeys = null;
		try {
			entityKeys = ComponingFieldsFactory.create(domainEntityClass);
		} catch (Exception e) {
			LOGGER.error("Unable to get componing keys for entity " + domainEntityClass.getSimpleName(), e);
			return null;
		}
		
		ParameterEntityBuilder builder = new ParameterEntityBuilder();
		builder.loadDefaultExample();
		for (String aKey : entityKeys) {
			builder.set(ParameterEntity.NAME, aKey);
			parameters.add(builder.build(false));
		}
		
		Fields resultFields = new Fields(
				FieldNames.COMMAND_NAME, getCrudName(domainEntityClass),
				GenericFieldNames.INPUT, parameters);
		return resultFields;
	}

	private static <T extends DomainEntity> Fields putInputKeys(Class<T> domainEntityClass) {
		ArrayList<DomainEntity> parameters = new ArrayList<DomainEntity>();
		ArrayList<String> keys = null;
		try {
			keys = ComponingFieldsFactory.create(domainEntityClass);
		} catch (Exception e) {
			LOGGER.error("Unable to get componing keys for entity " + domainEntityClass.getSimpleName(), e);
			return null;
		}
		
		for (String aKey : keys) {
			ParameterEntity parameter = new ParameterEntity();
			parameter.set(ParameterEntity.NAME, aKey);
			parameter.set(ParameterEntity.HTTP_PARAMETER_TYPE, GenericFieldValues.BODY);
			parameters.add(parameter);
		}
		
		ParameterEntity idParameter = new ParameterEntity();
		idParameter.set(ParameterEntity.NAME, DomainEntity.ID);
		idParameter.set(ParameterEntity.HTTP_PARAMETER_TYPE, GenericFieldValues.URL_PATH);
		parameters.add(idParameter);
		
		Fields resultFields = new Fields(FieldNames.COMMAND_NAME, getCrudName(domainEntityClass));
		resultFields.put(GenericFieldNames.INPUT, parameters);
		return resultFields;
	}
	
	private static <T extends DomainEntity> Fields getInputKeys(Class<T> domainEntityClass) {
		ArrayList<ParameterEntity> parameters = new ArrayList<ParameterEntity>(); 

		ParameterEntity parameter = new ParameterEntity();
		parameter.set(ParameterEntity.NAME, DomainEntity.ID);
		parameter.set(ParameterEntity.HTTP_PARAMETER_TYPE, GenericFieldValues.URL_PATH);
		parameters.add(parameter);
		
		Fields resultFields = new Fields(FieldNames.COMMAND_NAME, getCrudName(domainEntityClass));
		resultFields.put(GenericFieldNames.INPUT, parameters);
		return resultFields;
	}
	
	private static <T extends DomainEntity> Fields deleteInputKeys(Class<T> domainEntityClass) {
		ArrayList<ParameterEntity> parameters = new ArrayList<ParameterEntity>(); 
		
		ParameterEntity parameter = new ParameterEntity();
		parameter.set(ParameterEntity.NAME, DomainEntity.ID);
		parameter.set(ParameterEntity.HTTP_PARAMETER_TYPE, GenericFieldValues.URL_PATH);
		parameters.add(parameter);
		
		Fields resultFields = new Fields(
				FieldNames.COMMAND_NAME, getCrudName(domainEntityClass),
				GenericFieldNames.INPUT, parameters);
		return resultFields;
	}
	
	private static <T extends DomainEntity> Fields postOutputKeys(Class<T> domainEntityClass) {
		ArrayList<String> entityKeys = null;
		try {
			entityKeys = ComponingFieldsFactory.create(domainEntityClass);
		} catch (Exception e) {
			LOGGER.error("Unable to get componing keys for entity " + domainEntityClass.getSimpleName(), e);
			return null;
		}

		ArrayList<DomainEntity> parameters = new ArrayList<DomainEntity>();
		ParameterEntityBuilder builder = new ParameterEntityBuilder();
		for (String aKey : entityKeys) {
			builder.set(ParameterEntity.NAME, aKey);
			parameters.add(builder.build(false));
		}
		
		builder.set(ParameterEntity.NAME, DomainEntity.ID);
		parameters.add(builder.build(false));
		
		return new Fields(GenericFieldNames.OUTPUT, parameters);
	}
	
	private static <T extends DomainEntity> Fields putOutputKeys(Class<T> domainEntityClass) {
		ArrayList<String> entityKeys = null;
		try {
			entityKeys = ComponingFieldsFactory.create(domainEntityClass);
		} catch (Exception e) {
			LOGGER.error("Unable to get componing keys for entity " + domainEntityClass.getSimpleName(), e);
			return null;
		}

		ArrayList<DomainEntity> parameters = new ArrayList<DomainEntity>();
		ParameterEntityBuilder builder = new ParameterEntityBuilder();
		for (String aKey : entityKeys) {
			builder.set(ParameterEntity.NAME, aKey);
			parameters.add(builder.build(false));
		}
		
		builder.set(ParameterEntity.NAME, DomainEntity.ID);
		parameters.add(builder.build(false));
		
		return new Fields(GenericFieldNames.OUTPUT, parameters);
	}

	private static <T extends DomainEntity> Fields deleteOutputKeys(Class<T> domainEntityClass) {
		ArrayList<String> entityKeys = null;
		try {
			entityKeys = ComponingFieldsFactory.create(domainEntityClass);
		} catch (Exception e) {
			LOGGER.error("Unable to get componing keys for entity " + domainEntityClass.getSimpleName(), e);
			return null;
		}

		ArrayList<DomainEntity> parameters = new ArrayList<DomainEntity>();
		ParameterEntityBuilder builder = new ParameterEntityBuilder();
		for (String aKey : entityKeys) {
			builder.set(ParameterEntity.NAME, aKey);
			parameters.add(builder.build(false));
		}
		
		builder.set(ParameterEntity.NAME, DomainEntity.ID);
		parameters.add(builder.build(false));
		
		return new Fields(GenericFieldNames.OUTPUT, parameters);
	}
	
	private static <T extends DomainEntity> Fields getOutputKeys(Class<T> domainEntityClass) {
		ArrayList<String> entityKeys = null;
		try {
			entityKeys = ComponingFieldsFactory.create(domainEntityClass);
		} catch (Exception e) {
			LOGGER.error("Unable to get componing keys for entity " + domainEntityClass.getSimpleName(), e);
			return null;
		}

		ArrayList<DomainEntity> parameters = new ArrayList<DomainEntity>();
		ParameterEntityBuilder builder = new ParameterEntityBuilder();
		for (String aKey : entityKeys) {
			builder.set(ParameterEntity.NAME, aKey);
			parameters.add(builder.build(false));
		}
		
		builder.set(ParameterEntity.NAME, DomainEntity.ID);
		parameters.add(builder.build(false));
		
		return new Fields(GenericFieldNames.OUTPUT, parameters);
	}
	
	private static <T extends DomainEntity> String getCrudName(Class<T> domainEntityClass) {
		return "CRUD_" + domainEntityClass.getSimpleName();
	}
}
