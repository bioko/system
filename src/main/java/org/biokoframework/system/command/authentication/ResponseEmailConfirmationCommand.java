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

import com.google.inject.Inject;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.authentication.EmailConfirmation;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.email.EmailException;
import org.biokoframework.system.services.email.IEmailConfirmationService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

import java.util.ArrayList;


public class ResponseEmailConfirmationCommand extends AbstractCommand {

	private static final String ISO_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ssZ";

    private final IEmailConfirmationService fConfirmationService;
    private final Repository<Login> fLoginRepo;

    @Inject
	public ResponseEmailConfirmationCommand(IEmailConfirmationService confirmationService, IRepositoryService repos) {
        fConfirmationService = confirmationService;
        fLoginRepo = repos.getRepository(Login.class);
    }

	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);

		String userEmail = input.get(Login.USER_EMAIL);
		String token = input.get(EmailConfirmation.TOKEN);

        Repository<Login> loginRepo = getRepository(Login.class);
        Login login = loginRepo.retrieveByForeignKey(Login.USER_EMAIL, userEmail);
        if (login == null) {
            throw CommandExceptionsFactory.createEntityNotFound(Login.class, Login.USER_EMAIL,userEmail);
        }

        try {
            fConfirmationService.confirmEmailAddress(login.getId(), token);
        } catch (EmailException exception) {
            throw CommandExceptionsFactory.createContainerException(exception);
        }

		logOutput();
		return new Fields(GenericFieldNames.RESPONSE, new ArrayList<DomainEntity>());
	}

}
