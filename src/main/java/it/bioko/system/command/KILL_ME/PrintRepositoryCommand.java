package it.bioko.system.command.KILL_ME;

import it.bioko.system.KILL_ME.commons.GenericCommandNames;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.command.Command;
import it.bioko.system.command.CommandException;
import it.bioko.system.repository.core.Repository;
import it.bioko.utils.fields.Fields;

public class PrintRepositoryCommand extends Command {

	private final Repository<?> _repository;
	private String _repositoryName;

	public PrintRepositoryCommand(Repository<?> repository) {
		_repositoryName = repository.getClass().getSimpleName();
		Loggers.xsystem.info("repository class: " + _repositoryName);
		_repository = repository;
	}

	@Override
	public Fields execute(Fields input) throws CommandException {
		Fields fields = Fields.successful();
		Loggers.xsystem.info(_repository.report());
		
		String previousReport = input.stringNamed(GenericFieldNames.REPOSITORY_REPORT);
		fields.put(GenericFieldNames.REPOSITORY_REPORT, previousReport + _repository.report());
		
		Loggers.xsystem.info("INPUT" + input.asString());
		Fields result = fields.putAll(input);
		Loggers.xsystem.info("RESULT" + result.asString());
		return result;
	}
	
	public String repositoryName(){
		return new StringBuilder(GenericCommandNames.PRINT_REPOSITORY).append("-").append(_repositoryName).toString();
	}

	@Override
	public Fields componingInputKeys() {
		return Fields.empty();
	}

	@Override
	public Fields componingOutputKeys() {
		return Fields.empty();
	}

	@Override
	public String getName() {
		return GenericCommandNames.PRINT_REPOSITORY;
	}
}