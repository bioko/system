package it.bioko.system.service.push;



public interface PushServiceImplementation {

	void addPushReceiver(String userToken, String deviceToken,	boolean pushStatus) throws NotificationFailureException;

	void sendPush(String userToken, String message) throws NotificationFailureException;

	void sendBroadcastPush(String message) throws NotificationFailureException;

}
