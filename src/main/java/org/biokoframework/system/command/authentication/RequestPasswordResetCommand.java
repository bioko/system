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

import org.apache.commons.lang3.StringUtils;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.template.Template;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.services.passwordreset.IPasswordResetService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestPasswordResetCommand extends AbstractCommand {

	public static final String PASSWORD_RESET_MAIL_TEMPLATE = "passwordResetMailTemplate";


    private final IPasswordResetService fPasswordResetService;
    private final String fLandingPageUrl;

    @Inject
	public RequestPasswordResetCommand(IPasswordResetService passwordResetService, @Named("resetPasswordLandingPage") String landingPageUrl) {
        fPasswordResetService = passwordResetService;
		fLandingPageUrl = landingPageUrl;
	}
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);
		
		Repository<Login> loginRepo = getRepository(Login.class);

		String userEmail = input.get(Login.USER_EMAIL);
        ensureLoginExists(loginRepo, userEmail);

        Template mailTemplate = retrieveTemplateOrFailTrying();

        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("url", StringUtils.defaultString(fLandingPageUrl));

        fPasswordResetService.requestPasswordReset(userEmail, mailTemplate, contentMap);

		logOutput();
		return new Fields(GenericFieldNames.RESPONSE, new ArrayList<DomainEntity>());
	}

    private void ensureLoginExists(Repository<Login> loginRepo, String userEmail) throws CommandException {
        Login login = loginRepo.retrieveByForeignKey(Login.USER_EMAIL, userEmail);
        if (login == null) {
            throw CommandExceptionsFactory.createEntityNotFound(Login.class, Login.USER_EMAIL, userEmail);
        }
    }

    private Template retrieveTemplateOrFailTrying() throws CommandException {
        Repository<Template> templateRepo = getRepository(Template.class);
        Template mailTemplate = templateRepo.retrieveByForeignKey(Template.TRACK, PASSWORD_RESET_MAIL_TEMPLATE);
        if (mailTemplate == null) {
            throw CommandExceptionsFactory.createEntityNotFound(Template.class, Template.TRACK, PASSWORD_RESET_MAIL_TEMPLATE);
        }
        return mailTemplate;
    }

}
