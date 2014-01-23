package it.bioko.system.service.cron;

import it.bioko.system.KILL_ME.exception.SystemException;
import it.bioko.system.event.SystemListener;

import org.apache.log4j.Logger;

final class SchedulerKiller implements SystemListener {

	private final CronService _service;
	private final Logger _logger;
	
	public SchedulerKiller(CronService service, Logger logger) {
		_service = service;
		_logger = logger;
	}
	
	@Override
	public void systemShutdown() {
		try {
			_service.stop();
		} catch (SystemException exception) {
			_logger.error("Unable to stop scheduler", exception);
		}
	}
	
}