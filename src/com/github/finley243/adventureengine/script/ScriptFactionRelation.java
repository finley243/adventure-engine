package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Faction.FactionRelation;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ScriptFactionRelation extends Script {

	private final String targetFaction;
	private final String relationFaction;
	private final FactionRelation relation;
	
	public ScriptFactionRelation(Condition condition, Map<String, Expression> localParameters, String targetFaction, String relationFaction, FactionRelation relation) {
		super(condition, localParameters);
		this.targetFaction = targetFaction;
		this.relationFaction = relationFaction;
		this.relation = relation;
	}

	@Override
	public void executeSuccess(ContextScript context) {
		context.game().data().getFaction(targetFaction).setRelation(relationFaction, relation);
	}

}
