package it.bioko.system.command.KILL_ME;

import it.bioko.system.KILL_ME.FieldsErrors;
import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.command.Command;
import it.bioko.system.command.CommandException;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;

import java.util.LinkedHashMap;
import java.util.Map.Entry;


public class MultipleCommand extends Command {
	
	private LinkedHashMap<String, Command> _steps = new LinkedHashMap<String, Command>();
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		Fields overAllOutput = Fields.successful();
		Loggers.xsystem.info("Executing MultipleCommand: " + this.getClass().getSimpleName());
		Loggers.xsystem.info("Steps: " + _steps.size());
		for (Entry<String,Command> step : _steps.entrySet()) {
			Fields output = Fields.empty();
			Loggers.xsystem.info("Executing step: " + step.getKey());
			output = step.getValue().execute(input);
			if (output.contains(FieldsErrors.FAILURE)) {
				output.put(FieldNames.FAILED_COMMAND, step.getValue().getClass().getSimpleName());
				Loggers.xsystem.info("Step " + step.getKey() + " execution failed!");
				return overAllOutput.putAll(Fields.failed()).putAll(output);
			} else if (output.contains(FieldsErrors.ERROR)) {
				output.put(FieldNames.ERROR_COMMAND, step.getValue().getClass().getSimpleName());
				Loggers.xsystem.info("Step " + step.getKey() + " execution error!");
				return overAllOutput.putAll(Fields.failed()).putAll(output);
			}
			overAllOutput.putAll(output);

			input.putAll(output);

		}
		Loggers.xsystem.info("MultipleCommand output: " + overAllOutput.asString());
		Loggers.xsystem.info("End MultipleCommand: " + this.getClass().getSimpleName());
		return overAllOutput;
	}
	
	public void addStep(String aStepKey, Command aCommand) {
		Loggers.xsystem.info("Adding step: " + aStepKey + " to MultipleCommand " + this.getClass().getSimpleName());
		_steps.put(aStepKey, aCommand);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}