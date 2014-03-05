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

import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.authentication.EmailConfirmation;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.repository.core.SafeRepositoryHelper;
import org.biokoframework.system.services.currenttime.ICurrentTimeService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.FieldValues;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

import com.google.inject.Inject;


public class ResponseEmailConfirmationCommand extends AbstractCommand {

	private static final String ISO_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ssZ";
	private ICurrentTimeService fCurrentTimeService;
	
	@Inject
	public ResponseEmailConfirmationCommand(ICurrentTimeService currentTimeService) {
		fCurrentTimeService = currentTimeService;
	}

	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);

		Repository<Login> loginRepo = getRepository(Login.class);
		Repository<EmailConfirmation> emailConfirmationRepo = getRepository(EmailConfirmation.class);
		
		String loginUserEmail = input.get(Login.USER_EMAIL);
		String token = input.get(EmailConfirmation.TOKEN);
		
		EmailConfirmation confirmation = emailConfirmationRepo.retrieveByForeignKey(EmailConfirmation.TOKEN, token);
		Login login = loginRepo.retrieve(confirmation.get(EmailConfirmation.LOGIN_ID).toString());
		if (login != null && login.get(Login.USER_EMAIL).equals(loginUserEmail)) {
			String timestamp = fCurrentTimeService.getCurrentTimeAsDateTime().toString(ISO_TIMESTAMP);
			
			confirmation.set(EmailConfirmation.CONFIRMATION_TIMESTAMP, timestamp);
			confirmation.set(EmailConfirmation.CONFIRMED, FieldValues.TRUE);
			SafeRepositoryHelper.save(emailConfirmationRepo, confirmation);
		}
				
		logOutput();
		return new Fields(GenericFieldNames.RESPONSE, new ArrayList<DomainEntity>());
	}

}
