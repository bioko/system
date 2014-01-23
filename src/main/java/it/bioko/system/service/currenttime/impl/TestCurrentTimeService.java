package it.bioko.system.service.currenttime.impl;

import it.bioko.system.service.currenttime.CurrentTimeServiceImplementation;
import it.bioko.utils.validator.Validator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class TestCurrentTimeService implements CurrentTimeServiceImplementation {

	private static DateTime _calendar = new DateTime();
	
	public static void setCalendar(String dateAsString, String dateFormat) {
		_calendar = DateTimeFormat.forPattern(dateFormat).parseDateTime(dateAsString);
	}
	
	
	@Override
	public DateTime getCurrentTimeAsCalendar() {		
		return _calendar;
	}


	public static void setCalendar(String dateAsString) {
		setCalendar(dateAsString, Validator.ISO_TIMESTAMP);
	}

	
}
