package it.bioko.system.service.authentication;

import it.bioko.system.KILL_ME.commons.GenericConstants;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericRepositoryNames;
import it.bioko.system.command.AbstractFilter;
import it.bioko.system.command.CommandException;
import it.bioko.system.entity.login.Login;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.system.repository.core.Repository;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class BasicAuthFilter extends AbstractFilter {
	
	private Repository<Login> _loginRepo;

	private static final String BASIC_AUTH_START = "Basic ";

	private boolean _checkAuth;
	private ArrayList<String> _commandRoles;

		
	public BasicAuthFilter(boolean checkAuth, String[] roles) {
		_checkAuth = checkAuth;
		_commandRoles = new ArrayList<String>();
		if (roles!=null) {
			for (String role: roles)
				_commandRoles.add(role);
		}
	}


	@Override
	public void onContextInitialized() {
		_loginRepo = _context.getRepository(GenericRepositoryNames.LOGIN_REPOSITORY);
	}

	public void filterInput(Fields input) throws CommandException {
		if(_loginRepo == null) {
			return;
		}
		
		String encodedAuthentication = input.stringNamed(GenericFieldNames.BASIC_AUTHENTICATION);
		String decodedString = new String(Base64.decodeBase64(encodedAuthentication.substring(BASIC_AUTH_START.length())));

		String userName = decodedString.replaceFirst(":.*", "");
		String password = decodedString.replaceFirst(".*:", "");
		
		Login login = _loginRepo.retrieveByForeignKey(GenericFieldNames.USER_EMAIL, userName);
		if (login == null || !login.get(GenericFieldNames.PASSWORD).equals(password)) {
			if (_checkAuth) {
				throw CommandExceptionsFactory.createInvalidLoginException();
			}
		} else {
			
			if (!_commandRoles.isEmpty()) {
				String userRolesAsString = StringUtils.defaultString(login.get(Login.ROLES));
				
				LinkedList<String> userRoles = new LinkedList<String>(Arrays.asList(userRolesAsString.split(GenericConstants.USER_ROLE_SEPARATOR)));
				userRoles.retainAll(_commandRoles);
				if (userRoles.isEmpty()) {
					throw CommandExceptionsFactory.createInsufficientPrivilegesException();
				}
			}
			
			input.put(GenericFieldNames.AUTH_LOGIN_ID, login.getId());
		}
		
	}
	
}
