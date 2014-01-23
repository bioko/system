package it.bioko.system.service.random;

public interface RandomGeneratorServiceImplementation {

	String generateString(String label, int length);

	Integer generateInteger(String label, int n);

}
