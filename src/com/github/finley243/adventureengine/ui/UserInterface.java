package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.event.ui.RenderMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;

public interface UserInterface {

	void onTextEvent(RenderTextEvent event);
	
	void onMenuEvent(RenderMenuEvent event);
	
}
