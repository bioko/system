package it.bioko.system.event;

import java.util.EventListener;

public interface SystemListener extends EventListener {
	
	public void systemShutdown();

}
