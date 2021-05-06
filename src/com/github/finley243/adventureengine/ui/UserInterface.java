package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.event.DisplayMenuEvent;
import com.github.finley243.adventureengine.event.DisplayTextEvent;

public interface UserInterface {

	public void onTextEvent(DisplayTextEvent event);
	
	public void onMenuEvent(DisplayMenuEvent event);
	
}
