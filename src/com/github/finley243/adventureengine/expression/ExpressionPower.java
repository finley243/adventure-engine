package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionPower extends Expression {

    private final Expression expressionBase;
    private final Expression expressionExponent;

    public ExpressionPower(Expression expressionBase, Expression expressionExponent) {
        if (expressionBase == null || expressionExponent == null) throw new IllegalArgumentException("Null expression");
        this.expressionBase = expressionBase;
        this.expressionExponent = expressionExponent;
    }

    @Override
    public DataType getDataType(Context context) {
        return DataType.FLOAT;
    }

    @Override
    public float getValueFloat(Context context) {
        if (!(expressionBase.getDataType(context) == DataType.FLOAT || expressionBase.getDataType(context) == DataType.INTEGER)) {
            throw new IllegalArgumentException("Non-numeric expression provided to ExpressionPower");
        }
        if (!(expressionExponent.getDataType(context) == DataType.FLOAT || expressionExponent.getDataType(context) == DataType.INTEGER)) {
            throw new IllegalArgumentException("Non-numeric expression provided to ExpressionPower");
        }
        float valueBase;
        float valueExponent;
        if (expressionBase.getDataType(context) == DataType.INTEGER) {
            valueBase = expressionBase.getValueInteger(context);
        } else {
            valueBase = expressionBase.getValueFloat(context);
        }
        if (expressionExponent.getDataType(context) == DataType.INTEGER) {
            valueExponent = expressionExponent.getValueInteger(context);
        } else {
            valueExponent = expressionExponent.getValueFloat(context);
        }
        return (float) Math.pow(valueBase, valueExponent);
    }

}
