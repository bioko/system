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

package org.biokoframework.system.services.random.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.biokoframework.system.services.random.IRandomService;

public class TestRandomGeneratorService implements IRandomService {
	
	
	private static HashMap<String, Integer> _currentIndexes = new HashMap<String, Integer>();
	private static HashMap<String,List<String>> _fakeRandomQueues = new HashMap<String, List<String>>();
	
	private static HashMap<String, Integer> _currentIndexesInt = new HashMap<String, Integer>();
	private static HashMap<String, List<Long>> _fakeRandomQueuesInt = new HashMap<String, List<Long>>();

	@Override
	public String generateString(String label, int length) {
		if (_fakeRandomQueues.get(label)==null) {
			System.out.println("[EASY MAN] Test random queue with label '"+label+"' is void");
			return null;
		}
		
		Integer idx = _currentIndexes.get(label);
		if (idx == null) {
			idx = new Integer(-1);
//			_currentIndexes.put(label, idx);
		}
		
		idx++;
		
		_currentIndexes.put(label, idx);		
		String value = null; 
				
		try {
			value =	_fakeRandomQueues.get(label).get(idx);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("[EASY MAN] Test random queue with label '"+label+"' has arrived at hits end, current index is "+idx);
		}
		
		return value;
	}
	
	public static void addRandomQueue(String queueLabel, List<String> randomValues) {
		_fakeRandomQueues.put(queueLabel, randomValues);
		Integer queueIdx = new Integer(-1);
		_currentIndexes.put(queueLabel, queueIdx);
	}
	
	public static void addRandomQueue(String queueLabel, String[] randomValues) {
		ArrayList<String> randomValuesAsArrayList = new ArrayList<String>();
		for(String rv: randomValues) 
			randomValuesAsArrayList.add(rv);
		
		addRandomQueue(queueLabel, randomValuesAsArrayList);
	}

	public static void setSingleRandomValue(String queueLabel, String randomValue) {
		List<String> singleValueList = new ArrayList<String>();
		singleValueList.add(randomValue);
		_fakeRandomQueues.put(queueLabel, singleValueList);
		Integer queueIdx = new Integer(-1);
		_currentIndexes.put(queueLabel, queueIdx);
		
	}

	@Override
	public Long generateInteger(String label, int n) {
		if (_fakeRandomQueuesInt.get(label)==null) {
			System.err.println("[EASY MAN] Test random queue with label '"+label+"' is void");
			return null;
		}
		
		Integer idx = _currentIndexesInt.get(label);
		if (idx == null) {
			idx = new Integer(-1);
//			_currentIndexes.put(label, idx);
		}
		
		idx++;
		
		_currentIndexesInt.put(label, idx);		
		Long value = null; 
				
		try {
			value =	_fakeRandomQueuesInt.get(label).get(idx);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("[EASY MAN] Test random queue with label '"+label+"' has arrived at hits end, current index is "+idx);
		}
		
		return value;
	}

    @Override
    public UUID generateUUID() {
        return UUID.fromString("00000000-0000-0000-0000-000000000000");
    }

    public static void addRandomQueueInt(String queueLabel, List<Long> randomValues) {
		_fakeRandomQueuesInt.put(queueLabel, randomValues);
		Integer queueIdx = new Integer(-1);
		_currentIndexesInt.put(queueLabel, queueIdx);
	}
	
}
