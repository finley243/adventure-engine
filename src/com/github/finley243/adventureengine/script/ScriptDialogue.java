package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptDialogue extends Script {

	private final ActorReference actor;
	private final String topic;
	
	public ScriptDialogue(Condition condition, ActorReference actor, String topic) {
		super(condition);
		if(actor.isPlayer()) throw new IllegalArgumentException("ScriptDialogue must have a non-player actor reference");
		this.actor = actor;
		this.topic = topic;
	}

	@Override
	public void executeSuccess(Actor subject) {
		Data.getPlayer().startDialogue(actor.getActor(subject), topic);
	}

}
