package com.github.finley243.adventureengine.actor;

import java.util.Map;

public class Faction {

	public enum FactionRelation {
		ENEMY, NEUTRAL, FRIEND
	}
	
	private String ID;
	private FactionRelation defaultRelation;
	private Map<String, FactionRelation> relations;
	
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
		} else if(!relations.containsKey(factionID)) {
			return defaultRelation;
		} else {
			return relations.get(factionID);
		}
	}
	
	public void setRelation(String factionID, FactionRelation relation) {
		relations.put(factionID, relation);
	}
	
}
