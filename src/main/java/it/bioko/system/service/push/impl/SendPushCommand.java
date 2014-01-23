package it.bioko.system.service.push.impl;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.command.Command;
import it.bioko.system.command.CommandException;
import it.bioko.system.service.push.NotificationFailureException;
import it.bioko.system.service.push.PushService;
import it.bioko.system.service.queue.Queue;
import it.bioko.utils.fields.Fields;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class SendPushCommand extends Command {

	private String _pusherdrilloUrl;
	private String _appToken;
	private String _appSecret;

	@Override
	public void onContextInitialized() {
		_pusherdrilloUrl = _context.getSystemProperty(ProdNotificationImplementation.PUSHERDRILLO_URL);
		_appToken = _context.getSystemProperty(ProdNotificationImplementation.PUSHERDRILLO_APP_TOKEN);
		_appSecret = _context.getSystemProperty(ProdNotificationImplementation.PUSHERDRILLO_APP_SECRET);
	}
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);

		Queue pushQueue = _context.get(input.stringNamed(GenericFieldNames.QUEUE_NAME));
		
		Fields pushFields;
		while((pushFields = pushQueue.popFields()) != null) {
			String userToken = pushFields.stringNamed(PushService.USER_TOKEN);
			String content = pushFields.stringNamed(GenericFieldNames.CONTENT);
			String production = pushFields.stringNamed(PushService.PRODUCTION);

			try {
				if (StringUtils.isEmpty(userToken)) {
					sendBroadcastPush(content, production);
				} else {
					sendPush(userToken, content, production);
				}
			} catch (NotificationFailureException exception) {
				_context.getLogger().error("Pushing", exception);
			}
			
		}
			
		logOutput();
		return Fields.empty();
	}

	@Override
	public String getName() {
		return SendPushCommand.class.getSimpleName();
	}
	
	private void sendPush(String userToken, String message, String production) throws NotificationFailureException {
		try {
			
			Fields fields = Fields.empty();
			fields.put(ProdNotificationImplementation.APP_TOKEN, _appToken);
			fields.put(ProdNotificationImplementation.APP_SECRET, _appSecret);
			fields.put(ProdNotificationImplementation.USER_TOKEN, userToken);
			fields.put(ProdNotificationImplementation.MESSAGE, message);
			fields.put(PushService.PRODUCTION, production);
			
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(_pusherdrilloUrl + ProdNotificationImplementation.SEND_TARGETED_MESSAGE);
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

	private void sendBroadcastPush(String message, String production) throws NotificationFailureException {
		try {
			
			Fields fields = Fields.empty();
			fields.put(ProdNotificationImplementation.APP_TOKEN, _appToken);
			fields.put(ProdNotificationImplementation.APP_SECRET, _appSecret);
			fields.put(ProdNotificationImplementation.MESSAGE, message);
			fields.put(PushService.PRODUCTION, production);
			
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(_pusherdrilloUrl + ProdNotificationImplementation.SEND_BROADCAST_MESSAGE);
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

}
