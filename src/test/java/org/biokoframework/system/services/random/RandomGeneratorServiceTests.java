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

package org.biokoframework.system.services.random;

import org.biokoframework.system.services.random.impl.ProdRandomGeneratorService;
import org.biokoframework.system.services.random.impl.TestRandomGeneratorService;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class RandomGeneratorServiceTests {
	
	@Test
	public void usingForTestEnvironmentTest() {
		IRandomService testService = new TestRandomGeneratorService();
		
		ArrayList<String> fakeRandomsQueue1 = new ArrayList<String>();
		fakeRandomsQueue1.add("QUEUE1_FIRST_FAKE_RANDOM");		
		fakeRandomsQueue1.add("QUEUE1_SECOND_FAKE_RANDOM");
		
		ArrayList<String> fakeRandomsQueue2 = new ArrayList<String>();
		fakeRandomsQueue2.add("QUEUE2_FIRST_FAKE_RANDOM");		
		fakeRandomsQueue2.add("QUEUE2_SECOND_FAKE_RANDOM");
			
		TestRandomGeneratorService.addRandomQueue("queue1", fakeRandomsQueue1);
		TestRandomGeneratorService.addRandomQueue("queue2", fakeRandomsQueue2);
		
		
		assertEquals("QUEUE1_FIRST_FAKE_RANDOM", testService.generateString("queue1",100));
		assertEquals("QUEUE2_FIRST_FAKE_RANDOM", testService.generateString("queue2",100));
		assertEquals("QUEUE2_SECOND_FAKE_RANDOM", testService.generateString("queue2",100));
		assertEquals("QUEUE1_SECOND_FAKE_RANDOM", testService.generateString("queue1",100));
	}
	
	@Test
	public void usingForProdEnvironmentTest() {
		IRandomService prodService = new ProdRandomGeneratorService();

		String trueRandom = prodService.generateString("NotUsedInProducion",10);
		assertEquals(10, trueRandom.length());
		System.out.println("First random string: "+trueRandom);
		String secondTrueRandom = prodService.generateString("NotUsedInProducion",25);
		assertEquals(25, secondTrueRandom.length());
		System.out.println("Second random string: "+secondTrueRandom);
		
	}

}
