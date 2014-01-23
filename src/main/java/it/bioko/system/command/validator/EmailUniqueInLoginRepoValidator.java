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
