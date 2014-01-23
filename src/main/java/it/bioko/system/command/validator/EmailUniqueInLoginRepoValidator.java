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

package it.bioko.system.command.validator;

import it.bioko.system.KILL_ME.commons.GenericRepositoryNames;
import it.bioko.system.entity.login.Login;
import it.bioko.system.repository.core.Repository;
import it.bioko.system.service.validation.AbstractValidator;
import it.bioko.utils.domain.ErrorEntity;
import it.bioko.utils.fields.Fields;
import it.bioko.utils.validator.ValidatorErrorBuilder;

import java.util.List;

public class EmailUniqueInLoginRepoValidator extends AbstractValidator {

	@Override
	public void validate(Fields input, List<ErrorEntity> errors) {

		Repository<Login> loginRepo = _context.getRepository(GenericRepositoryNames.LOGIN_REPOSITORY);
		String valueToTest = input.stringNamed(Login.USER_EMAIL);
		List<Login> result = loginRepo.getEntitiesByForeignKey(Login.USER_EMAIL, valueToTest);

		if (!result.isEmpty())
			errors.add(ValidatorErrorBuilder.buildUniqueViolationError(Login.USER_EMAIL));

	}

}
