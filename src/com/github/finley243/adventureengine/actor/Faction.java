package com.github.finley243.adventureengine.actor;

import java.util.Map;

public class Faction {

	public enum FactionRelation {
		ENEMY, NEUTRAL, FRIEND
	}
	
	private final String ID;
	private final FactionRelation defaultRelation;
	private final Map<String, FactionRelation> relations;
	
	public Faction(String ID, FactionRelation defaultRelation, Map<String, FactionRelation> relations) {
		this.ID = ID;
		this.defaultRelation = defaultRelation;
		this.relations = relations;
	}
	
	public String getID() {
		return ID;
	}
	
	public FactionRelation getRelationTo(String factionID) {
		if(factionID.equals(this.ID)) {
			return FactionRelation.FRIEND;
		} else {
			return relations.getOrDefault(factionID, defaultRelation);
		}
	}
	
	public void setRelation(String factionID, FactionRelation relation) {
		relations.put(factionID, relation);
	}
	
}
