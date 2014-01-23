package it.bioko.system.service.push;

import it.bioko.system.ConfigurationEnum;
import it.bioko.system.context.Context;
import it.bioko.system.service.push.impl.ProdNotificationImplementation;
import it.bioko.system.service.push.impl.TestNotificationImplementation;

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
