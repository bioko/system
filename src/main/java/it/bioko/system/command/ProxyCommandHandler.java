package it.bioko.system.command;

import java.util.HashSet;
import java.util.Set;

public class ProxyCommandHandler extends AbstractCommandHandler {

	private AbstractCommandHandler _commandHandler;

	public ProxyCommandHandler(AbstractCommandHandler commandHandler) {
		_commandHandler = commandHandler;
	}
	
	@Override
	public final void put(String aCommandName, Command aCommand) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public Command getByName(String aCommandName) throws CommandException {
		return _commandHandler.getByName(aCommandName);
	}

	@Override
	public String report() {
		return _commandHandler.report();
	}

	@Override
	public Set<String> keys() {
		return new HashSet<String>(_commandHandler.keys());
	}

	@Override
	public void putRest(String aRestCommandName, Command aCommand) {
		throw new UnsupportedOperationException();
		
	}

}
