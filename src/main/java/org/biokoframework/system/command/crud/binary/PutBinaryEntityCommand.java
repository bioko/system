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

import org.apache.log4j.Logger;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.binary.BinaryEntity;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.factory.binary.BinaryEntityRepository;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.RepositoryException;

import java.util.ArrayList;
import java.util.List;

public class PutBinaryEntityCommand extends AbstractCommand {

    private static final Logger LOGGER = Logger.getLogger(PutBinaryEntityCommand.class);

    @Override
	public Fields execute(Fields input) throws CommandException, ValidationException {
		logInput(input);
		Fields result = new Fields();
		
		BinaryEntityRepository blobRepo = getRepository(BinaryEntity.class);

		String blobId = input.get(DomainEntity.ID);
		if (blobId == null || blobId.isEmpty()) {
			throw CommandExceptionsFactory.createExpectedFieldNotFound(DomainEntity.ID);
		}
		BinaryEntity existingBlob = blobRepo.retrieveWithoutFile(blobId);
		if (existingBlob == null) {
			throw CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class, blobId);
		}

        String blobFieldName = "";
        if (input.keys().size() != 2) {
            throw CommandExceptionsFactory.createBadCommandInvocationException();
        } else {
            List<String> keys = input.keys();
            keys.remove(DomainEntity.ID);
            blobFieldName = keys.get(0);
        }

		BinaryEntity newBlob = input.get(blobFieldName);
		newBlob.setId(blobId);
		
		ArrayList<BinaryEntity> response = new ArrayList<BinaryEntity>();
		
		if (!newBlob.isValid()) {
			throw CommandExceptionsFactory.createNotCompleteEntity(newBlob.getClass().getSimpleName());
		}
        try {
            newBlob = blobRepo.save(newBlob);
        } catch (RepositoryException e) {
            LOGGER.error("Saving blob", e);
            throw CommandExceptionsFactory.createBadCommandInvocationException();
        }
        response.add(newBlob);

		result.put(GenericFieldNames.RESPONSE, response);
		logOutput(result);
		return result;
	}
	
	@Override
	public Fields componingInputKeys() {
		return new Fields();
	}
	
	@Override
	public Fields componingOutputKeys() {		
		return new Fields();
	}

}
