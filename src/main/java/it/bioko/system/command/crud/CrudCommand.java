package it.bioko.system.command.crud;

import it.bioko.system.KILL_ME.commons.GenericCommandNames;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.command.CommandException;
import it.bioko.system.command.ValidationException;
import it.bioko.system.command.KILL_ME.SetCommand;
import it.bioko.system.context.Context;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.system.repository.core.Repository;
import it.bioko.system.repository.core.SafeRepositoryHelper;
import it.bioko.system.service.description.JsonSystemDescriptor;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;

import java.util.List;

import org.apache.log4j.Logger;

public class CrudCommand<T extends DomainEntity> extends SetCommand {

	private Class<T> _domainEntityClass;
	private Repository<T> _domainEntityRepository;
	private Context _context;


	public CrudCommand(Context context, Class<T> domainEntityClass, Repository<T> domainEntityRepository) {

		super(CrudComponingKeysBuilder.inputKeys(domainEntityClass),
				CrudComponingKeysBuilder.outputKeys(domainEntityClass));

		_context = context;
		_domainEntityClass = domainEntityClass;
		_domainEntityRepository = domainEntityRepository;
	}

	@Override
	public Fields execute(Fields input) throws CommandException {
		Fields result = Fields.empty();
		CrudMethod crudMethod = CrudMethod.fromRestCommand(input.stringNamed(FieldNames.COMMAND_NAME));

		Logger logger = _context.get(Context.LOGGER);
		
		try {
			logger.info("INPUT: " + input.asJson());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		result.put(GenericCommandNames.CRUD_METHOD, crudMethod.value());
		T actualEntity = null;
		try {
			actualEntity = _domainEntityClass.getConstructor(Fields.class).newInstance(input);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		if (crudMethod.equals(CrudMethod.OPTIONS)) {
			result.put(GenericFieldNames.RESPONSE, new JsonSystemDescriptor().describeEntity(_domainEntityClass));
		} else if (!actualEntity.isValid() && (crudMethod.equals(CrudMethod.POST) || crudMethod.equals(CrudMethod.PUT))) {
			throw CommandExceptionsFactory.createContainerException( 
					new ValidationException(actualEntity.getValidationErrors()));
		} else {
			// TODO MATTO ma che schifo di codice!!!!
			List<T> response = SafeRepositoryHelper.call(_domainEntityRepository, actualEntity, crudMethod.value(), _context);
			if (response.size() > 0) {
				result.put(GenericFieldNames.RESPONSE, response);
			} else {
				throw CommandExceptionsFactory.createEntityNotFound(_domainEntityClass.getSimpleName(), actualEntity.getId());
			}
			result.putAll(input);
		}
		logger.info("OUTPUT after execution: " + result.asString());
		logger.info("END CRUD Command:" + this.getClass().getSimpleName());
		return result;
	}

	@Override
	public String getName() {
		return "CRUD_" + _domainEntityClass.getSimpleName();
	}
}
