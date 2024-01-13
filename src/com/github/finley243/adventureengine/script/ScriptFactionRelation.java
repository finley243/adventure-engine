package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Faction.FactionRelation;

public class ScriptFactionRelation extends Script {

	private final String targetFaction;
	private final String relationFaction;
	private final FactionRelation relation;
	
	public ScriptFactionRelation(String targetFaction, String relationFaction, FactionRelation relation) {
		this.targetFaction = targetFaction;
		this.relationFaction = relationFaction;
		this.relation = relation;
	}

	@Override
	public ScriptReturnData execute(Context context) {
		context.game().data().getFaction(targetFaction).setRelation(relationFaction, relation);
		return new ScriptReturnData(null, false, false, null);
	}

}
