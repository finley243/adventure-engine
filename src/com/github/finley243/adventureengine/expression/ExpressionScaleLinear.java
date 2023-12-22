package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;

public class ExpressionScaleLinear extends Expression {

    private final Expression input;
    private final Expression inputMin;
    private final Expression inputMax;
    private final Expression outputMin;
    private final Expression outputMax;

    public ExpressionScaleLinear(Expression input, Expression inputMin, Expression inputMax, Expression outputMin, Expression outputMax) {
        if (input == null) throw new IllegalArgumentException("Input expression is null");
        if (inputMin == null) throw new IllegalArgumentException("InputMin expression is null");
        if (inputMax == null) throw new IllegalArgumentException("InputMax expression is null");
        if (outputMin == null) throw new IllegalArgumentException("OutputMin expression is null");
        if (outputMax == null) throw new IllegalArgumentException("OutputMax expression is null");
        this.input = input;
        this.inputMin = inputMin;
        this.inputMax = inputMax;
        this.outputMin = outputMin;
        this.outputMax = outputMax;
    }

    @Override
    public DataType getDataType() {
        return DataType.FLOAT;
    }

    @Override
    public float getValueFloat(Context context) {
        if (input.getDataType() != DataType.INTEGER && input.getDataType() != DataType.FLOAT) throw new IllegalArgumentException("Input expression is not an int or float");
        if (inputMin.getDataType() != DataType.INTEGER && inputMin.getDataType() != DataType.FLOAT) throw new IllegalArgumentException("InputMin expression is not an int or float");
        if (inputMax.getDataType() != DataType.INTEGER && inputMax.getDataType() != DataType.FLOAT) throw new IllegalArgumentException("InputMax expression is not an int or float");
        if (outputMin.getDataType() != DataType.INTEGER && outputMin.getDataType() != DataType.FLOAT) throw new IllegalArgumentException("OutputMin expression is not an int or float");
        if (outputMax.getDataType() != DataType.INTEGER && outputMax.getDataType() != DataType.FLOAT) throw new IllegalArgumentException("OutputMax expression is not an int or float");
        float inputValue;
        float inputMinValue;
        float inputMaxValue;
        float outputMinValue;
        float outputMaxValue;
        if (input.getDataType() == DataType.INTEGER) {
            inputValue = input.getValueInteger(context);
        } else {
            inputValue = input.getValueFloat(context);
        }
        if (input.getDataType() == DataType.INTEGER) {
            inputMinValue = inputMin.getValueInteger(context);
        } else {
            inputMinValue = inputMin.getValueFloat(context);
        }
        if (input.getDataType() == DataType.INTEGER) {
            inputMaxValue = inputMax.getValueInteger(context);
        } else {
            inputMaxValue = inputMax.getValueFloat(context);
        }
        if (input.getDataType() == DataType.INTEGER) {
            outputMinValue = outputMin.getValueInteger(context);
        } else {
            outputMinValue = outputMin.getValueFloat(context);
        }
        if (input.getDataType() == DataType.INTEGER) {
            outputMaxValue = outputMax.getValueInteger(context);
        } else {
            outputMaxValue = outputMax.getValueFloat(context);
        }
        inputValue = MathUtils.bound(inputValue, inputMinValue, inputMaxValue);
        return ((inputValue - inputMinValue) / (inputMaxValue - inputMinValue)) * (outputMaxValue - outputMinValue) + outputMinValue;
    }

}
