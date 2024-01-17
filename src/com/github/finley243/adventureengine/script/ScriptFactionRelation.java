package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Faction.FactionRelation;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptFactionRelation extends Script {

	@Override
	public ScriptReturnData execute(Context context) {
		Expression factionExpression = context.getLocalVariables().get("faction").getExpression();
		Expression relatedFactionExpression = context.getLocalVariables().get("relatedFaction").getExpression();
		Expression relationExpression = context.getLocalVariables().get("relation").getExpression();
		if (factionExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, "Faction parameter is not a string");
		if (relatedFactionExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, "RelatedFaction parameter is not a string");
		if (relationExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, "Relation parameter is not a string");
		FactionRelation relation;
		switch (relationExpression.getValueString()) {
			case "ally" -> relation = FactionRelation.ALLY;
			case "neutral" -> relation = FactionRelation.NEUTRAL;
			case "hostile" -> relation = FactionRelation.HOSTILE;
			default -> {
				return new ScriptReturnData(null, null, "Relation parameter is not a valid faction relation");
			}
		}
		context.game().data().getFaction(factionExpression.getValueString()).setRelation(relatedFactionExpression.getValueString(), relation);
		return new ScriptReturnData(null, null, null);
	}

}
