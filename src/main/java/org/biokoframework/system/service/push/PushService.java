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

package org.biokoframework.system.service.push;

import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.context.Context;
import org.biokoframework.system.service.push.impl.ProdNotificationImplementation;
import org.biokoframework.system.service.push.impl.TestNotificationImplementation;

public class PushService {

	public static final String PUSH_QUEUE = "pushQueue";

	public static final String USER_TOKEN = "userToken";
	public static final String DEVICE_TOKEN = "deviceToken";
	public static final String ACCEPT_PUSH = "acceptPush";

	public static final String PRODUCTION = "production";
	
	private PushServiceImplementation _notifier;

	public PushService(Context context) {
		
		ConfigurationEnum configuration = context.get(Context.SYSTEM_CONFIGURATION);
		switch (configuration) {
		case PROD:
			_notifier = new ProdNotificationImplementation(context, true);
			break;
		case DEMO:
			_notifier = new ProdNotificationImplementation(context, false);
			break;
		default:
			_notifier = new TestNotificationImplementation();
			break;
		}
		
	}

	public void addPushReceiver(String userToken, String deviceToken, boolean pushStatus) throws NotificationFailureException {
		_notifier.addPushReceiver(userToken, deviceToken, pushStatus);
	}
	
	public void sendPush(String userToken, String message) throws NotificationFailureException {
		_notifier.sendPush(userToken, message);
	}
	
	public void sendPushBroadcastPush(String message) throws NotificationFailureException {
		_notifier.sendBroadcastPush(message);
	}
	
}
