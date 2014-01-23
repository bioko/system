package it.bioko.system.command;

import java.util.Set;

public abstract class AbstractCommandHandler {
	
	public abstract void put(String aCommandName, Command aCommand);
	public abstract  Command getByName(String aCommandName) throws CommandException;
	public abstract String report() ;
	public abstract Set<String> keys();
	public abstract void putRest(String aRestCommandName, Command aCommand);
}