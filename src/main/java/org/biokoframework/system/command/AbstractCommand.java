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

package org.biokoframework.system.command;

import org.apache.log4j.Logger;
import org.biokoframework.system.context.Context;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.json.JSonBuilder;
import org.biokoframework.utils.repository.Repository;

import javax.inject.Inject;


public abstract class AbstractCommand implements ICommand {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractCommand.class);
	
	protected Context fContext;
	protected String fCommandName;
	
	private IRepositoryService fRepositoryService;
	private IEntityBuilderService fBuilder;
	
	@Inject
	public final void setRepositoryService(IRepositoryService service) {
		fRepositoryService = service;
	}
	
	@Inject
	public final void setEntityBuilder(IEntityBuilderService builder) {
		fBuilder = builder;
	}

    @Deprecated
	public void fillInvocationInfo(Fields output) {
		try {
			output.put(FieldNames.COMMAND_INVOCATION_INPUT_INFO, new JSonBuilder().buildFrom(componingInputKeys()));
			output.put(FieldNames.COMMAND_INVOCATION_OUTPUT_INFO, new JSonBuilder().buildFrom(componingOutputKeys()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
    @Deprecated
	public Fields componingInputKeys() {
		return new Fields();
	}

	@Override
    @Deprecated
	public Fields componingOutputKeys() {
		return new Fields();
	}
	
	@Override
    @Deprecated
	public void setContext(Context context) {
		fContext = context;
	}

	protected void logInput(Fields input) { 
		LOGGER.info("EXECUTING Command:" + this.getClass().getSimpleName());
		LOGGER.info("INPUT: " + input.toString());
	}
	
	protected void logOutput(Fields output) {
		if (LOGGER.isInfoEnabled()) {
			if (output == null) {		
				LOGGER.info("OUTPUT after execution: (nothing)");
			} else {
				LOGGER.info("OUTPUT after execution: " + output.toString());
			}
		
			LOGGER.info("END Command:" + this.getClass().getSimpleName());
		}
	}
	
	protected void logOutput() {
		logOutput(null);
	}
	
	////////////////////////////////////////////////
	// Helper methods
	
	protected <DE extends DomainEntity> DE createEntity(Class<DE> entityClass) {
		return fBuilder.getInstance(entityClass);
	}
	
	protected <DE extends DomainEntity> DE createEntity(Class<DE> entityClass, Fields fields) {
		return fBuilder.getInstance(entityClass, fields);
	}
	
	protected <DE extends DomainEntity, R extends Repository<DE>> R getRepository(Class<DE> entityClass) {
		return fRepositoryService.getRepository(entityClass);
	}
	
	protected <DE extends DomainEntity, R extends Repository<DE>> R getRepository(Class<DE> entityClass, String name) {
		return fRepositoryService.getRepository(entityClass, name);
	}

    ////////////////////////////////////////////////

    /**
     * @see ICommand#_dont_implement_ICommand___instead_extend_AbstractCommand_()
     */
    @Override
    @Deprecated
    public final void _dont_implement_ICommand___instead_extend_AbstractCommand_() {
        // See Matcher interface for an explanation of this method.
    }
	
}