package it.bioko.system.command.authentication;

import it.bioko.system.KILL_ME.commons.GenericConstants;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericRepositoryNames;
import it.bioko.system.command.Command;
import it.bioko.system.command.CommandException;
import it.bioko.system.entity.authentication.PasswordReset;
import it.bioko.system.entity.login.Login;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.system.repository.core.Repository;
import it.bioko.system.repository.core.SafeRepositoryHelper;
import it.bioko.system.service.currenttime.CurrentTimeService;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.fields.Fields;
import it.bioko.utils.validator.Validator;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class ApplyPasswordResetCommand extends Command {

	private Repository<Login> _loginRepo;
	private Repository<PasswordReset> _passwordResetRepo;
	private CurrentTimeService _currentTimeService;

	@Override
	public void onContextInitialized() {
		_loginRepo = _context.getRepository(GenericRepositoryNames.LOGIN_REPOSITORY);
		_passwordResetRepo = _context.getRepository(GenericRepositoryNames.PASSWORD_RESET);
		
		_currentTimeService = (CurrentTimeService) _context.get(GenericConstants.CONTEXT_CURRENT_TIME_SERVICE);
	}
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);
		
		String token = input.stringNamed(PasswordReset.TOKEN);
		PasswordReset passwordReset = _passwordResetRepo.retrieveByForeignKey(PasswordReset.TOKEN, token);
		
		if (passwordReset != null) {
			_passwordResetRepo.delete(passwordReset.getId());

			DateTime tokenExpireTime = DateTime.parse(passwordReset.get(PasswordReset.TOKEN_EXPIRATION), DateTimeFormat.forPattern(Validator.ISO_TIMESTAMP));
			DateTime now = _currentTimeService.getCurrentTimeAsDateTime();
			if (now.isBefore(tokenExpireTime)) {
				Login login = _loginRepo.retrieve(passwordReset.get(PasswordReset.LOGIN_ID));
				login.set(Login.PASSWORD, input.stringNamed(Login.PASSWORD));
				SafeRepositoryHelper.save(_loginRepo, login, _context);
			}
		} else {
			throw CommandExceptionsFactory.createEntityNotFound(PasswordReset.class.getSimpleName(), PasswordReset.TOKEN, token);
		}
		

		logOutput();
		return Fields.single(GenericFieldNames.RESPONSE, new ArrayList<DomainEntity>());
	}

}
