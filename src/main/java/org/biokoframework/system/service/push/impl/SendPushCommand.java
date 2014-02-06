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

package org.biokoframework.system.service.push.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.command.AbstractCommand;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.service.push.NotificationFailureException;
import org.biokoframework.system.service.push.PushService;
import org.biokoframework.system.service.queue.Queue;
import org.biokoframework.utils.fields.Fields;

public class SendPushCommand extends AbstractCommand {

	private String _pusherdrilloUrl;
	private String _appToken;
	private String _appSecret;

	@Override
	public void onContextInitialized() {
		_pusherdrilloUrl = fContext.getSystemProperty(ProdNotificationImplementation.PUSHERDRILLO_URL);
		_appToken = fContext.getSystemProperty(ProdNotificationImplementation.PUSHERDRILLO_APP_TOKEN);
		_appSecret = fContext.getSystemProperty(ProdNotificationImplementation.PUSHERDRILLO_APP_SECRET);
	}
	
	@Override
	public Fields execute(Fields input) throws CommandException {
		logInput(input);

		Queue pushQueue = fContext.get(input.get(GenericFieldNames.QUEUE_NAME).toString());
		
		Fields pushFields;
		while((pushFields = pushQueue.popFields()) != null) {
			String userToken = pushFields.get(PushService.USER_TOKEN);
			String content = pushFields.get(GenericFieldNames.CONTENT);
			String production = pushFields.get(PushService.PRODUCTION);

			try {
				if (StringUtils.isEmpty(userToken)) {
					sendBroadcastPush(content, production);
				} else {
					sendPush(userToken, content, production);
				}
			} catch (NotificationFailureException exception) {
				fContext.getLogger().error("Pushing", exception);
			}
			
		}
			
		logOutput();
		return new Fields();
	}

	@Override
	public String getName() {
		return SendPushCommand.class.getSimpleName();
	}
	
	private void sendPush(String userToken, String message, String production) throws NotificationFailureException {
		try {
			
			Fields fields = new Fields();
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
			
			Fields fields = new Fields();
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
