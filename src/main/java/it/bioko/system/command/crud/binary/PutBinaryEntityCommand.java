package it.bioko.system.command.crud.binary;

import it.bioko.system.KILL_ME.commons.GenericCommandNames;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.HttpMethod;
import it.bioko.system.command.Command;
import it.bioko.system.command.CommandException;
import it.bioko.system.context.Context;
import it.bioko.system.entity.EntityClassNameTranslator;
import it.bioko.system.entity.binary.BinaryEntity;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.system.factory.binary.BinaryEntityRepository;
import it.bioko.system.repository.core.SafeRepositoryHelper;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class PutBinaryEntityCommand extends Command {

	private final Context _context;
	private final BinaryEntityRepository _blobRepo;
	private final String _blobFieldName;

	public PutBinaryEntityCommand(Context context, BinaryEntityRepository blobRepo, String blobName) {
		_context = context;
		_blobRepo = blobRepo;
		_blobFieldName = EntityClassNameTranslator.toFieldName(blobName);
	}

	@Override
	public Fields execute(Fields input) throws CommandException {
		Fields result = Fields.empty();

		Logger logger = _context.get(Context.LOGGER);
		
		try {
			logger.info("INPUT: " + input.asJson());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String blobId = input.stringNamed(DomainEntity.ID);
		if (blobId == null || blobId.isEmpty()) {
			throw CommandExceptionsFactory.createExpectedFieldNotFound(DomainEntity.ID);
		}
		BinaryEntity existingBlob = _blobRepo.retrieveWithoutFile(blobId);
		if (existingBlob == null) {
			throw CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class.getSimpleName(), blobId);
		}
		
		BinaryEntity newBlob = (BinaryEntity) input.objectNamed(_blobFieldName);
		newBlob.setId(blobId);
		
		ArrayList<BinaryEntity> response = new ArrayList<BinaryEntity>();
		
		if (!newBlob.isValid()) {
			throw CommandExceptionsFactory.createNotCompleteEntity(newBlob.getClass().getSimpleName());
		}
		newBlob = SafeRepositoryHelper.save(_blobRepo, newBlob, _context);
		if (newBlob == null) {
			throw CommandExceptionsFactory.createBadCommandInvocationException();
		}
		response.add(newBlob);
		
		
		result.put(GenericFieldNames.RESPONSE, response);
		logger.info("OUTPUT after execution: " + result.asString());
		logger.info("END CRUD Command:" + this.getClass().getSimpleName());
		return result;
	}

	@Override
	public String getName() {
		return GenericCommandNames.composeRestCommandName(HttpMethod.PUT, BinaryEntity.class.getSimpleName());
	}
	
	
	@Override
	public Fields componingInputKeys() {
		return Fields.empty();
	}
	
	@Override
	public Fields componingOutputKeys() {		
		return Fields.empty();
	}

}
