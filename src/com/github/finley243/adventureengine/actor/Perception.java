package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.VisualEvent;

public interface Perception {

	public void onVisualEvent(VisualEvent event);
	
	public void onSoundEvent(SoundEvent event);
	
}
