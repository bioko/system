package it.bioko.system.exceptions;

import it.bioko.system.KILL_ME.XSystemIdentityCard;
import it.bioko.utils.domain.ErrorEntity;
import it.bioko.utils.fields.Fields;

public class SystemExceptionsFactory {

	public static SystemNotFoundException createSystemNotFound(XSystemIdentityCard xSystemIdentityCard) {

		String message = new StringBuilder()
							.append("System")
							.append(xSystemIdentityCard.report().replace("\n", " "))
							.append(" not found")
							.toString();
		
		Fields fields = Fields.single(ErrorEntity.ERROR_MESSAGE, message);		
		return new SystemNotFoundException(new ErrorEntity(fields));
	}

}
