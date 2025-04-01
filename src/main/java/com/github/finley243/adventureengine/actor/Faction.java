package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;

import java.util.Map;

public class Faction extends GameInstanced {

	public enum FactionRelation {
		HOSTILE, NEUTRAL, ALLY
	}
	
	private final FactionRelation defaultRelation;
	private final Map<String, FactionRelation> relations;
	
	public Faction(Game game, String ID, FactionRelation defaultRelation, Map<String, FactionRelation> relations) {
		super(game, ID);
		this.defaultRelation = defaultRelation;
		this.relations = relations;
	}
	
	public FactionRelation getRelationTo(String factionID) {
		if (factionID.equals(this.getID())) {
			return FactionRelation.ALLY;
		} else {
			return relations.getOrDefault(factionID, defaultRelation);
		}
	}
	
	public void setRelation(String factionID, FactionRelation relation) {
		relations.put(factionID, relation);
	}
	
}
