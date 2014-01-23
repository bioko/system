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

import org.apache.commons.codec.binary.Base64;

public class BasicStrategy implements AuthenticationStrategy {
	
	private static final String BASIC_AUTH_START = "Basic ";

	private static final List<String> AUTH_FIELDS = 
			Arrays.asList(GenericFieldNames.BASIC_AUTHENTICATION);

	@Override
	public List<String> getAuthFields() {
		return AUTH_FIELDS;
	}

	@Override
	public Fields authenticate(Context context, Fields input, boolean failSilently) throws CommandException {
		Repository<Login> loginRepository = context.getRepository(GenericRepositoryNames.LOGIN_REPOSITORY);
		
		String encodedAuthentication = input.stringNamed(GenericFieldNames.BASIC_AUTHENTICATION);
		String decodedString = new String(Base64.decodeBase64(encodedAuthentication.substring(BASIC_AUTH_START.length())));

		String userName = decodedString.replaceFirst(":.*", "");
		String password = decodedString.replaceFirst(".*:", "");
		
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
		String userName = input.stringNamed(GenericFieldNames.USER_EMAIL);
		String password = input.stringNamed(GenericFieldNames.PASSWORD);
		
		Login login = new Login(Fields.empty());
		login.set(Login.USER_EMAIL, userName);
		login.set(Login.PASSWORD, password);
		
		return login;
	}
	
}
