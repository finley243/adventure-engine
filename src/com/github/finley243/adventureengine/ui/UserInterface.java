package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.event.ui.RenderMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.google.common.eventbus.Subscribe;

public interface UserInterface {

	@Subscribe
	void onTextEvent(RenderTextEvent event);

	@Subscribe
	void onMenuEvent(RenderMenuEvent event);
	
}
