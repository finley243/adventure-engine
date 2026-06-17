package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Bark;
import com.github.finley243.adventureengine.world.environment.Area;

public class SensoryEvent {

	private final Area[] origins;
	private final String lineVisible;
	private final String lineAudible;
	private final Context context;
	private final Action action;
	private final Bark bark;
	private final boolean isDetectedBySelf;
	private final boolean isLoud;

	public SensoryEvent(Area origin, String lineVisible, Context context, boolean isDetectedBySelf, Action action, Bark bark) {
		this(new Area[]{origin}, lineVisible, null, context, isDetectedBySelf, false, action, bark);
	}

	public SensoryEvent(Area origin, String lineVisible, String lineAudible, Context context, boolean isDetectedBySelf, boolean isLoud, Action action, Bark bark) {
		this(new Area[]{origin}, lineVisible, lineAudible, context, isDetectedBySelf, isLoud, action, bark);
	}

	public SensoryEvent(Area[] origins, String lineVisible, String lineAudible, Context context, boolean isDetectedBySelf, boolean isLoud, Action action, Bark bark) {
		this.origins = origins;
		this.lineVisible = lineVisible;
		this.lineAudible = lineAudible;
		this.context = context;
		this.action = action;
		this.bark = bark;
		this.isDetectedBySelf = isDetectedBySelf;
		this.isLoud = isLoud;
	}
	
	public Area[] getOrigins() {
		return origins;
	}
	
	public String getLineVisible() {
		return lineVisible;
	}

	public String getLineAudible() {
		return lineAudible;
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

	public Context getContext() {
		return context;
	}

	public boolean isDetectedBySelf() {
		return isDetectedBySelf;
	}

	public boolean isLoud() {
		return isLoud;
	}

}
