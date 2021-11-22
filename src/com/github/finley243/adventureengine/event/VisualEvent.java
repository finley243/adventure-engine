package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;

public class VisualEvent {

	private final Area origin;
	private final String line;
	private final Context context;
	private final Action action;
	private final Actor subject;
	
	public VisualEvent(Area origin, String line, Context context, Action action, Actor subject) {
		this.origin = origin;
		this.line = line;
		this.context = context;
		this.action = action;
		this.subject = subject;
	}
	
	public Area getOrigin() {
		return origin;
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
