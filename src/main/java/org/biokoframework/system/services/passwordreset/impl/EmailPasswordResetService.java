/*
 * Copyright (c) 2014.
 * 	Mikol Faro		<mikol.faro@gmail.com>
 * 	Simone Mangano	 	<simone.mangano@ieee.org>
 * 	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
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
 */

package org.biokoframework.system.services.passwordreset.impl;

import org.apache.log4j.Logger;
import org.biokoframework.system.entity.authentication.PasswordReset;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.template.Template;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.currenttime.ICurrentTimeService;
import org.biokoframework.system.services.email.EmailException;
import org.biokoframework.system.services.email.IEmailService;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.system.services.passwordreset.IPasswordResetService;
import org.biokoframework.system.services.random.IRandomService;
import org.biokoframework.system.services.templates.ITemplatingService;
import org.biokoframework.system.services.templates.TemplatingException;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.RepositoryException;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

import static org.joda.time.format.ISODateTimeFormat.dateTimeNoMillis;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014 Apr 03
 */
public class EmailPasswordResetService implements IPasswordResetService {

    private static final Logger LOGGER = Logger.getLogger(EmailPasswordResetService.class);

    private static final String PASSWORD_RESET_TOKEN = "passwordResetToken";

    private final Repository<Login> fLoginRepo;
    private final Repository<PasswordReset> fPasswordResetRepo;

    private final IEntityBuilderService fEntityBuilder;
    private final ICurrentTimeService fCurrentTime;
    private final IRandomService fRandomToken;
    private final ITemplatingService fTemplatingService;
    private final IEmailService fEmailService;

    private final String fNoReplyAddress;

    @Inject
    public EmailPasswordResetService(IRepositoryService repos, IEntityBuilderService entityBuilderService, ICurrentTimeService currentTimeService,
                                     IRandomService randomTokenService, IEmailService emailService, ITemplatingService templatingService,
                                     @Named("noReplyEmailAddress") String noReplyAddress) {

        fEmailService = emailService;
        fTemplatingService = templatingService;
        fLoginRepo = repos.getRepository(Login.class);
        fPasswordResetRepo = repos.getRepository(PasswordReset.class);

        fEntityBuilder = entityBuilderService;
        fCurrentTime = currentTimeService;
        fRandomToken = randomTokenService;

        fNoReplyAddress = noReplyAddress;
    }

    @Override
    public void requestPasswordReset(String userEmail, Template template, Map<String, Object> templateContent) {

        Login login = fLoginRepo.retrieveByForeignKey(Login.USER_EMAIL, userEmail);
        if (login == null) {
//            throw CommandExceptionsFactory.createEntityNotFound(Login.class.getSimpleName(), Login.USER_EMAIL, userEmail);
        } else {
            try {
                PasswordReset reset = createReset(login.getId());

                templateContent.put("token", reset.get(PasswordReset.TOKEN));

                template = fTemplatingService.compileTemplate(template, templateContent);

                String content = template.get(Template.BODY);
                String subject = template.get(Template.TITLE);

                fEmailService.sendASAP(userEmail, fNoReplyAddress, content, subject);

            } catch (ValidationException|RepositoryException exception) {
                LOGGER.error("Cannot save password reset entity", exception);
            } catch (TemplatingException exception) {
                LOGGER.error("Cannot compile template for password reset mail", exception);
            } catch (EmailException exception) {
                LOGGER.error("Cannot sand password reset mail", exception);
            }
        }

    }

    private PasswordReset createReset(String loginId) throws ValidationException, RepositoryException {
        DateTime tomorrow = fCurrentTime.getCurrentTimeAsDateTime().plusDays(1);
        String randomToken = fRandomToken.generateUUID().toString();

        PasswordReset passwordReset = fEntityBuilder.getInstance(PasswordReset.class, new Fields(
                PasswordReset.LOGIN_ID, loginId,
                PasswordReset.TOKEN_EXPIRATION, tomorrow.toString(dateTimeNoMillis()),
                PasswordReset.TOKEN, randomToken));
        fPasswordResetRepo.save(passwordReset);
        return passwordReset;
    }

    @Override
    public void performPasswordReset(String token, String newPassword) throws ValidationException, RepositoryException {
        PasswordReset reset = fPasswordResetRepo.retrieveByForeignKey(PasswordReset.TOKEN, token);
        if (reset != null) {
            if (!isPast((String) reset.get(PasswordReset.TOKEN_EXPIRATION))) {
                String loginId = reset.get(PasswordReset.LOGIN_ID);
                Login login = fLoginRepo.retrieve(loginId);
                login.set(Login.PASSWORD, newPassword);
                fLoginRepo.save(login);
            } else {
                fPasswordResetRepo.delete(reset.getId());

                ErrorEntity error = new ErrorEntity();
                error.setAll(new Fields(
                        ErrorEntity.ERROR_CODE, FieldNames.TOKEN_EXPIRED_CODE,
                        ErrorEntity.ERROR_MESSAGE, "The token is expired",
                        ErrorEntity.ERROR_FIELD, PasswordReset.TOKEN_EXPIRATION));

                throw new ValidationException(error);
            }
        }
    }

    private boolean isPast(String timeStamp) {
        DateTime expiration = DateTime.parse(timeStamp, dateTimeNoMillis());
        return fCurrentTime.getCurrentTimeAsDateTime().isAfter(expiration);
    }
}
