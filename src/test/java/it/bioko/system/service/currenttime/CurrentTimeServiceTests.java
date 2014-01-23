package it.bioko.system.service.currenttime;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import it.bioko.system.ConfigurationEnum;
import it.bioko.system.service.currenttime.CurrentTimeService;
import it.bioko.system.service.currenttime.impl.TestCurrentTimeService;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

public class CurrentTimeServiceTests {
	
	@Test
	public void calendarMockedTest() {
		
		String sourceTsAsString = "1974-10-19 01:02:03";
		String sourceTsPattern = "yyyy-MM-dd HH:mm:ss";
		DateTime sourceTsAsDateTime = DateTimeFormat.forPattern(sourceTsPattern).parseDateTime(sourceTsAsString);
		
		CurrentTimeService ctService = new CurrentTimeService(ConfigurationEnum.DEV);
		TestCurrentTimeService.setCalendar(sourceTsAsString, sourceTsPattern);
		
		DateTime currentTsAsDateTime = ctService.getCurrentTimeAsDateTime();
		assertThat(currentTsAsDateTime, is(equalTo(sourceTsAsDateTime)));
	}
	
	@Test
	public void calendarMockedProd() throws InterruptedException {
		CurrentTimeService ctService = new CurrentTimeService(ConfigurationEnum.PROD);
		
		DateTime firstTime = new DateTime();
		
		Thread.sleep(10);
		DateTime secondTime = ctService.getCurrentTimeAsDateTime();
		Thread.sleep(10);
		DateTime thirdTime = ctService.getCurrentTimeAsDateTime();
		
		assertThat(firstTime.isBefore(secondTime), is(equalTo(true)));
		assertThat(secondTime.isBefore(thirdTime), is(equalTo(true)));
	}

}
