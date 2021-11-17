package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.world.environment.Area;

public class SoundEvent {

	private final Area origin;
	// If isLoud, sound can be heard through exits (not elevators)
	private final boolean isLoud;

	public SoundEvent(Area origin, boolean isLoud) {
		this.origin = origin;
		this.isLoud = isLoud;
	}

	public Area getOrigin() {
		return origin;
	}

	public boolean isLoud() {
		return isLoud;
	}
	
}
