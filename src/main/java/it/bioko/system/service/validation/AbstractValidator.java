package it.bioko.system.service.validation;

import it.bioko.system.context.Context;
import it.bioko.utils.domain.ErrorEntity;
import it.bioko.utils.fields.Fields;

import java.util.List;

public abstract class AbstractValidator {
	
	protected Context _context;
		
	public abstract void validate(Fields input, List<ErrorEntity> errors);
	
	public void setContext(Context context) {
		_context = context;
	}
	
	public void onContextInitialized() {}
	

}
