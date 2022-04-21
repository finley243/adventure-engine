package com.github.finley243.adventureengine.actor;

public class ActorReference {

	public enum ReferenceType {
		PLAYER, SUBJECT, REFERENCE
	}
	
	private final ReferenceType type;
	private final String reference;
	
	public ActorReference(ReferenceType type, String reference) {
		this.type = type;
		this.reference = reference;
	}
	
	public Actor getActor(Actor subject) {
		switch(type) {
		case PLAYER:
			return subject.game().data().getPlayer();
		case REFERENCE:
			return subject.game().data().getActor(reference);
		case SUBJECT:
		default:
			return subject;
		}
	}
	
	public boolean isPlayer() {
		return type == ReferenceType.PLAYER;
	}

}
