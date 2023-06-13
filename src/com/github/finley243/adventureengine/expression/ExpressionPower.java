package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;

public class ExpressionPower extends Expression {

    private final Expression expressionBase;
    private final Expression expressionExponent;

    public ExpressionPower(Expression expressionBase, Expression expressionExponent) {
        if (expressionBase == null || expressionExponent == null) throw new IllegalArgumentException("Null expression");
        if (!(expressionBase.getDataType() == DataType.FLOAT || expressionBase.getDataType() == DataType.INTEGER)) {
            throw new IllegalArgumentException("Non-numeric expression provided to ExpressionPower");
        }
        if (!(expressionExponent.getDataType() == DataType.FLOAT || expressionExponent.getDataType() == DataType.INTEGER)) {
            throw new IllegalArgumentException("Non-numeric expression provided to ExpressionPower");
        }
        this.expressionBase = expressionBase;
        this.expressionExponent = expressionExponent;
    }

    @Override
    public DataType getDataType() {
        return DataType.FLOAT;
    }

    @Override
    public float getValueFloat(Context context) {
        float valueBase;
        float valueExponent;
        if (expressionBase.getDataType() == DataType.INTEGER) {
            valueBase = expressionBase.getValueInteger(context);
        } else {
            valueBase = expressionBase.getValueFloat(context);
        }
        if (expressionExponent.getDataType() == DataType.INTEGER) {
            valueExponent = expressionExponent.getValueInteger(context);
        } else {
            valueExponent = expressionExponent.getValueFloat(context);
        }
        return (float) Math.pow(valueBase, valueExponent);
    }

}
