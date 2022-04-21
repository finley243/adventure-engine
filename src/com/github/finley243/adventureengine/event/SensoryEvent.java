package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;

public class SensoryEvent {

	public enum ResponseType {
		HOSTILE, INVESTIGATE, NONE
	}

	private final Area[] origins;
	private final String lineVisible;
	private final String lineAudible;
	private final Context context;
	private final Action action;
	private final Actor subject;
	private final boolean isLoud;
	private final ResponseType responseType;

	public SensoryEvent(Area origin, String lineVisible, Context context, Action action, Actor subject) {
		this(new Area[]{origin}, lineVisible, null, context, ResponseType.NONE, false, action, subject);
	}

	public SensoryEvent(Area[] origins, String lineVisible, Context context, Action action, Actor subject) {
		this(origins, lineVisible, null, context, ResponseType.NONE, false, action, subject);
	}

	public SensoryEvent(Area origin, String lineVisible, String lineAudible, Context context, ResponseType responseType, boolean isLoud, Action action, Actor subject) {
		this(new Area[]{origin}, lineVisible, lineAudible, context, responseType, isLoud, action, subject);
	}

	public SensoryEvent(Area[] origins, String lineVisible, String lineAudible, Context context, ResponseType responseType, boolean isLoud, Action action, Actor subject) {
		this.origins = origins;
		this.lineVisible = lineVisible;
		this.lineAudible = lineAudible;
		this.context = context;
		this.action = action;
		this.subject = subject;
		this.responseType = responseType;
		this.isLoud = isLoud;
	}
	
	public Area[] getOrigins() {
		return origins;
	}
	
	public String getTextVisible() {
		return TextGen.generate(lineVisible, context);
	}

	public String getTextAudible() {
		if(lineAudible == null) return null;
		return TextGen.generate(lineAudible, context);
	}

	public Action getAction() {
		return action;
	}

	public Actor getSubject() {
		return subject;
	}

	public ResponseType getResponseType() {
		return responseType;
	}

	public boolean isLoud() {
		return isLoud;
	}

}
