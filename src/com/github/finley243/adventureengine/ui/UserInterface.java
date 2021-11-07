package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.event.RenderMenuEvent;
import com.github.finley243.adventureengine.event.RenderTextEvent;

public interface UserInterface {

	void onTextEvent(RenderTextEvent event);
	
	void onMenuEvent(RenderMenuEvent event);
	
}
