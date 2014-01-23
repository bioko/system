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

package it.bioko.system.service.push.impl;

import it.bioko.system.service.push.NotificationFailureException;
import it.bioko.system.service.push.PushServiceImplementation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestNotificationImplementation implements PushServiceImplementation {

	private static Map<String, String> _devices = new HashMap<String, String>();
	
	private static Map<String, List<String>> _messages = new HashMap<String, List<String>>();

	@Override
	public void addPushReceiver(String userToken, String deviceToken, boolean pushStatus) throws NotificationFailureException {
		if (pushStatus) {
			_devices.put(userToken, deviceToken);
		} else {
			_devices.remove(userToken);
		}
	}

	@Override
	public void sendPush(String userToken, String message) {
		String deviceToken = _devices.get(userToken);
		List<String> deviceMessages = _messages.get(deviceToken);
		
		if (deviceMessages == null) {
			deviceMessages = new LinkedList<String>();
			_messages.put(deviceToken, deviceMessages);
		}
		deviceMessages.add(message);
	}

	@Override
	public void sendBroadcastPush(String message) {
		// TODO Auto-generated method stub
	}

	public static List<String> getMessagesForUser(String userToken) {
		return _messages.get(_devices.get(userToken));
	}
	public static void register(String userToken, String deviceToken) {
		_devices.put(userToken, deviceToken);
	}
}
