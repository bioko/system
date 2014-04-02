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

package org.biokoframework.system.services.email.impl;

import org.apache.log4j.Logger;
import org.biokoframework.system.entity.authentication.EmailConfirmation;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.entity.template.Template;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.repository.core.SafeRepositoryHelper;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.email.EmailException;
import org.biokoframework.system.services.email.IEmailConfirmationService;
import org.biokoframework.system.services.email.IEmailService;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.system.services.random.IRandomService;
import org.biokoframework.system.services.templates.ITemplatingService;
import org.biokoframework.system.services.templates.TemplatingException;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.repository.RepositoryException;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014 Apr 01
 */
public class EmailConfirmationServiceImpl implements IEmailConfirmationService {

    private static final Logger LOGGER = Logger.getLogger(EmailConfirmationServiceImpl.class);

    private final IEntityBuilderService fEntityBuilder;
    private final Repository<EmailConfirmation> fConfirmationRepo;
    private final IRandomService fRandomTokenService;

    private final ITemplatingService fTemplatingService;
    private final IEmailService fEmailService;

    private final String fNoReplyEmailAddress;

    @Inject
    public EmailConfirmationServiceImpl(IRepositoryService repos, IEntityBuilderService entityBuilderService, IRandomService randomTokenService,
                                        IEmailService emailService, ITemplatingService templatingService,
                                        @Named("noReplyEmailAddress") String noreplyEmailAddress) {
        fConfirmationRepo = repos.getRepository(EmailConfirmation.class);
        fRandomTokenService = randomTokenService;
        fEntityBuilder = entityBuilderService;

        fTemplatingService = templatingService;
        fEmailService = emailService;

        fNoReplyEmailAddress = noreplyEmailAddress;
    }

    @Override
    public void sendConfirmationEmail(Login login, Template mailTemplate, Map<String, Object> content) throws EmailException, TemplatingException {

        String token = fRandomTokenService.generateUUID().toString();

        EmailConfirmation confirmation = fEntityBuilder.getInstance(EmailConfirmation.class, new Fields(
                EmailConfirmation.LOGIN_ID, login.getId(),
                EmailConfirmation.TOKEN, token,
                EmailConfirmation.CONFIRMED, false));

        try {
            fConfirmationRepo.save(confirmation);
        } catch (ValidationException|RepositoryException exception) {
            LOGGER.error("Cannot save confirmation entity", exception);
            throw new EmailException(exception);
        }

        String userEmail = login.get(Login.USER_EMAIL);

        content.put("token", token);
        Template compiled = fTemplatingService.compileTemplate(mailTemplate, content);

        String message = compiled.get(Template.BODY);
        String subject = compiled.get(Template.TITLE);
        fEmailService.sendASAP(userEmail, fNoReplyEmailAddress, message, subject);

    }

    @Override
    public void confirmEmailAddress(String loginId, String token) {

    }

    @Override
    public void isConfirmed(String loginId) {

    }
}
