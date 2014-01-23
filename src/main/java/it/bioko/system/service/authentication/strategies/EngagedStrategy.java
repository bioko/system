package it.bioko.system.service.authentication.strategies;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericRepositoryNames;
import it.bioko.system.command.CommandException;
import it.bioko.system.context.Context;
import it.bioko.system.entity.login.Login;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.system.repository.core.Repository;
import it.bioko.utils.fields.Fields;

import java.util.Arrays;
import java.util.List;

public class EngagedStrategy implements AuthenticationStrategy {

	private static final List<String> AUTH_FIELDS = Arrays.asList(Login.USER_EMAIL, Login.PASSWORD);

	@Override
	public List<String> getAuthFields() {
		return AUTH_FIELDS;
	}

	@Override
	public Fields authenticate(Context context, Fields input, boolean failSilently) throws CommandException {
		Repository<Login> loginRepository = context.getRepository(GenericRepositoryNames.LOGIN_REPOSITORY);
		
		String userName = input.stringNamed(GenericFieldNames.USER_EMAIL);
		String password = input.stringNamed(GenericFieldNames.PASSWORD);
		
		// TODO transform into query: "SELECT * FROM Login where userEmail = ? and password = ?;
		Login login = loginRepository.retrieveByForeignKey(GenericFieldNames.USER_EMAIL, userName);
		if (login == null) {
			if (failSilently) {
				return null;
			} else {
				throw CommandExceptionsFactory.createInvalidLoginException();
			}
		} else if (!login.get(GenericFieldNames.PASSWORD).equals(password)) {
			throw CommandExceptionsFactory.createInvalidLoginException();
		}
		
		return Fields.single(Login.class.getSimpleName(), login);
	}

	@Override
	public Login createNewLogin(Context context, Fields input) throws CommandException {	
		
		Login login = new Login(input);
		
		return login;
	}

}
