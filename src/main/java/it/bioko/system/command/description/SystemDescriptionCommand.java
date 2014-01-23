package it.bioko.system.command.description;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.command.Command;
import it.bioko.system.command.CommandException;
import it.bioko.system.service.description.JsonSystemDescription;
import it.bioko.system.service.description.JsonSystemDescriptor;
import it.bioko.utils.fields.Fields;

public class SystemDescriptionCommand extends Command {

	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);
		
		JsonSystemDescriptor descriptor = new JsonSystemDescriptor();
		JsonSystemDescription description = descriptor.describeSystem(_context);
		
		Fields output = Fields.single(GenericFieldNames.RESPONSE, description);
		logOutput(output);
		return output;
	}

}
