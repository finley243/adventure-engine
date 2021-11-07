package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ScriptDialogue implements Script {

	private final ActorReference actor;
	private final String topic;
	
	public ScriptDialogue(ActorReference actor, String topic) {
		if(actor.isPlayer()) throw new IllegalArgumentException("ScriptDialogue must have a non-player actor reference");
		this.actor = actor;
		this.topic = topic;
	}

	@Override
	public void execute(Actor subject) {
		Data.getPlayer().startDialogue(actor.getActor(subject), topic);
	}

}
