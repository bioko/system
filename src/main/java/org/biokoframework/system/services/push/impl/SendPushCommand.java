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

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.services.push.IPushNotificationService;
import org.biokoframework.system.services.push.NotificationFailureException;
import org.biokoframework.system.services.queue.IQueueService;
import org.biokoframework.utils.fields.Fields;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SendPushCommand extends AbstractCommand {

	private static final Logger LOGGER = Logger.getLogger(SendPushCommand.class);
	
	private final IQueueService fPushQueueService;
	private final String fPusherdrilloUrl;
	private final String fAppToken;
	private final String fAppSecret;

	@Inject
	public SendPushCommand(@Named("pushQueue") IQueueService pushQueueService, 
			@Named("pusherdrilloUrl") String pusherdrilloUrl, 
			@Named("pusherdrilloAppToken") String appToken, @Named("puserdrilloAppSecret") String appSecret) {
		
		fPushQueueService = pushQueueService;
		fPusherdrilloUrl = pusherdrilloUrl;
		fAppToken = appToken;
		fAppSecret = appSecret;
	}
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);

		Fields pushFields;
		while((pushFields = fPushQueueService.popFields()) != null) {
			String userToken = pushFields.get(IPushNotificationService.USER_TOKEN);
			String content = pushFields.get(GenericFieldNames.CONTENT);
			Boolean production = pushFields.get(IPushNotificationService.PRODUCTION);

			try {
				if (StringUtils.isEmpty(userToken)) {
					sendBroadcastPush(content, production);
				} else {
					sendPush(userToken, content, production);
				}
			} catch (NotificationFailureException exception) {
				LOGGER.error("Pushing", exception);
			}
			
		}
			
		logOutput();
		return new Fields();
	}
	
	private void sendPush(String userToken, String message, Boolean production) throws NotificationFailureException {
		try {
			
			Fields fields = new Fields();
			fields.put(AbstractPushNotificationService.APP_TOKEN, fAppToken);
			fields.put(AbstractPushNotificationService.APP_SECRET, fAppSecret);
			fields.put(AbstractPushNotificationService.USER_TOKEN, userToken);
			fields.put(AbstractPushNotificationService.MESSAGE, message);
			fields.put(IPushNotificationService.PRODUCTION, production);
			
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(fPusherdrilloUrl + AbstractPushNotificationService.SEND_TARGETED_MESSAGE);
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

	private void sendBroadcastPush(String message, boolean production) throws NotificationFailureException {
		try {
			
			Fields fields = new Fields();
			fields.put(AbstractPushNotificationService.APP_TOKEN, fAppToken);
			fields.put(AbstractPushNotificationService.APP_SECRET, fAppSecret);
			fields.put(AbstractPushNotificationService.MESSAGE, message);
			fields.put(IPushNotificationService.PRODUCTION, production);
			
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(fPusherdrilloUrl + AbstractPushNotificationService.SEND_BROADCAST_MESSAGE);
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
