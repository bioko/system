package it.bioko.system.command;

import it.bioko.system.context.Context;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;
import it.bioko.utils.json.JSonBuilder;

import org.apache.log4j.Logger;


public abstract class Command {
	
	protected Context _context;
	protected String _commandName;
	
	public abstract Fields execute(Fields input) throws CommandException;
	
	public void fillInvocationInfo(Fields output) {
		try {
			output.put(FieldNames.COMMAND_INVOCATION_INPUT_INFO, new JSonBuilder().buildFrom(componingInputKeys()));
			output.put(FieldNames.COMMAND_INVOCATION_OUTPUT_INFO, new JSonBuilder().buildFrom(componingOutputKeys()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Fields componingInputKeys() {
		return Fields.empty();
	}

	public Fields componingOutputKeys() {
		return Fields.empty();
	}

	
	public String getName() {
		return _commandName;
	}
	
	public void setCommandName(String commandName) {
		_commandName = commandName;
	}
	
	public void setContext(Context context) {
		_context = context;
	}
	
	public void onContextInitialized() {
		
	}
	
	protected void logInput(Fields input) { 
		Logger logger = _context.getLogger();
		logger.info("EXECUTING Command:" + this.getClass().getSimpleName());
		logger.info("INPUT: " + input.asString());
	}
	
	protected void logOutput(Fields output) { 
		Logger logger = _context.getLogger();
		
		if (output==null)		
			logger.info("OUTPUT after execution: (nothing)");
		else
			logger.info("OUTPUT after execution: "+output.asString());
		
		logger.info("END Command:" + this.getClass().getSimpleName());
	}
	
	protected void logOutput() {
		logOutput(null);
	}
	
	
	
}