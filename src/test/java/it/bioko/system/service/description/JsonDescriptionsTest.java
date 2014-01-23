package it.bioko.system.service.description;

import static it.bioko.utils.matcher.Matchers.matchesJSONString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import it.bioko.system.ConfigurationEnum;
import it.bioko.system.KILL_ME.XSystemIdentityCard;
import it.bioko.system.command.AbstractCommandHandler;
import it.bioko.system.command.ProxyCommandHandler;
import it.bioko.system.context.Context;
import it.bioko.system.context.ProxyContext;
import it.bioko.system.entity.login.Login;
import it.bioko.system.factory.AnnotatedCommandHandlerFactory;
import it.bioko.system.service.description.dummy.DummyContextFactory;
import it.bioko.system.service.description.dummy.DummyEntityWithLocation;
import it.bioko.system.service.description.dummy.DummyReferencingEntity;
import it.bioko.system.service.description.dummy.DummySystemCommands;

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
