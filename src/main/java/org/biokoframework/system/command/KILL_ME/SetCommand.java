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

package org.biokoframework.system.command.KILL_ME;

import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.utils.fields.Fields;

import java.util.LinkedHashMap;

@Deprecated
public abstract class SetCommand extends AbstractCommand {

	private LinkedHashMap<String, Fields> _inputKeysMap;
	private LinkedHashMap<String, Fields> _outputKeysMap;

	public SetCommand(LinkedHashMap<String, Fields> inputKeysMap,
			LinkedHashMap<String, Fields> outputKeysMap) {
//		if (!_inputKeysMap.keySet().equals(_outputKeysMap.keySet())) {
//			throw new UnsupportedOperationException("The two sets must have the same keys");
//		}
		_inputKeysMap = inputKeysMap;
		_outputKeysMap = outputKeysMap;
	}
	
	public Fields componingInputKeys(String aCommandName) {
		if (_inputKeysMap.containsKey(aCommandName)) {
			return _inputKeysMap.get(aCommandName); 
		}
		return new Fields();
	}

	public Fields componingOutputKeys(String aCommandName) {
		if (_outputKeysMap.containsKey(aCommandName)) {
			return _outputKeysMap.get(aCommandName);
		}
		return new Fields();
	}
	
}
