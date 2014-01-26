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

package org.biokoframework.system.command.authentication;

import java.util.ArrayList;

import org.biokoframework.system.KILL_ME.commons.GenericConstants;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.GenericRepositoryNames;
import org.biokoframework.system.command.Command;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.authentication.EmailConfirmation;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.repository.core.SafeRepositoryHelper;
import org.biokoframework.system.service.currenttime.CurrentTimeService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.FieldValues;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.validator.Validator;


public class ResponseEmailConfirmationCommand extends Command {

	private Repository<Login> _loginRepo;
	private Repository<EmailConfirmation> _emailConfirmationRepo;
	private CurrentTimeService _currentTimeService;
	
	@Override
	public void onContextInitialized() {
		super.onContextInitialized();
		
		_loginRepo = _context.getRepository(GenericRepositoryNames.LOGIN_REPOSITORY);
		_emailConfirmationRepo = _context.getRepository(GenericRepositoryNames.EMAIL_CONFIRMATION);
		
		_currentTimeService = _context.get(GenericConstants.CONTEXT_CURRENT_TIME_SERVICE);
	}

	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);
		
		String loginUserEmail = input.stringNamed(Login.USER_EMAIL);
		String token = input.stringNamed(EmailConfirmation.TOKEN);
		
		EmailConfirmation confirmation = _emailConfirmationRepo.retrieveByForeignKey(EmailConfirmation.TOKEN, token);
		Login login = _loginRepo.retrieve(confirmation.get(EmailConfirmation.LOGIN_ID));
		if (login != null && login.get(Login.USER_EMAIL).equals(loginUserEmail)) {
			String timestamp = _currentTimeService.getCurrentTimeAsDateTime().toString(Validator.ISO_TIMESTAMP);
			
			confirmation.set(EmailConfirmation.CONFIRMATION_TIMESTAMP, timestamp);
			confirmation.set(EmailConfirmation.CONFIRMED, FieldValues.TRUE);
			SafeRepositoryHelper.save(_emailConfirmationRepo, confirmation, _context);
		}
				
		logOutput();
		return Fields.single(GenericFieldNames.RESPONSE, new ArrayList<DomainEntity>());
	}

}
