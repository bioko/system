package it.bioko.system.service.random.impl;

import it.bioko.system.service.random.RandomGeneratorServiceImplementation;

import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

public class ProdRandomGeneratorService implements RandomGeneratorServiceImplementation {

	Random rand = new Random();
	
	@Override
	public String generateString(String label, int length) {
		return RandomStringUtils.randomAlphabetic(length);
	}
	
	@Override
	public Integer generateInteger(String label, int n) {
		return rand.nextInt(n); 
	}

}
