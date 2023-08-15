package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.event.ui.RenderNumericMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderChoiceMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.google.common.eventbus.Subscribe;

public interface UserInterface {

	@Subscribe
	void onTextEvent(RenderTextEvent event);

	@Subscribe
	void onMenuEvent(RenderChoiceMenuEvent event);

	@Subscribe
	void onNumericMenuEvent(RenderNumericMenuEvent event);
	
}
