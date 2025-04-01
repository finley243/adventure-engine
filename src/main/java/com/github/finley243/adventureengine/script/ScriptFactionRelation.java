package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Faction.FactionRelation;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptFactionRelation extends Script {

    public ScriptFactionRelation(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
	public ScriptReturnData execute(Context context) {
		Expression factionExpression = context.getLocalVariables().get("faction").getExpression();
		Expression relatedFactionExpression = context.getLocalVariables().get("relatedFaction").getExpression();
		Expression relationExpression = context.getLocalVariables().get("relation").getExpression();
		if (factionExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("Faction parameter is not a string", getTraceData()));
		if (relatedFactionExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("RelatedFaction parameter is not a string", getTraceData()));
		if (relationExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("Relation parameter is not a string", getTraceData()));
		FactionRelation relation;
		switch (relationExpression.getValueString()) {
			case "ally" -> relation = FactionRelation.ALLY;
			case "neutral" -> relation = FactionRelation.NEUTRAL;
			case "hostile" -> relation = FactionRelation.HOSTILE;
			default -> {
				return new ScriptReturnData(null, null, new ScriptErrorData("Relation parameter is not a valid faction relation", getTraceData()));
			}
		}
		context.game().data().getFaction(factionExpression.getValueString()).setRelation(relatedFactionExpression.getValueString(), relation);
		return new ScriptReturnData(null, null, null);
	}

}
