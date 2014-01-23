package it.bioko.system.command.KILL_ME;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.command.AbstractCommandHandler;
import it.bioko.system.command.Command;
import it.bioko.system.command.CommandException;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class MultipleCommandPlusPlus extends Command {

	private String _name;
	private AbstractCommandHandler _commandHandler;
	private LinkedHashMap<String, String> _steps = new LinkedHashMap<String, String>();

	public MultipleCommandPlusPlus(String commandName, AbstractCommandHandler commandHandler) {
		_name = commandName;
		_commandHandler = commandHandler;
	}
	
	public MultipleCommandPlusPlus() {
	}
	
		
	

	@Override
	public final Fields execute(Fields input) throws CommandException {
		Loggers.xsystem.info("EXECUTING Multiple Command");
		Loggers.xsystem.info("STEPS: " + _steps.size());
		Loggers.xsystem.info("INPUT: " + input.asString());
		
		if (_commandHandler==null)
			_commandHandler = _context.getCommandHandler();
		
		Fields result = Fields.empty();
		ArrayList<Object> response = new ArrayList<Object>();
		
		Fields stepInput = input.copy();
		for (Entry<String, String> aStep: _steps.entrySet()) {
			Loggers.xsystem.info("Executing step: " + aStep.getKey());
						
			Command stepCommand = _commandHandler.getByName(aStep.getValue());
			if (stepCommand == null) {
				throw CommandExceptionsFactory.createCommandNotFoundException(aStep.getValue());
			} else {
				stepInput.put(FieldNames.COMMAND_NAME, aStep.getValue());
				Fields stepOutput = stepCommand.execute(stepInput);
				
				Collection<?> stepResponse = (Collection<?>) stepOutput.valueFor(GenericFieldNames.RESPONSE);
				prepareNextStepInput(aStep.getKey(), stepOutput, stepInput);
				
				if (stepResponse!=null)
					response.addAll(stepResponse);				
			}
		}
		
		postProcessingCleanUp(response, result);
		
		Loggers.xsystem.info("MultipleCommand output: " + result.asString());
		Loggers.xsystem.info("END MultipleCommand");
		return result;
	}

	protected void postProcessingCleanUp(ArrayList<Object> response, Fields result) {
		result.put(GenericFieldNames.RESPONSE, response);
	}


	protected void prepareNextStepInput(String prevStepName, Fields prevStepOutput, Fields nextStepInput) {
		prevStepOutput.remove(GenericFieldNames.RESPONSE);
		nextStepInput.putAll(prevStepOutput);
	}
	
	@Override
	public final String getName() {
		return _name;
	}

	public MultipleCommandPlusPlus addStep(String stepName, String commandName) {
		_steps.put(stepName, commandName);
		return this;
	}

}
