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

package org.biokoframework.system.command.KILL_ME;


import static org.biokoframework.system.exceptions.FieldValidatorFactory.fieldsContainsKey;

import java.util.ArrayList;
import java.util.HashMap;

import org.biokoframework.system.KILL_ME.commons.GenericCommandNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.system.KILL_ME.commons.logger.Loggers;
import org.biokoframework.system.command.Command;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.EntityClassNameTranslator;
import org.biokoframework.system.entity.description.ParameterEntity;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

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
		Loggers.xsystem.info("INPUT: " + input.toString());
		
		validate(input);
		
		String value = input.get(_key);
		if (value == null) {
			
			HashMap<String, Object> entityMap = input.get(EntityClassNameTranslator.toFieldName(_entityClass.getSimpleName()));
			
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
		
		Loggers.xsystem.info("OUTPUT after execution: " + result.toString());
		Loggers.xsystem.info("END Command:" + this.getClass().getSimpleName());
		return result;
	}

	private void validate(Fields input) throws CommandException {
		if (input.get(EntityClassNameTranslator.toFieldName(_entityClass.getSimpleName())) != null) {
			
			HashMap<String, Object> entityMap = input.get(EntityClassNameTranslator.toFieldName(_entityClass.getSimpleName()));
			
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
