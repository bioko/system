package org.biokoframework.system.command;

import org.biokoframework.system.context.Context;
import org.biokoframework.utils.fields.Fields;

public interface ICommand {

	public Fields execute(Fields input) throws CommandException;
	
	@Deprecated
	public void setContext(Context context);
	@Deprecated
	public void setCommandName(String commandName);
	@Deprecated
	public void onContextInitialized();
	@Deprecated
	public String getName();
	@Deprecated
	public Fields componingInputKeys();
	@Deprecated
	public Fields componingOutputKeys();
	
}
