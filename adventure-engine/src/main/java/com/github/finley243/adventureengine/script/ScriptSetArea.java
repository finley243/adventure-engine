package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.world.Physical;
import com.github.finley243.adventureengine.world.environment.Area;

public class ScriptSetArea extends Script {

    public ScriptSetArea(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        Expression areaExpression = context.getLocalVariables().get("area").getExpression();
        Expression objectExpression = context.getLocalVariables().get("object").getExpression();
        if (areaExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, new ScriptErrorData("Area parameter is not a stat holder", getTraceData()));
        if (!(areaExpression.getValueStatHolder() instanceof Area area)) return new ScriptReturnData(null, null, new ScriptErrorData("Area parameter is not an area", getTraceData()));
        if (objectExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, new ScriptErrorData("Object parameter is not a stat holder", getTraceData()));
        if (!(objectExpression.getValueStatHolder() instanceof Physical physicalObject)) return new ScriptReturnData(null, null, new ScriptErrorData("Object parameter is not a physical object", getTraceData()));
        physicalObject.setArea(area);
        return new ScriptReturnData(null, null, null);
    }

}
