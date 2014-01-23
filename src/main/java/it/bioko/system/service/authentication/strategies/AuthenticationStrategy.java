package it.bioko.system.service.authentication.strategies;

import it.bioko.system.command.CommandException;
import it.bioko.system.context.Context;
import it.bioko.system.entity.login.Login;
import it.bioko.utils.fields.Fields;

import java.util.List;

public interface AuthenticationStrategy {
	
	public static final String OUTPUT_FIELDS = "outputFields";
	
	public List<String> getAuthFields();
	
	public Fields authenticate(Context context, Fields input, boolean failSilently) throws CommandException;

	public Login createNewLogin(Context context, Fields input) throws CommandException;
	
}
