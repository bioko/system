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

import java.util.List;

import org.apache.log4j.Logger;
import org.biokoframework.system.KILL_ME.commons.GenericCommandNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.command.KILL_ME.SetCommand;
import org.biokoframework.system.context.Context;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.repository.core.SafeRepositoryHelper;
import org.biokoframework.system.service.description.JsonSystemDescriptor;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class CrudCommand<T extends DomainEntity> extends SetCommand {

	private Class<T> fDomainEntityClass;
	private Context fContext;

	@Inject
	public CrudCommand(@Assisted Class<T> domainEntityClass, Repository<T> domainEntityRepository) {

		super(CrudComponingKeysBuilder.inputKeys(domainEntityClass), CrudComponingKeysBuilder.outputKeys(domainEntityClass));

		fDomainEntityClass = domainEntityClass;
	}

	@Override
	public Fields execute(Fields input) throws CommandException {
		Fields result = new Fields();
		CrudMethod crudMethod = CrudMethod.fromRestCommand(input.get(FieldNames.COMMAND_NAME).toString());
		Repository<T> repository = getRepository(fDomainEntityClass);

		Logger logger = fContext.get(Context.LOGGER);
		
		try {
			logger.info("INPUT: " + input.toJSONString());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		result.put(GenericCommandNames.CRUD_METHOD, crudMethod.value());
		T actualEntity = null;
		try {
			actualEntity = fDomainEntityClass.getConstructor(Fields.class).newInstance(input);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		if (crudMethod.equals(CrudMethod.OPTIONS)) {
			result.put(GenericFieldNames.RESPONSE, new JsonSystemDescriptor().describeEntity(fDomainEntityClass));
		} else if (!actualEntity.isValid() && (crudMethod.equals(CrudMethod.POST) || crudMethod.equals(CrudMethod.PUT))) {
			throw CommandExceptionsFactory.createContainerException( 
					new ValidationException(actualEntity.getValidationErrors()));
		} else {
			// TODO MATTO ma che schifo di codice!!!!
			List<T> response = SafeRepositoryHelper.call(repository, actualEntity, crudMethod.value(), fContext);
			if (response.size() > 0) {
				result.put(GenericFieldNames.RESPONSE, response);
			} else {
				throw CommandExceptionsFactory.createEntityNotFound(fDomainEntityClass.getSimpleName(), actualEntity.getId());
			}
			result.putAll(input);
		}
		logger.info("OUTPUT after execution: " + result.toString());
		logger.info("END CRUD Command:" + this.getClass().getSimpleName());
		return result;
	}
	
}
