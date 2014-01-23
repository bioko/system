package it.bioko.system.service.description.dummy;

import it.bioko.system.command.crud.annotation.CrudCommand;
import it.bioko.system.entity.login.Login;
import it.bioko.system.service.description.SystemCommands;

@SystemCommands("dummySystem")
public class DummySystemCommands {

	@CrudCommand(entity = Login.class, repoName = "loginRepo")
	public static final String LOGIN = "login";
	
	@CrudCommand(entity = DummyEntityWithLocation.class, repoName = "dummyRepo")
	public static final String AN_ENTITY = "anEntity";
	
}
