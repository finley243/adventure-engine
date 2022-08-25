package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Bark;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;

public class SensoryEvent {

	private final Area[] origins;
	private final String lineVisible;
	private final String lineAudible;
	private final Context context;
	private final Action action;
	private final Bark bark;
	private final Actor subject;
	private final Actor target;
	private final boolean isLoud;

	public SensoryEvent(Area origin, String lineVisible, Context context, Action action, Bark bark, Actor subject, Actor target) {
		this(new Area[]{origin}, lineVisible, null, context, false, action, bark, subject, target);
	}

	public SensoryEvent(Area[] origins, String lineVisible, Context context, Action action, Bark bark, Actor subject, Actor target) {
		this(origins, lineVisible, null, context, false, action, bark, subject, target);
	}

	public SensoryEvent(Area origin, String lineVisible, String lineAudible, Context context, boolean isLoud, Action action, Bark bark, Actor subject, Actor target) {
		this(new Area[]{origin}, lineVisible, lineAudible, context, isLoud, action, bark, subject, target);
	}

	public SensoryEvent(Area[] origins, String lineVisible, String lineAudible, Context context, boolean isLoud, Action action, Bark bark, Actor subject, Actor target) {
		this.origins = origins;
		this.lineVisible = lineVisible;
		this.lineAudible = lineAudible;
		this.context = context;
		this.action = action;
		this.bark = bark;
		this.subject = subject;
		this.target = target;
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

	public Bark getBark() {
		return bark;
	}

	public boolean isAction() {
		return action != null;
	}

	public boolean isBark() {
		return bark != null;
	}

	public Actor getSubject() {
		return subject;
	}

	public Actor getTarget() {
		return target;
	}

	public boolean isLoud() {
		return isLoud;
	}

}
