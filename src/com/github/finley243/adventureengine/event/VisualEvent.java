package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;

public class VisualEvent {

	private Area origin;
	private String line;
	private Context context;
	
	public VisualEvent(Area origin, String line, Context context) {
		this.origin = origin;
		this.line = line;
		this.context = context;
	}
	
	public Area getOrigin() {
		return origin;
	}
	
	public String getText() {
		return TextGen.generate(line, context);
	}
	
}
