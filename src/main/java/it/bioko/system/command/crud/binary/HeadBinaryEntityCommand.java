package it.bioko.system.command.crud.binary;

import it.bioko.system.KILL_ME.commons.GenericCommandNames;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.system.KILL_ME.commons.HttpMethod;
import it.bioko.system.command.Command;
import it.bioko.system.command.CommandException;
import it.bioko.system.context.Context;
import it.bioko.system.entity.binary.BinaryEntity;
import it.bioko.system.entity.description.ParameterEntity;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.system.factory.binary.BinaryEntityRepository;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class HeadBinaryEntityCommand extends Command {

	private final Context _context;
	private final BinaryEntityRepository _blobRepo;

	public HeadBinaryEntityCommand(Context context, BinaryEntityRepository blobRepo) {
		_context = context;
		_blobRepo = blobRepo;
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
		
		ArrayList<BinaryEntity> response = new ArrayList<BinaryEntity>();
		
		String blobId = input.stringNamed(DomainEntity.ID);
		if (blobId == null || blobId.isEmpty()) {
			throw CommandExceptionsFactory.createExpectedFieldNotFound(DomainEntity.ID);
		}
				
		BinaryEntity blob = _blobRepo.retrieveWithoutFile(blobId);
		if (blob == null) {
			throw CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class.getSimpleName(), blobId);
		}
		response.add(blob);
		
		result.put(GenericFieldNames.RESPONSE_CONTENT_TYPE, blob.get(BinaryEntity.MEDIA_TYPE));
		
		result.put(GenericFieldNames.RESPONSE, response);
		logger.info("OUTPUT after execution: " + result.asString());
		logger.info("END CRUD Command:" + this.getClass().getSimpleName());
		return result;
	}

	@Override
	public String getName() {
		return GenericCommandNames.composeRestCommandName(HttpMethod.GET, BinaryEntity.class.getSimpleName());
	}
	
	
	@Override
	public Fields componingInputKeys() {
		ArrayList<ParameterEntity> parameters = new ArrayList<ParameterEntity>(); 
		
		ParameterEntity parameter = new ParameterEntity(Fields.empty());
		parameter.set(ParameterEntity.NAME, DomainEntity.ID);
		parameter.set(ParameterEntity.HTTP_PARAMETER_TYPE, GenericFieldValues.URL_PATH);
		parameters.add(parameter);
		
		Fields resultFields = Fields.single(GenericFieldNames.INPUT, parameters);
		return resultFields;
	}
	
	@Override
	public Fields componingOutputKeys() {		
		return Fields.empty();
	}

}
