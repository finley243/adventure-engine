package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.event.RenderMenuEvent;
import com.github.finley243.adventureengine.event.RenderTextEvent;

public interface UserInterface {

	public void onTextEvent(RenderTextEvent event);
	
	public void onMenuEvent(RenderMenuEvent event);
	
}
