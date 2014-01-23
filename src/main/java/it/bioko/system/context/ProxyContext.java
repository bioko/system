package it.bioko.system.context;

import it.bioko.system.command.AbstractCommandHandler;
import it.bioko.system.event.SystemListener;
import it.bioko.system.repository.core.Repository;
import it.bioko.utils.domain.DomainEntity;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

public class ProxyContext extends Context {

	private Context _context;

	public ProxyContext(Context context) {
		_context = context;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String name) {
		return (T) _context.get(name);
	}

	@Override
	public void put(String name, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Logger getLogger() {
		return _context.getLogger();
	}

	@Override
	public String getSystemName() {
		return _context.getSystemName();
	}

	@Override
	public void addRepository(String repoName, Repository<?> repo) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public <DE extends DomainEntity> Repository<DE> getRepository(String repoName) {
		return _context.getRepository(repoName);
	}

	@Override
	public void setCommandHandler(AbstractCommandHandler commandHandler) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public AbstractCommandHandler getCommandHandler() {
		return _context.getCommandHandler();
	}
	
	@Override 
	public void setSystemProperty(String name, String value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getSystemProperty(String name) {
		return _context.getSystemProperty(name);
	}

	@Override
	public void addSystemListener(SystemListener listener) {
		_context.addSystemListener(listener);
	}
	
	@Override
	public List<SystemListener> getSystemListeners() {
		return Collections.unmodifiableList(_context.getSystemListeners());
	}
	
}
