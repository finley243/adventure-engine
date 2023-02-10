package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.ContextScript;

public class ActorReference {

	public enum ReferenceType {
		PLAYER, SUBJECT, TARGET, REFERENCE
	}
	
	private final ReferenceType type;
	private final String reference;
	
	public ActorReference(ReferenceType type, String reference) {
		this.type = type;
		this.reference = reference;
	}
	
	public Actor getActor(ContextScript context) {
		switch(type) {
		case PLAYER:
			return context.game().data().getPlayer();
		case REFERENCE:
			return context.game().data().getActor(reference);
		case TARGET:
			return context.getTarget();
		case SUBJECT:
		default:
			return context.getSubject();
		}
	}
	
	public boolean isPlayer() {
		return type == ReferenceType.PLAYER;
	}

}
