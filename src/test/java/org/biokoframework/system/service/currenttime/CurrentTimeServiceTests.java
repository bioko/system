/*
 * Copyright (c) 2014																 
 *	Mikol Faro			<mikol.faro@gmail.com>
 *	Simone Mangano		<simone.mangano@ieee.org>
 *	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package org.biokoframework.system.service.currenttime;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.service.currenttime.CurrentTimeService;
import org.biokoframework.system.service.currenttime.impl.TestCurrentTimeService;
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
