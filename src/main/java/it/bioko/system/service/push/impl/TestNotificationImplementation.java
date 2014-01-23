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
