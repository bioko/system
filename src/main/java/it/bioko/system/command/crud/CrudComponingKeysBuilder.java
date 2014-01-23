package it.bioko.system.command.crud;

import it.bioko.system.KILL_ME.commons.GenericCommandNames;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.system.KILL_ME.commons.HttpMethod;
import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.entity.EntityClassNameTranslator;
import it.bioko.system.entity.description.ParameterEntity;
import it.bioko.system.entity.description.ParameterEntityBuilder;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.ComponingFieldsFactory;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class CrudComponingKeysBuilder {

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
			Loggers.xsystem.error("Unable to get componing keys for entity " + domainEntityClass.getSimpleName(), e);
			return null;
		}
		
		ParameterEntityBuilder builder = new ParameterEntityBuilder();
		builder.loadDefaultExample();
		for (String aKey : entityKeys) {
			builder.set(ParameterEntity.NAME, aKey);
			parameters.add(builder.build(false));
		}
		
		Fields resultFields = Fields.single(FieldNames.COMMAND_NAME, getCrudName(domainEntityClass));
		resultFields.put(GenericFieldNames.INPUT, parameters);
		return resultFields;
	}

	private static <T extends DomainEntity> Fields putInputKeys(Class<T> domainEntityClass) {
		ArrayList<DomainEntity> parameters = new ArrayList<DomainEntity>();
		ArrayList<String> keys = null;
		try {
			keys = ComponingFieldsFactory.create(domainEntityClass);
		} catch (Exception e) {
			Loggers.xsystem.error("Unable to get componing keys for entity " + domainEntityClass.getSimpleName(), e);
			return null;
		}
		
		for (String aKey : keys) {
			ParameterEntity parameter = new ParameterEntity(Fields.empty());
			parameter.set(ParameterEntity.NAME, aKey);
			parameter.set(ParameterEntity.HTTP_PARAMETER_TYPE, GenericFieldValues.BODY);
			parameters.add(parameter);
		}
		
		ParameterEntity idParameter = new ParameterEntity(Fields.empty());
		idParameter.set(ParameterEntity.NAME, DomainEntity.ID);
		idParameter.set(ParameterEntity.HTTP_PARAMETER_TYPE, GenericFieldValues.URL_PATH);
		parameters.add(idParameter);
		
		Fields resultFields = Fields.single(FieldNames.COMMAND_NAME, getCrudName(domainEntityClass));
		resultFields.put(GenericFieldNames.INPUT, parameters);
		return resultFields;
	}
	
	private static <T extends DomainEntity> Fields getInputKeys(Class<T> domainEntityClass) {
		ArrayList<ParameterEntity> parameters = new ArrayList<ParameterEntity>(); 

		ParameterEntity parameter = new ParameterEntity(Fields.empty());
		parameter.set(ParameterEntity.NAME, DomainEntity.ID);
		parameter.set(ParameterEntity.HTTP_PARAMETER_TYPE, GenericFieldValues.URL_PATH);
		parameters.add(parameter);
		
		Fields resultFields = Fields.single(FieldNames.COMMAND_NAME, getCrudName(domainEntityClass));
		resultFields.put(GenericFieldNames.INPUT, parameters);
		return resultFields;
	}
	
	private static <T extends DomainEntity> Fields deleteInputKeys(Class<T> domainEntityClass) {
		ArrayList<ParameterEntity> parameters = new ArrayList<ParameterEntity>(); 
		
		ParameterEntity parameter = new ParameterEntity(Fields.empty());
		parameter.set(ParameterEntity.NAME, DomainEntity.ID);
		parameter.set(ParameterEntity.HTTP_PARAMETER_TYPE, GenericFieldValues.URL_PATH);
		parameters.add(parameter);
		
		Fields resultFields = Fields.single(FieldNames.COMMAND_NAME, getCrudName(domainEntityClass));
		resultFields.put(GenericFieldNames.INPUT, parameters);
		return resultFields;
	}
	
	private static <T extends DomainEntity> Fields postOutputKeys(Class<T> domainEntityClass) {
		ArrayList<String> entityKeys = null;
		try {
			entityKeys = ComponingFieldsFactory.create(domainEntityClass);
		} catch (Exception e) {
			Loggers.xsystem.error("Unable to get componing keys for entity " + domainEntityClass.getSimpleName(), e);
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
		
		Fields fields = Fields.empty();		
		fields.put(GenericFieldNames.OUTPUT, parameters);
		return fields;
	}
	
	private static <T extends DomainEntity> Fields putOutputKeys(Class<T> domainEntityClass) {
		ArrayList<String> entityKeys = null;
		try {
			entityKeys = ComponingFieldsFactory.create(domainEntityClass);
		} catch (Exception e) {
			Loggers.xsystem.error("Unable to get componing keys for entity " + domainEntityClass.getSimpleName(), e);
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
		
		Fields fields = Fields.empty();		
		fields.put(GenericFieldNames.OUTPUT, parameters);
		return fields;
	}

	private static <T extends DomainEntity> Fields deleteOutputKeys(Class<T> domainEntityClass) {
		ArrayList<String> entityKeys = null;
		try {
			entityKeys = ComponingFieldsFactory.create(domainEntityClass);
		} catch (Exception e) {
			Loggers.xsystem.error("Unable to get componing keys for entity " + domainEntityClass.getSimpleName(), e);
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
		
		Fields fields = Fields.empty();		
		fields.put(GenericFieldNames.OUTPUT, parameters);
		return fields;
	}
	
	private static <T extends DomainEntity> Fields getOutputKeys(Class<T> domainEntityClass) {
		ArrayList<String> entityKeys = null;
		try {
			entityKeys = ComponingFieldsFactory.create(domainEntityClass);
		} catch (Exception e) {
			Loggers.xsystem.error("Unable to get componing keys for entity " + domainEntityClass.getSimpleName(), e);
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
		
		Fields fields = Fields.empty();		
		fields.put(GenericFieldNames.OUTPUT, parameters);
		return fields;
	}
	
	private static <T extends DomainEntity> String getCrudName(Class<T> domainEntityClass) {
		return "CRUD_" + domainEntityClass.getSimpleName();
	}
}
