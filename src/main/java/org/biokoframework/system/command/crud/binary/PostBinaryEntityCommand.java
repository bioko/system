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

import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.binary.BinaryEntity;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.factory.binary.BinaryEntityRepository;
import org.biokoframework.system.repository.core.SafeRepositoryHelper;
import org.biokoframework.utils.fields.Fields;

import java.util.ArrayList;

public class PostBinaryEntityCommand extends AbstractCommand {

	private final String fBlobFieldName = "";

//	public PostBinaryEntityCommand(String blobName) {
//		fBlobFieldName = EntityClassNameTranslator.toFieldName(blobName);
//	}

	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);
		Fields result = new Fields();
		
		BinaryEntityRepository blobRepo = getRepository(BinaryEntity.class);
		
		ArrayList<BinaryEntity> response = new ArrayList<BinaryEntity>();
		
		BinaryEntity blob = input.get(fBlobFieldName);

		if (!blob.isValid()) {
			throw CommandExceptionsFactory.createNotCompleteEntity(blob.getClass().getSimpleName());
		}
			
		blob = SafeRepositoryHelper.save(blobRepo, blob);
		if (blob == null) {
			throw CommandExceptionsFactory.createBadCommandInvocationException();
		}
		response.add(blob);
		
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
