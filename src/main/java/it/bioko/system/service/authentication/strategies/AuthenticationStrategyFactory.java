package it.bioko.system.service.authentication.strategies;

import it.bioko.system.command.CommandException;
import it.bioko.utils.fields.Fields;

import java.util.Arrays;
import java.util.List;

public class AuthenticationStrategyFactory {

	public static List<AuthenticationStrategy> createAuthenticationStrategy() {
		return Arrays.asList(
				new TokenAuthStrategy(),
				new BasicStrategy());
	}

	public static List<AuthenticationStrategy> createCheckInStrategy() {
		return Arrays.asList(
				new FacebookStrategy(),
				new EngagedStrategy(),
				new BasicStrategy());
	}

	public static AuthenticationStrategy retrieveAuthenticationStrategy(Fields input) throws CommandException {
		return retrieveStrategy(input, createAuthenticationStrategy());
	}
	

	public static AuthenticationStrategy retrieveCheckInStrategy(Fields input) {
		return retrieveStrategy(input, createCheckInStrategy());
	}

	private static AuthenticationStrategy retrieveStrategy(Fields input, List<AuthenticationStrategy> candidatesStrategies) {
		AuthenticationStrategy theStrategy = null;
		List<String> inputKeys = input.keys();
		for (AuthenticationStrategy aStrategy : candidatesStrategies) {
			if (inputKeys.containsAll(aStrategy.getAuthFields())) {
				theStrategy = aStrategy;
				break;
			}
		}
		
		return theStrategy;
	}

}
