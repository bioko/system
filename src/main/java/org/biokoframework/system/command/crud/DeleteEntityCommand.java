package org.biokoframework.system.command.crud;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

public class DeleteEntityCommand extends AbstractCommand {

	private final Class<? extends DomainEntity> fDomainEntityClass;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Inject
	public DeleteEntityCommand(@Named("entity") Class domainEntityClass) {
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
