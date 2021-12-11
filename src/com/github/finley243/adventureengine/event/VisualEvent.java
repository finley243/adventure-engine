package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.HashSet;
import java.util.Set;

public class VisualEvent {

	private final Area[] origins;
	private final String line;
	private final Context context;
	private final Action action;
	private final Actor subject;
	
	public VisualEvent(Area origin, String line, Context context, Action action, Actor subject) {
		this.origins = new Area[]{origin};
		this.line = line;
		this.context = context;
		this.action = action;
		this.subject = subject;
	}

	public VisualEvent(Area[] origins, String line, Context context, Action action, Actor subject) {
		this.origins = origins;
		this.line = line;
		this.context = context;
		this.action = action;
		this.subject = subject;
	}
	
	public Area[] getOrigins() {
		return origins;
	}
	
	public String getText() {
		return TextGen.generate(line, context);
	}

	public Action getAction() {
		return action;
	}

	public Actor getSubject() {
		return subject;
	}

}
