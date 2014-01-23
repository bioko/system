package it.bioko.system.service.push.impl;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.context.Context;
import it.bioko.system.service.push.NotificationFailureException;
import it.bioko.system.service.push.PushService;
import it.bioko.system.service.push.PushServiceImplementation;
import it.bioko.system.service.queue.Queue;
import it.bioko.utils.fields.FieldValues;
import it.bioko.utils.fields.Fields;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class ProdNotificationImplementation implements PushServiceImplementation {

	protected static final String PUSHERDRILLO_APP_TOKEN = "pusherdrilloAppToken";
	protected static final String PUSHERDRILLO_APP_SECRET = "pusherdrilloAppSecret";
	protected static final String PUSHERDRILLO_URL = "pusherdrilloUrl";

	protected static final String APP_TOKEN = "appToken";
	protected static final String APP_SECRET = "appSecret";
	protected static final String DEVICE_TYPE = "deviceType";
	protected static final String IOS_TYPE = "iOS";
	protected static final String DEVICE_VERSION = "deviceVersion";
	protected static final String USER_TOKEN = "userToken";
	protected static final String PUSH_STATUS = "pushStatus";
	protected static final String MESSAGE = "message";
	protected static final String REGISTER_DEVICE = "/register-device";
	protected static final String SEND_TARGETED_MESSAGE = "/send-message";
	protected static final String SEND_BROADCAST_MESSAGE = "/send-broadcast-message";

	private String _pusherdrilloUrl;
	private String _appToken;
	private Context _context;
	private String _production;
	
	public ProdNotificationImplementation(Context context, boolean production) {
		_context = context;
		_pusherdrilloUrl = context.getSystemProperty(PUSHERDRILLO_URL);
		_appToken = context.getSystemProperty(PUSHERDRILLO_APP_TOKEN);
		_production = production ? FieldValues.TRUE : FieldValues.FALSE;
	}
	
	@Override
	public void addPushReceiver(String userToken, String deviceToken, boolean pushStatus) throws NotificationFailureException {
		try {
		
			Fields fields = Fields.empty();
			fields.put(APP_TOKEN, _appToken);
			fields.put(PushService.DEVICE_TOKEN, deviceToken);
			fields.put(DEVICE_TYPE, IOS_TYPE);
			fields.put(DEVICE_VERSION, "6");
			fields.put(USER_TOKEN, userToken);
			fields.put(PUSH_STATUS, pushStatus);
			
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(_pusherdrilloUrl + REGISTER_DEVICE);
			post.setEntity(new StringEntity(fields.toJSONString(), ContentType.APPLICATION_JSON));
	
			post.completed();
			
			HttpResponse response = client.execute(post);
			
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new NotificationFailureException(EntityUtils.toString(response.getEntity()));
			}
			
		} catch (NotificationFailureException exception) {
			throw exception;
		} catch (Exception exception) {
			throw new NotificationFailureException(exception);
		}
	}

	@Override
	public void sendPush(String userToken, String message) throws NotificationFailureException {
		Fields sendMailInputFields = Fields.empty();
		sendMailInputFields.put(PushService.USER_TOKEN, userToken);
		sendMailInputFields.put(GenericFieldNames.CONTENT, message);
		sendMailInputFields.put(PushService.PRODUCTION, _production);
		
		Queue pushQueue = _context.get(PushService.PUSH_QUEUE);
		pushQueue.pushFields(sendMailInputFields);
	}

	@Override
	public void sendBroadcastPush(String message) throws NotificationFailureException {
		Fields sendMailInputFields = Fields.empty();
		sendMailInputFields.put(GenericFieldNames.CONTENT, message);
		sendMailInputFields.put(PushService.PRODUCTION, _production);
		
		Queue pushQueue = _context.get(PushService.PUSH_QUEUE);
		pushQueue.pushFields(sendMailInputFields);
	}



}
