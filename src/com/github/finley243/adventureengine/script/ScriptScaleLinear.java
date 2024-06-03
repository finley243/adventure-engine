package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptScaleLinear extends Script {

    public ScriptScaleLinear(int line) {
        super(line);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression input = context.getLocalVariables().get("input").getExpression();
        Expression inputMin = context.getLocalVariables().get("inputMin").getExpression();
        Expression inputMax = context.getLocalVariables().get("inputMax").getExpression();
        Expression outputMin = context.getLocalVariables().get("outputMin").getExpression();
        Expression outputMax = context.getLocalVariables().get("outputMax").getExpression();
        if (input.getDataType() != Expression.DataType.INTEGER && input.getDataType() != Expression.DataType.FLOAT) return new ScriptReturnData(null, null, new ScriptErrorData("Input parameter is not an integer or float", getLine()));
        if (inputMin.getDataType() != Expression.DataType.INTEGER && inputMin.getDataType() != Expression.DataType.FLOAT) return new ScriptReturnData(null, null, new ScriptErrorData("InputMin parameter is not an integer or float", getLine()));
        if (inputMax.getDataType() != Expression.DataType.INTEGER && inputMax.getDataType() != Expression.DataType.FLOAT) return new ScriptReturnData(null, null, new ScriptErrorData("InputMax parameter is not an integer or float", getLine()));
        if (outputMin.getDataType() != Expression.DataType.INTEGER && outputMin.getDataType() != Expression.DataType.FLOAT) return new ScriptReturnData(null, null, new ScriptErrorData("OutputMin parameter is not an integer or float", getLine()));
        if (outputMax.getDataType() != Expression.DataType.INTEGER && outputMax.getDataType() != Expression.DataType.FLOAT) return new ScriptReturnData(null, null, new ScriptErrorData("OutputMax parameter is not an integer or float", getLine()));
        float inputValue;
        float inputMinValue;
        float inputMaxValue;
        float outputMinValue;
        float outputMaxValue;
        if (input.getDataType() == Expression.DataType.INTEGER) {
            inputValue = input.getValueInteger();
        } else {
            inputValue = input.getValueFloat();
        }
        if (inputMin.getDataType() == Expression.DataType.INTEGER) {
            inputMinValue = inputMin.getValueInteger();
        } else {
            inputMinValue = inputMin.getValueFloat();
        }
        if (inputMax.getDataType() == Expression.DataType.INTEGER) {
            inputMaxValue = inputMax.getValueInteger();
        } else {
            inputMaxValue = inputMax.getValueFloat();
        }
        if (outputMin.getDataType() == Expression.DataType.INTEGER) {
            outputMinValue = outputMin.getValueInteger();
        } else {
            outputMinValue = outputMin.getValueFloat();
        }
        if (outputMax.getDataType() == Expression.DataType.INTEGER) {
            outputMaxValue = outputMax.getValueInteger();
        } else {
            outputMaxValue = outputMax.getValueFloat();
        }
        inputValue = MathUtils.bound(inputValue, inputMinValue, inputMaxValue);
        float scaledValue = ((inputValue - inputMinValue) / (inputMaxValue - inputMinValue)) * (outputMaxValue - outputMinValue) + outputMinValue;
        return new ScriptReturnData(Expression.constant(scaledValue), FlowStatementType.RETURN, null);
    }

}
