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

import org.biokoframework.system.entity.authentication.EmailConfirmation;
import org.biokoframework.system.entity.template.Template;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.email.IEmailConfirmationService;
import org.biokoframework.system.services.email.IEmailService;
import org.biokoframework.utils.repository.Repository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014 Apr 01
 */
public class EmailConfirmationServiceImpl implements IEmailConfirmationService {

    private final IEmailService fEmailService;
    private final String fNoReplyEmailAddress;
    private final Repository<EmailConfirmation> fConfirmationRepo;

    @Inject
    public EmailConfirmationServiceImpl(IEmailService emailService, IRepositoryService repos, @Named("noReplyEmailAddress") String noreplyEmailAddress) {
        fEmailService = emailService;
        fConfirmationRepo = repos.getRepository(EmailConfirmation.class);

        fNoReplyEmailAddress = noreplyEmailAddress;
    }

    @Override
    public void sendConfirmationEmail(String userEmail, Template mailTemplate, Map<String, Object> content) {

    }

    @Override
    public void confirmEmailAddress(String loginId, String token) {

    }

    @Override
    public void isConfirmed(String loginId) {

    }
}
