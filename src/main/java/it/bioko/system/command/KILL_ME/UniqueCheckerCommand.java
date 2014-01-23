package it.bioko.system.command.KILL_ME;


import static it.bioko.system.exceptions.FieldValidatorFactory.fieldsContainsKey;
import it.bioko.system.KILL_ME.commons.GenericCommandNames;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.command.Command;
import it.bioko.system.command.CommandException;
import it.bioko.system.entity.EntityClassNameTranslator;
import it.bioko.system.entity.description.ParameterEntity;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.system.repository.core.Repository;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;
import java.util.HashMap;

@Deprecated
public class UniqueCheckerCommand<T extends DomainEntity> extends Command {

	private String _key;
	private Repository<T> _repository;
	private Class<T> _entityClass;
	
	public UniqueCheckerCommand(String key, Repository<T> repository, Class<T> entityClass) {
		_key = key;
		_repository = repository;
		_entityClass = entityClass;
	}
	
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		Loggers.xsystem.info("EXECUTING Command:" + this.getClass().getSimpleName());
		Loggers.xsystem.info("INPUT: " + input.asString());
		
		validate(input);
		
		String value = input.stringNamed(_key);
		if (value == null) {
			
			@SuppressWarnings("unchecked")
			HashMap<String, Object> entityMap = (HashMap<String, Object>) 
					input.valueFor(EntityClassNameTranslator.toFieldName(_entityClass.getSimpleName()));
			
			value = (String) entityMap.get(_key);
		}
		
		for (T entity : _repository.getAll()) {
			if (value.equals(entity.get(_key))) {
				throw CommandExceptionsFactory.createAlreadyExistingEntity(entity.getClass().getSimpleName());
			}
		}
		
		Fields result = Fields.empty();
		result.put(GenericFieldNames.RESPONSE, new ArrayList<DomainEntity>());
		result.putAll(input);
		
		Loggers.xsystem.info("OUTPUT after execution: " + result.asString());
		Loggers.xsystem.info("END Command:" + this.getClass().getSimpleName());
		return result;
	}

	private void validate(Fields input) throws CommandException {
		if (input.valueFor(EntityClassNameTranslator.toFieldName(_entityClass.getSimpleName())) != null) {
			
			@SuppressWarnings("unchecked")
			HashMap<String, Object> entityMap = (HashMap<String, Object>) 
					input.valueFor(EntityClassNameTranslator.toFieldName(_entityClass.getSimpleName()));
			
			if (entityMap.get(_key) == null) {
				throw CommandExceptionsFactory.createExpectedFieldNotFound(_key);
			}
		} else {
			fieldsContainsKey(input, _key);
		}
	}

	@Override
	public String getName() {
		return GenericCommandNames.UNIQUE_CHECKER;
	}
	
	@Override
	public Fields componingInputKeys() {
		ArrayList<ParameterEntity> parameters = new ArrayList<ParameterEntity>();
		
		ParameterEntity parameter = new ParameterEntity(Fields.empty());
		parameter.set(ParameterEntity.NAME, _key);
		parameter.set(ParameterEntity.HTTP_PARAMETER_TYPE, GenericFieldValues.QUERY_STRING);
		parameters.add(parameter);
		
		Fields fields = Fields.single(FieldNames.COMMAND_NAME, GenericCommandNames.UNIQUE_CHECKER);
		fields.put(GenericFieldNames.INPUT, parameters);
		return fields;
	}

}
