package it.bioko.system.service.currenttime.impl;

import it.bioko.system.service.currenttime.CurrentTimeServiceImplementation;

import org.joda.time.DateTime;

public class ProdCurrentTimeService implements CurrentTimeServiceImplementation {

	@Override
	public DateTime getCurrentTimeAsCalendar() {		
		return new DateTime();
	}

}
