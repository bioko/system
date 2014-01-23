package it.bioko.system.command;

import it.bioko.system.context.Context;
import it.bioko.utils.fields.Fields;

public abstract class AbstractFilter {

	protected Context _context;
	
	public void setContext(Context context) {
		_context = context;
	}
	
	public void filterInput(Fields input) throws CommandException {}	
	public void filterOutput(Fields output) throws CommandException {}
	
	public void onContextInitialized() {}		// event listener
	
}
