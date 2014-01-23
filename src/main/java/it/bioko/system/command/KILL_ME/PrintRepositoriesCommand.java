package it.bioko.system.command.KILL_ME;

public class PrintRepositoriesCommand extends MultipleCommand {

	public void addStep(PrintRepositoryCommand aCommand) {
		super.addStep(aCommand.repositoryName(), aCommand);
	}
}