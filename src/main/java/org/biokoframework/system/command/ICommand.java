package org.biokoframework.system.command;

import org.biokoframework.system.context.Context;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;

public interface ICommand {

	public Fields execute(Fields input) throws CommandException, ValidationException;
	
	@Deprecated
	void setContext(Context context);
	@Deprecated
	void setCommandName(String commandName);
	@Deprecated
	void onContextInitialized();
	@Deprecated
	Fields componingInputKeys();
	@Deprecated
	Fields componingOutputKeys();
	
}
