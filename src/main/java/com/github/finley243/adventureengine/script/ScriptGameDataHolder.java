package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptGameDataHolder extends Script {

    private final String type;
    private final Script objectID;

    public ScriptGameDataHolder(ScriptTraceData traceData, String type, Script objectID) {
        super(traceData);
        this.type = type;
        this.objectID = objectID;
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        Script.ScriptReturnData returnData = objectID.execute(scriptRuntime, context);
        if (returnData.error() != null) {
            return returnData;
        } else if (returnData.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression contains unexpected flow statement", getTraceData()));
        } else if (returnData.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression value is null", getTraceData()));
        } else if (returnData.value().getDataType() != Expression.DataType.STRING) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression value is not a string", getTraceData()));
        }
        String objectIDString = returnData.value().getValueString();
        return switch (type) {
            case "actor" -> new ScriptReturnData(Expression.valueHolder(scriptRuntime.getActor(objectIDString)), null, null);
            case "object" -> new ScriptReturnData(Expression.valueHolder(scriptRuntime.getObject(objectIDString)), null, null);
            case "item" -> new ScriptReturnData(Expression.valueHolder(scriptRuntime.getItem(objectIDString)), null, null);
            case "itemTemplate" -> new ScriptReturnData(Expression.valueHolder(scriptRuntime.getItemTemplate(objectIDString)), null, null);
            case "area" -> new ScriptReturnData(Expression.valueHolder(scriptRuntime.getArea(objectIDString)), null, null);
            case "room" -> new ScriptReturnData(Expression.valueHolder(scriptRuntime.getRoom(objectIDString)), null, null);
            case "scene" -> new ScriptReturnData(Expression.valueHolder(scriptRuntime.getScene(objectIDString)), null, null);
            default -> new ScriptReturnData(null, null, new ScriptErrorData("Invalid gameData type: " + type, getTraceData()));
        };
    }

}
