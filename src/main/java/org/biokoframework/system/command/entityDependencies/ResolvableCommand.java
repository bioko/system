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

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.command.ICommand;
import org.biokoframework.system.entity.description.ParameterEntity;
import org.biokoframework.system.entity.resolution.AnnotatedEntityResolver;
import org.biokoframework.system.entity.resolution.EntityResolver;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

public class ResolvableCommand extends AbstractCommand {

	private static final Logger LOGGER = Logger.getLogger(ResolvableCommand.class);
	
	protected ICommand fBaseCommand;
	private EntityResolver fResolver;

	public ResolvableCommand(ICommand baseCommand) {
		fBaseCommand = baseCommand;
		fResolver = new AnnotatedEntityResolver();
	}

	
	@Override
	public Fields execute(Fields input) throws CommandException, ValidationException {
		LOGGER.info("EXECUTING Command:" + this.getClass().getSimpleName());	
		LOGGER.info("INPUT: " + input.toString());
		
		boolean doResolve = false;
		if (input.containsKey(GenericFieldNames.RESOLVE_ENTITIES)) {
			doResolve = Boolean.parseBoolean(input.get(GenericFieldNames.RESOLVE_ENTITIES).toString());
		}
		
		Fields unresolvedResult = fBaseCommand.execute(input);
		
		Fields result;
		if (doResolve) {
			ArrayList<DomainEntity> entities = unresolvedResult.get(GenericFieldNames.RESPONSE);
			ArrayList<DomainEntity> resolvedEntities = new ArrayList<DomainEntity>();
			
			try {
				for (DomainEntity anEntity : entities) {
					DomainEntity aResolvedEntity = fResolver.solve(anEntity, anEntity.getClass());
					resolvedEntities.add(aResolvedEntity);
				}
				
				result = new Fields(GenericFieldNames.RESPONSE, resolvedEntities);
			} catch (Exception exception) {
				throw CommandExceptionsFactory.createContainerException(exception);
			}
		} else {
			result = unresolvedResult;
		}
		
		LOGGER.info("OUTPUT after execution: " + result.toString());
		LOGGER.info("END Command:" + this.getClass().getSimpleName());	
		return result;
	}

	public <DE extends DomainEntity> ResolvableCommand with(Repository<DE> repository, Class<DE> domainEntityClass) {
		fResolver.with(repository, domainEntityClass);
		return this;
	}

	public ResolvableCommand maxDepth(int depthLimit) {
		fResolver.maxDepth(depthLimit);
		return this;
	}
		
	@Override
	public Fields componingInputKeys() {
		Fields componingInputKeys = fBaseCommand.componingInputKeys();
		
		ParameterEntity parameter = new ParameterEntity();
		parameter.set(ParameterEntity.NAME, GenericFieldNames.RESOLVE_ENTITIES);
		parameter.set(ParameterEntity.HTTP_PARAMETER_TYPE, GenericFieldValues.QUERY_STRING);
		
		ArrayList<ParameterEntity> inputs = componingInputKeys.get(GenericFieldNames.INPUT);
		inputs.add(parameter);
		
		componingInputKeys.put(GenericFieldNames.INPUT, inputs);
		
		return componingInputKeys;
	}

	@Override
	public Fields componingOutputKeys() {
		return fBaseCommand.componingOutputKeys();
	}
}
