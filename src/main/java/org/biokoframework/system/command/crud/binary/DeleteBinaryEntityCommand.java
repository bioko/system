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

package org.biokoframework.system.command.crud.binary;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.biokoframework.system.KILL_ME.commons.GenericCommandNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.biokoframework.system.command.Command;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.context.Context;
import org.biokoframework.system.entity.binary.BinaryEntity;
import org.biokoframework.system.entity.description.ParameterEntity;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.factory.binary.BinaryEntityRepository;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.Fields;

public class DeleteBinaryEntityCommand extends Command {

	private final Context _context;
	private final BinaryEntityRepository _blobRepo;
	
	public DeleteBinaryEntityCommand(Context context, BinaryEntityRepository blobRepo) {
		_context = context;
		_blobRepo = blobRepo;
	}

	@Override
	public Fields execute(Fields input) throws CommandException {
		Fields result = Fields.empty();

		Logger logger = _context.get(Context.LOGGER);
		
		try {
			logger.info("INPUT: " + input.asJson());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ArrayList<BinaryEntity> response = new ArrayList<BinaryEntity>();
		
		String blobId = input.stringNamed(DomainEntity.ID);
		if (blobId == null || blobId.isEmpty()) {
			throw CommandExceptionsFactory.createExpectedFieldNotFound(DomainEntity.ID);
		}
				
		BinaryEntity blob = _blobRepo.delete(blobId);
		if (blob == null) {
			throw CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class.getSimpleName(), blobId);
		}
		
		result.put(GenericFieldNames.RESPONSE, response);
		logger.info("OUTPUT after execution: " + result.asString());
		logger.info("END CRUD Command:" + this.getClass().getSimpleName());
		return result;
	}

	@Override
	public String getName() {
		return GenericCommandNames.composeRestCommandName(HttpMethod.DELETE, BinaryEntity.class.getSimpleName());
	}
	
	@Override
	public Fields componingInputKeys() {
		ArrayList<ParameterEntity> parameters = new ArrayList<ParameterEntity>(); 
		
		ParameterEntity parameter = new ParameterEntity(Fields.empty());
		parameter.set(ParameterEntity.NAME, DomainEntity.ID);
		parameter.set(ParameterEntity.HTTP_PARAMETER_TYPE, GenericFieldValues.URL_PATH);
		parameters.add(parameter);
		
		Fields resultFields = Fields.single(GenericFieldNames.INPUT, parameters);
		return resultFields;
	}
	
	@Override
	public Fields componingOutputKeys() {		
		return Fields.empty();
	}

}