package it.bioko.system.service.authentication;

import it.bioko.system.KILL_ME.commons.GenericConstants;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.command.AbstractFilter;
import it.bioko.system.command.CommandException;
import it.bioko.system.entity.login.Login;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.system.service.authentication.strategies.AuthenticationStrategy;
import it.bioko.system.service.authentication.strategies.AuthenticationStrategyFactory;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class AuthFilter extends AbstractFilter {

	private boolean _checkAuth;
	private List<String> _commandRoles;
	private Fields _outputFields;

	public AuthFilter(boolean checkAuth, String[] roles) {
		_checkAuth = checkAuth;
		if (roles != null) {
			_commandRoles = Arrays.asList(roles);
		} else {
			_commandRoles = new ArrayList<String>();
		}
	}
	
	@Override
	public void filterInput(Fields input) throws CommandException {
		
		AuthenticationStrategy strategy = AuthenticationStrategyFactory.retrieveAuthenticationStrategy(input);
		if (strategy == null) {
			if (_checkAuth) {
				throw CommandExceptionsFactory.createUnauthorisedAccessException();
			} else {
				return;
			}
		}
		
		Fields authenticationFields = strategy.authenticate(_context, input, !_checkAuth);
		if (authenticationFields != null) {
			Login login = (Login) authenticationFields.objectNamed(Login.class.getSimpleName());
			if (!_commandRoles.isEmpty()) {
				String userRolesAsString = StringUtils.defaultString(login.get(Login.ROLES));

				LinkedList<String> userRoles = new LinkedList<String>(Arrays.asList(userRolesAsString.split(GenericConstants.USER_ROLE_SEPARATOR)));
				userRoles.retainAll(_commandRoles);
				if (userRoles.isEmpty()) {
					throw CommandExceptionsFactory.createInsufficientPrivilegesException();
				}
			}
			input.put(GenericFieldNames.AUTH_LOGIN_ID, login.getId());
			
			_outputFields = (Fields) authenticationFields.objectNamed(AuthenticationStrategy.OUTPUT_FIELDS);
		}
	}

	@Override
	public void filterOutput(Fields output) throws CommandException {
		if (_outputFields != null) {
			output.putAll(_outputFields);
			_outputFields = null;
		}
	}
	
}
