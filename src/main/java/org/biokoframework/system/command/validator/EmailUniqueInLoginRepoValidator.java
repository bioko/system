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

package org.biokoframework.system.command.validator;

import java.util.List;

import org.biokoframework.system.KILL_ME.commons.GenericRepositoryNames;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.service.validation.AbstractValidator;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.biokoframework.utils.validation.ValidationErrorBuilder;

public class EmailUniqueInLoginRepoValidator extends AbstractValidator {

	@Override
	public void validate(Fields input, List<ErrorEntity> errors) {

		Repository<Login> loginRepo = _context.getRepository(GenericRepositoryNames.LOGIN_REPOSITORY);
		String valueToTest = input.get(Login.USER_EMAIL);
		List<Login> result = loginRepo.getEntitiesByForeignKey(Login.USER_EMAIL, valueToTest);

		if (!result.isEmpty())
			errors.add(ValidationErrorBuilder.buildUniqueViolationError(Login.USER_EMAIL));

	}

}
