package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Faction.FactionRelation;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptFactionRelation extends Script {

	private final String targetFaction;
	private final String relationFaction;
	private final FactionRelation relation;
	
	public ScriptFactionRelation(Condition condition, String targetFaction, String relationFaction, FactionRelation relation) {
		super(condition);
		this.targetFaction = targetFaction;
		this.relationFaction = relationFaction;
		this.relation = relation;
	}

	@Override
	public void executeSuccess(Actor subject) {
		Data.getFaction(targetFaction).setRelation(relationFaction, relation);
	}

}
