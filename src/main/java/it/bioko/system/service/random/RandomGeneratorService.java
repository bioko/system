package it.bioko.system.service.random;

import it.bioko.system.ConfigurationEnum;
import it.bioko.system.service.random.impl.ProdRandomGeneratorService;
import it.bioko.system.service.random.impl.TestRandomGeneratorService;
import it.bioko.system.service.random.RandomGeneratorServiceImplementation;

public class RandomGeneratorService {
	
	private RandomGeneratorServiceImplementation _impl;


	public RandomGeneratorService(ConfigurationEnum configuration) {
		
		
		switch (configuration) {
		case PROD:
			_impl = new ProdRandomGeneratorService();
			break;
		case DEMO:
			_impl = new ProdRandomGeneratorService();
			break;
		default:
			_impl = new TestRandomGeneratorService();
			break;
		}
		
	}
	
	
	public String generateString(String label, int length) {
		return _impl.generateString(label, length);
	}


	public Integer generateInteger(String label, int n) {
		return _impl.generateInteger(label, n);
	}

}
