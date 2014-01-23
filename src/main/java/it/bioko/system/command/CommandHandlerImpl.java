package it.bioko.system.command;

import it.bioko.utils.domain.ErrorEntity;
import it.bioko.utils.fields.Fields;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

public class CommandHandlerImpl extends AbstractCommandHandler {

	private LinkedHashMap<String,Command> _commands = new LinkedHashMap<String, Command>();
	private String _system;
	private String _version;

	public CommandHandlerImpl(String system, String version) {
		_system = system;
		_version = version;
	}

	public static CommandHandlerImpl empty(String aSystem, String aVersion) {
		return new CommandHandlerImpl(aSystem, aVersion);
	}
	
	@Override
	public void put(String aCommandName, Command aCommand){
		_commands.put(aCommandName, aCommand);
	}

	@Override
	public Command getByName(String aCommandName) throws CommandException {
		try {
			return _commands.get(aCommandName);
		} catch (NullPointerException npe) {
			Fields fields = Fields.single(ErrorEntity.ERROR_MESSAGE, "Command name not found: " + aCommandName);
			throw new CommandException(new ErrorEntity(fields), npe);
		}
	}

	@Override
	public String report() {
		StringBuffer result = new StringBuffer(CommandHandlerImpl.class.getSimpleName());
		result.append(": System=" + _system);
		result.append(" Version=" + _version);
		result.append(": \n");
		for (Entry<String, Command> each : _commands.entrySet()) {
			result.append(each.getKey());
			result.append("=");
			result.append(each.getValue());
			result.append("\n");
		}
		return result.toString();
	}

	@Override
	public Set<String> keys() {
		return _commands.keySet();
	}

	@Override
	public void putRest(String aRestCommandName, Command aCommand) {
		_commands.put(aRestCommandName, aCommand);
	}
}