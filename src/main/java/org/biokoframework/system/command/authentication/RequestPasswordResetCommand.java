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
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.authentication.PasswordReset;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.template.Template;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.repository.core.SafeRepositoryHelper;
import org.biokoframework.system.services.currenttime.ICurrentTimeService;
import org.biokoframework.system.services.email.EmailException;
import org.biokoframework.system.services.email.IEmailService;
import org.biokoframework.system.services.email.KILL_ME.ContentBuilder;
import org.biokoframework.system.services.random.IRandomService;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public class RequestPasswordResetCommand extends AbstractCommand {

	private static final String NO_REPLY = "no-reply@engaged.it";

	public static final String PASSWORD_RESET_TOKEN = "passwordResetToken";

	public static final String PASSWORD_RESET_MAIL_TEMPLATE = "passwordResetMailTemplate";

	private static final String RESET_PASSWORD_LANDING_PAGE_URL = "resetPasswordLandingPage";
	
	private final ICurrentTimeService fCurrentTimeService;
	private final IRandomService fRandomTokenService;
	private final IEmailService fEmailService;

	private final String fLandingPageUrl;

	@Inject
	public RequestPasswordResetCommand(ICurrentTimeService currentTimeService, IRandomService randomTokenService, 
			IEmailService emailService, @Named("resetPasswordLandingPage") String landingPageUrl) {
		fCurrentTimeService = currentTimeService;
		fRandomTokenService = randomTokenService;
		fEmailService = emailService;
		fLandingPageUrl = landingPageUrl;
	}
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);
		
		Repository<Login> _loginRepo = getRepository(Login.class);
		Repository<PasswordReset> _passwordResetRepo = getRepository(PasswordReset.class);
		Repository<Template> _templateRepo = getRepository(Template.class);
		
		String userEmail = input.get(Login.USER_EMAIL);
		Login login = _loginRepo.retrieveByForeignKey(Login.USER_EMAIL, userEmail);
		if (login == null) {
			throw CommandExceptionsFactory.createEntityNotFound(Login.class.getSimpleName(), Login.USER_EMAIL, userEmail);
		}
		
		DateTime now = fCurrentTimeService.getCurrentTimeAsDateTime();
		
		PasswordReset passwordReset = new PasswordReset(new Fields());
		passwordReset.set(PasswordReset.LOGIN_ID, login.getId());
		passwordReset.set(PasswordReset.TOKEN_EXPIRATION, now.plusDays(1).toString(ISODateTimeFormat.dateTimeNoMillis()));
		String randomToken = fRandomTokenService.generateString(PASSWORD_RESET_TOKEN, 20);
		passwordReset.set(PasswordReset.TOKEN, randomToken);
		SafeRepositoryHelper.save(_passwordResetRepo, passwordReset);


		Template mailTemplate = _templateRepo.retrieveByForeignKey(Template.TRACK, PASSWORD_RESET_MAIL_TEMPLATE);
		if (mailTemplate != null) {
			
			
			Map<String, Object> contentMap = new HashMap<String, Object>();
			contentMap.put("url", StringUtils.defaultString(fLandingPageUrl));
			contentMap.put("token", randomToken);
			contentMap.put("userEmail", login.get(Login.USER_EMAIL));
			ContentBuilder contentBuilder = new ContentBuilder(mailTemplate, contentMap);
			
			String targetEmail = login.get(Login.USER_EMAIL);
			try {
				fEmailService.sendASAP(targetEmail, NO_REPLY, contentBuilder.buildBody(), contentBuilder.buildTitle());
			} catch (EmailException exception) {
				throw CommandExceptionsFactory.createContainerException(exception);
			}

		} else {
			throw CommandExceptionsFactory.createEntityNotFound(Template.class.getSimpleName(), Template.TRACK, PASSWORD_RESET_MAIL_TEMPLATE);
		}

		logOutput();
		return new Fields(GenericFieldNames.RESPONSE, new ArrayList<DomainEntity>());
	}

}
