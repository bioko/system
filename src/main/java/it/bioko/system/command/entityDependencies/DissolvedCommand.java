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

package it.bioko.system.command.entityDependencies;

import it.bioko.system.KILL_ME.commons.GenericCommandNames;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.command.Command;
import it.bioko.system.command.CommandException;
import it.bioko.system.entity.EntityClassNameTranslator;
import it.bioko.system.entity.description.ComposedParameterEntityBuilder;
import it.bioko.system.entity.description.ParameterEntity;
import it.bioko.system.entity.description.ParameterEntityBuilder;
import it.bioko.system.entity.resolution.AnnotatedEntityDissolver;
import it.bioko.system.entity.resolution.EntityDissolver;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.system.repository.core.Repository;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;
import java.util.List;

public class DissolvedCommand extends Command {

	private static final String COMPONING_KEYS_METHOD = "componingKeys";
	
	private EntityDissolver _dissolver = new AnnotatedEntityDissolver();
	private ArrayList<Class<? extends DomainEntity>> _classes = new ArrayList<Class<? extends DomainEntity>>(); 
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		Loggers.xsystem.info("EXECUTING Command:" + this.getClass().getSimpleName());	
		Loggers.xsystem.info("INPUT: " + input.asString());		
		
		ArrayList<Object> response = new ArrayList<Object>();
		
		try {
			response.add(_dissolver.dissolve(input));
		} catch (CommandException exception) {
			throw exception;
		} catch (Exception exception) {
			throw CommandExceptionsFactory.createContainerException(exception);
		}
		
		Fields result = Fields.single(GenericFieldNames.RESPONSE, response);
		
		Loggers.xsystem.info("OUTPUT after execution: " + result.asString());
		Loggers.xsystem.info("END Command:" + this.getClass().getSimpleName());
		return result;
	}

	public <DE extends DomainEntity> DissolvedCommand savingIn(Repository<DE> repository, Class<DE> domainEntityClass) {
		_classes.add(domainEntityClass);
		_dissolver.savingIn(repository, domainEntityClass);
		return this;
	}
	
	@Override
	public String getName() {
		return GenericCommandNames.DISSOLVABLE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Fields componingInputKeys() {
		ArrayList<ParameterEntity> parameters = new ArrayList<ParameterEntity>();

		for (Class<? extends DomainEntity> aClass : _classes) {
			ComposedParameterEntityBuilder entityParameter = new ComposedParameterEntityBuilder();
			entityParameter.set(ParameterEntity.NAME, EntityClassNameTranslator.toFieldName(aClass.getSimpleName()));

			ArrayList<String> keys;
			try {
				keys = (ArrayList<String>) aClass.getMethod(COMPONING_KEYS_METHOD).invoke(null);
			} catch (Exception exception) {
				Loggers.xsystem.error("Access to componing keys through reflection", exception);
				return null;
			}
			ParameterEntityBuilder simpleParameter = new ParameterEntityBuilder();
			
			List<ParameterEntity> entityContent = new ArrayList<ParameterEntity>();
			for (String aKey : keys) {
				if (!aKey.endsWith("Id")) {
					simpleParameter.set(ParameterEntity.NAME, aKey);
					entityContent.add(simpleParameter.build(false));
				}
			}
			entityParameter.setContent(entityContent);
			
			parameters.add(entityParameter.build(false));
		}
		
		Fields fields = Fields.single(GenericFieldNames.INPUT, parameters);
		return fields;
	}

	@SuppressWarnings("unchecked")
	public Fields componingOutputKeys() {
		ArrayList<ParameterEntity> parameters = new ArrayList<ParameterEntity>();

		for (Class<? extends DomainEntity> aClass : _classes) {
			ComposedParameterEntityBuilder entityParameter = new ComposedParameterEntityBuilder();
			entityParameter.set(ParameterEntity.NAME, EntityClassNameTranslator.toFieldName(aClass.getSimpleName()));

			ArrayList<String> keys;
			try {
				keys = (ArrayList<String>) aClass.getMethod(COMPONING_KEYS_METHOD).invoke(null);
			} catch (Exception exception) {
				Loggers.xsystem.error("Access to componing keys through reflection", exception);
				return null;
			}
			ParameterEntityBuilder simpleParameter = new ParameterEntityBuilder();
			
			List<ParameterEntity> entityContent = new ArrayList<ParameterEntity>();
			for (String aKey : keys) {
				simpleParameter.set(ParameterEntity.NAME, aKey);
				entityContent.add(simpleParameter.build(false));
			}
			entityParameter.setContent(entityContent);
			
			parameters.add(entityParameter.build(false));
		}
		
		Fields fields = Fields.single(GenericFieldNames.OUTPUT, parameters);
		return fields;
	}
	
}
