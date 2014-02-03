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

import org.biokoframework.system.KILL_ME.commons.GenericCommandNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.logger.Loggers;
import org.biokoframework.system.command.Command;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

public class PrintRepositoryCommand extends Command {

	private final Repository<?> _repository;
	private String _repositoryName;

	public PrintRepositoryCommand(Repository<?> repository) {
		_repositoryName = repository.getClass().getSimpleName();
		Loggers.xsystem.info("repository class: " + _repositoryName);
		_repository = repository;
	}

	@Override
	public Fields execute(Fields input) throws CommandException {
		Fields fields = Fields.successful();
		Loggers.xsystem.info(_repository.report());
		
		String previousReport = input.get(GenericFieldNames.REPOSITORY_REPORT);
		fields.put(GenericFieldNames.REPOSITORY_REPORT, previousReport + _repository.report());
		
		Loggers.xsystem.info("INPUT" + input.toJSONString());
		Fields result = fields.putAll(input);
		Loggers.xsystem.info("RESULT" + result.toJSONString());
		return result;
	}
	
	public String repositoryName(){
		return new StringBuilder(GenericCommandNames.PRINT_REPOSITORY).append("-").append(_repositoryName).toString();
	}

	@Override
	public Fields componingInputKeys() {
		return new Fields();
	}

	@Override
	public Fields componingOutputKeys() {
		return new Fields();
	}

	@Override
	public String getName() {
		return GenericCommandNames.PRINT_REPOSITORY;
	}
}