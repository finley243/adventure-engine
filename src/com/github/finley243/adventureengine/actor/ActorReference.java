package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Context;

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
	
	public Actor getActor(Context context) {
		return switch (type) {
			case PLAYER -> context.game().data().getPlayer();
			case REFERENCE -> context.game().data().getActor(reference);
			case TARGET -> context.getTarget();
			default -> context.getSubject();
		};
	}
	
	public boolean isPlayer() {
		return type == ReferenceType.PLAYER;
	}

}
