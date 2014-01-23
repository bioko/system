package it.bioko.system.service.currenttime;

import it.bioko.system.ConfigurationEnum;
import it.bioko.system.service.currenttime.impl.ProdCurrentTimeService;
import it.bioko.system.service.currenttime.impl.TestCurrentTimeService;
import it.bioko.system.service.currenttime.CurrentTimeServiceImplementation;

import org.joda.time.DateTime;

public class CurrentTimeService {
	
	CurrentTimeServiceImplementation _impl;
	
	public CurrentTimeService(ConfigurationEnum config) {
		switch (config) {
		case DEV:
			_impl = new TestCurrentTimeService();
			break;
			
		case PROD:
		case DEMO:
			_impl = new ProdCurrentTimeService();

		default:
			break;
		}
	}
	
	public DateTime getCurrentTimeAsDateTime() {
		return _impl.getCurrentTimeAsCalendar();
	}
	

}
