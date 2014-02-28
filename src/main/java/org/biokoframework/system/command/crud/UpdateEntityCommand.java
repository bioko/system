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

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.RepositoryException;

public class UpdateEntityCommand extends AbstractCommand {

	private final Class<? extends DomainEntity> fDomainEntityClass;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Inject
	public UpdateEntityCommand(@Named("entity") Class domainEntityClass) {
		fDomainEntityClass = domainEntityClass;
	}
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);
		Repository<? extends DomainEntity> repository = getRepository(fDomainEntityClass);

		String id = input.get(DomainEntity.ID);
		if (StringUtils.isEmpty(id)) {
			throw CommandExceptionsFactory.createExpectedFieldNotFound(DomainEntity.ID);
		}
		
		DomainEntity entity = repository.retrieve(id);
		if (entity == null) {
			throw CommandExceptionsFactory.createEntityNotFound(fDomainEntityClass.getSimpleName(), id);
		}
		
		for (String aKey : input.keys()) {
			entity.set(aKey, input.get(aKey));
		}
		
		ArrayList<DomainEntity> response = new ArrayList<>();
		try {
			response.add(repository.save(entity));
		} catch (RepositoryException exception) {
			throw CommandExceptionsFactory.createContainerException(exception);
		} catch (ValidationException exception) {
			throw CommandExceptionsFactory.createContainerException(exception);
		}
		
		Fields output = new Fields(
				GenericFieldNames.RESPONSE, response);
		logOutput(output);
		return output;
	}

}
