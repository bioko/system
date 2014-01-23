package it.bioko.system.service.random.impl;

import it.bioko.system.service.random.RandomGeneratorServiceImplementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestRandomGeneratorService implements RandomGeneratorServiceImplementation {
	
	
	private static HashMap<String, Integer> _currentIndexes = new HashMap<String, Integer>();
	private static HashMap<String,List<String>> _fakeRandomQueues = new HashMap<String, List<String>>();
	
	private static HashMap<String, Integer> _currentIndexesInt = new HashMap<String, Integer>();
	private static HashMap<String, List<Integer>> _fakeRandomQueuesInt = new HashMap<String, List<Integer>>();

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
	public Integer generateInteger(String label, int n) {
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
		Integer value = null; 
				
		try {
			value =	_fakeRandomQueuesInt.get(label).get(idx);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("[EASY MAN] Test random queue with label '"+label+"' has arrived at hits end, current index is "+idx);
		}
		
		return value;
	}

	public static void addRandomQueueInt(String queueLabel, List<Integer> randomValues) {
		_fakeRandomQueuesInt.put(queueLabel, randomValues);
		Integer queueIdx = new Integer(-1);
		_currentIndexesInt.put(queueLabel, queueIdx);
	}
	
}
