package org.biokoframework.system.command.crud;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class DeleteEntityCommand extends AbstractCommand {

	private final Class<? extends DomainEntity> fDomainEntityClass;

	@Inject
	public DeleteEntityCommand(@Assisted Class<? extends DomainEntity> domainEntityClass) {
		fDomainEntityClass = domainEntityClass;
	}
	
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);
		Repository<? extends DomainEntity> repository = getRepository(fDomainEntityClass);
		
		String id = input.get(DomainEntity.ID);
		if (StringUtils.isEmpty(id)) {
			throw CommandExceptionsFactory.createExpectedFieldNotFound(DomainEntity.ID);
		}
		
		ArrayList<DomainEntity> response = new ArrayList<>();
		response.add(repository.delete(id));
		
		Fields output = new Fields(
				GenericFieldNames.RESPONSE, response);
		logOutput(output);
		return output;
	}

}
