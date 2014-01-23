package it.bioko.system.service.description.dummy;

import it.bioko.system.KILL_ME.exception.SystemException;
import it.bioko.system.context.Context;
import it.bioko.system.service.context.AbstractContextFactory;

import org.apache.log4j.Logger;

public class DummyContextFactory extends AbstractContextFactory {

	@Override
	protected Context configureForProd(Context context) throws SystemException {
		// TODO Auto-generated method stub
		return context;
	}

	@Override
	protected Context configureForDev(Context context) throws SystemException {
		// TODO Auto-generated method stub
		return context;
	}

	@Override
	protected Context configureForDemo(Context context) throws SystemException {
		// TODO Auto-generated method stub
		return context;
	}

	@Override
	protected Logger getSystemLogger() {
		return Logger.getRootLogger();
	}

}
