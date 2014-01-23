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
