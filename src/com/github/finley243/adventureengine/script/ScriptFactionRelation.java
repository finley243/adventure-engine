package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Faction.FactionRelation;

public class ScriptFactionRelation implements Script {

	private final String targetFaction;
	private final String relationFaction;
	private final FactionRelation relation;
	
	public ScriptFactionRelation(String targetFaction, String relationFaction, FactionRelation relation) {
		this.targetFaction = targetFaction;
		this.relationFaction = relationFaction;
		this.relation = relation;
	}

	@Override
	public void execute(Actor subject) {
		Data.getFaction(targetFaction).setRelation(relationFaction, relation);
	}

}
