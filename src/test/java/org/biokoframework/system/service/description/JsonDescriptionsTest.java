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

package org.biokoframework.system.service.description;

import static org.biokoframework.utils.matcher.Matchers.matchesJSONString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.KILL_ME.XSystemIdentityCard;
import org.biokoframework.system.command.AbstractCommandHandler;
import org.biokoframework.system.command.ProxyCommandHandler;
import org.biokoframework.system.context.Context;
import org.biokoframework.system.context.ProxyContext;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.factory.AnnotatedCommandHandlerFactory;
import org.biokoframework.system.service.description.JsonEntityDescription;
import org.biokoframework.system.service.description.JsonSystemDescriptor;
import org.biokoframework.system.service.description.dummy.DummyContextFactory;
import org.biokoframework.system.service.description.dummy.DummyEntityWithLocation;
import org.biokoframework.system.service.description.dummy.DummyReferencingEntity;
import org.biokoframework.system.service.description.dummy.DummySystemCommands;
import org.junit.BeforeClass;
import org.junit.Test;

public class JsonDescriptionsTest {

	@BeforeClass
	public static void log4JtoConsole() {
//		BasicConfigurator.configure();
	}
	
	@Test
	public void loginEntityTest() {
		
		JsonSystemDescriptor descriptor = new JsonSystemDescriptor();
		JsonEntityDescription description = descriptor.describeEntity(Login.class);
		
		assertThat(description.getEntityName(), is(equalTo("Login")));
		assertThat(description.toJSONString(), 
				matchesJSONString(
					"{"
						+ "\"id\":{"
							+ "\"type\":\"Integer\""
						+ "},"
						+ "\"userEmail\":{"
							+ "\"type\":\"String\","
							+ "\"hints\":{\"cmsType\":\"email\"}"
						+ "},"
						+ "\"password\":{"
							+ "\"type\":\"String\","
							+ "\"hints\":{\"encrypt\":\"oneWay\"}"
						+ "},"
						+ "\"facebookId\":{"
							+ "\"type\":\"String\","
							+ "\"mandatory\":false"
						+ "},"
						+ "\"roles\":{"
							+ "\"type\":\"String\","
							+ "\"mandatory\":false"
						+ "}"
					+ "}"));

	}
	
	@Test
	public void entityWithForeignKeyTest() {
		JsonSystemDescriptor descriptor = new JsonSystemDescriptor();
		
		assertThat(descriptor.describeEntity(DummyReferencingEntity.class).toJSONString(), 
				matchesJSONString(
					"{"
						+ "\"id\":{"
							+ "\"type\":\"Integer\""
						+ "},"
						+ "\"aField\":{"
							+ "\"type\":\"String\""
						+ "},"
						+ "\"loginId\":{"
							+ "\"type\":\"Integer\","
							+ "\"reference\":\"Login\","
							+ "\"referenceField\":\"id\""
						+ "}"
					+ "}"));
	}
	
	@Test
	public void entityWithVirtualAndFields() {
		new DummySystemCommands();
		JsonSystemDescriptor descriptor = new JsonSystemDescriptor();
		
		assertThat(descriptor.describeEntity(DummyEntityWithLocation.class).toJSONString(), 
				matchesJSONString(
					"{"
						+ "\"id\":{"
							+ "\"type\":\"Integer\""
						+ "},"
						+ "\"aField\":{"
							+ "\"type\":\"String\""
						+ "},"
						+ "\"longitude\":{"
							+ "\"type\":\"Double\","
							+ "\"hints\":{\"cmsType\":\"hidden\"}"
						+ "},"
						+ "\"latitude\":{"
							+ "\"type\":\"Double\","
							+ "\"hints\":{\"cmsType\":\"hidden\"}"
						+ "},"
						+ "\"location\":{"
							+ "\"virtual\":true,"
							+ "\"hints\":{"
								+ "\"cmsType\":\"location\","
								+ "\"latitudeField\":\"latitude\","
								+ "\"longitudeField\":\"longitude\""
							+ "}"
						+ "}"
					+ "}"));
	}
	
	@Test
	public void dummySystemTest() throws Exception {
		JsonSystemDescriptor descriptor = new JsonSystemDescriptor();
		
		XSystemIdentityCard dummyIdentityCard = new XSystemIdentityCard("dummySystem", "1.0", ConfigurationEnum.PROD);
		
		Context context = new DummyContextFactory().create(dummyIdentityCard);
		context.put(Context.COMMANDS_CLASS, DummySystemCommands.class);
		AbstractCommandHandler commandHandler = AnnotatedCommandHandlerFactory.create(DummySystemCommands.class, new ProxyContext(context), dummyIdentityCard);
		context.setCommandHandler(new ProxyCommandHandler(commandHandler));
		
		assertThat(descriptor.describeSystem(context).toJSONString(),
				matchesJSONString(
					"{"
						+ "\"entities\":{"
							+ "\"Login\":{"
								+ "\"id\":{"
									+ "\"type\":\"Integer\""
								+ "},"
								+ "\"userEmail\":{"
									+ "\"type\":\"String\","
									+ "\"hints\":{\"cmsType\":\"email\"}"
								+ "},"
								+ "\"password\":{"
									+ "\"type\":\"String\","
									+ "\"hints\":{\"encrypt\":\"oneWay\"}"
								+ "},"
								+ "\"facebookId\":{"
									+ "\"type\":\"String\","
									+ "\"mandatory\":false"
								+ "},"
								+ "\"roles\":{"
									+ "\"type\":\"String\","
									+ "\"mandatory\":false"
								+ "}"
							+ "},"
							+ "\"DummyEntityWithLocation\":{"
								+ "\"id\":{"
									+ "\"type\":\"Integer\""
								+ "},"
								+ "\"aField\":{"
									+ "\"type\":\"String\""
								+ "},"
								+ "\"longitude\":{"
									+ "\"type\":\"Double\","
									+ "\"hints\":{\"cmsType\":\"hidden\"}"
								+ "},"
								+ "\"latitude\":{"
									+ "\"type\":\"Double\","
									+ "\"hints\":{\"cmsType\":\"hidden\"}"
								+ "},"
								+ "\"location\":{"
									+ "\"virtual\":true,"
									+ "\"hints\":{"
										+ "\"cmsType\":\"location\","
										+ "\"latitudeField\":\"latitude\","
										+ "\"longitudeField\":\"longitude\""
									+ "}"
								+ "}"
							+ "}"
						+ "}"
					+ "}"));
	}
	
}