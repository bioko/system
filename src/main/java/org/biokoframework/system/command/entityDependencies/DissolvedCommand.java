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

package org.biokoframework.system.command.entityDependencies;

import org.apache.log4j.Logger;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.EntityClassNameTranslator;
import org.biokoframework.system.entity.description.ComposedParameterEntityBuilder;
import org.biokoframework.system.entity.description.ParameterEntity;
import org.biokoframework.system.entity.description.ParameterEntityBuilder;
import org.biokoframework.system.entity.resolution.AnnotatedEntityDissolver;
import org.biokoframework.system.entity.resolution.EntityDissolver;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class DissolvedCommand extends AbstractCommand {

	private static final String COMPONING_KEYS_METHOD = "componingKeys";

	private static final Logger LOGGER = Logger.getLogger(DissolvedCommand.class);
	
	private EntityDissolver _dissolver = new AnnotatedEntityDissolver();
	private ArrayList<Class<? extends DomainEntity>> _classes = new ArrayList<Class<? extends DomainEntity>>(); 
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		LOGGER.info("EXECUTING Command:" + this.getClass().getSimpleName());	
		LOGGER.info("INPUT: " + input.toString());		
		
		ArrayList<Object> response = new ArrayList<Object>();
		
		try {
			response.add(_dissolver.dissolve(input));
		} catch (CommandException exception) {
			throw exception;
		} catch (Exception exception) {
			throw CommandExceptionsFactory.createContainerException(exception);
		}
		
		Fields result = new Fields(GenericFieldNames.RESPONSE, response);
		
		LOGGER.info("OUTPUT after execution: " + result.toString());
		LOGGER.info("END Command:" + this.getClass().getSimpleName());
		return result;
	}

	public <DE extends DomainEntity> DissolvedCommand savingIn(Repository<DE> repository, Class<DE> domainEntityClass) {
		_classes.add(domainEntityClass);
		_dissolver.savingIn(repository, domainEntityClass);
		return this;
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
				LOGGER.error("Access to componing keys through reflection", exception);
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
		
		return new Fields(GenericFieldNames.INPUT, parameters);
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
				LOGGER.error("Access to componing keys through reflection", exception);
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
		
		return new Fields(GenericFieldNames.OUTPUT, parameters);
	}
	
}
