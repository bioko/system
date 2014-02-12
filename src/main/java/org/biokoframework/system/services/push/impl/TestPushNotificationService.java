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

package org.biokoframework.system.services.push.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.biokoframework.system.services.push.NotificationFailureException;
import org.biokoframework.system.services.push.IPushNotificationService;

public class TestPushNotificationService implements IPushNotificationService {

	private static Map<String, String> fDevices = new HashMap<String, String>();
	
	private static Map<String, List<String>> fMessages = new HashMap<String, List<String>>();

	@Override
	public void addPushReceiver(String userToken, String deviceToken, boolean pushStatus) throws NotificationFailureException {
		if (pushStatus) {
			fDevices.put(userToken, deviceToken);
		} else {
			fDevices.remove(userToken);
		}
	}

	@Override
	public void sendPush(String userToken, String message) {
		String deviceToken = fDevices.get(userToken);
		List<String> deviceMessages = fMessages.get(deviceToken);
		
		if (deviceMessages == null) {
			deviceMessages = new LinkedList<String>();
			fMessages.put(deviceToken, deviceMessages);
		}
		deviceMessages.add(message);
	}

	@Override
	public void sendBroadcastPush(String message) {
		// TODO Auto-generated method stub
	}

	public static List<String> getMessagesForUser(String userToken) {
		return fMessages.get(fDevices.get(userToken));
	}
	public static void register(String userToken, String deviceToken) {
		fDevices.put(userToken, deviceToken);
	}
}
