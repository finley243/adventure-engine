package com.github.finley243.adventureengine.actor;

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
	
	public Actor getActor(Actor subject, Actor target) {
		switch(type) {
		case PLAYER:
			return subject.game().data().getPlayer();
		case REFERENCE:
			return subject.game().data().getActor(reference);
		case TARGET:
			return target;
		case SUBJECT:
		default:
			return subject;
		}
	}
	
	public boolean isPlayer() {
		return type == ReferenceType.PLAYER;
	}

}
