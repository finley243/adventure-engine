package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
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
	public void executeSuccess(Context context, ScriptReturnTarget returnTarget) {
		context.game().data().getFaction(targetFaction).setRelation(relationFaction, relation);
		sendReturn(new ScriptReturn(null, false, false, null));
	}

}
