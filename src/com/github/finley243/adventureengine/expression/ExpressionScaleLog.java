package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;

public class ExpressionScaleLog extends Expression {

    private final Expression input;
    private final Expression inputMin;
    private final Expression inputMax;
    private final Expression outputMin;
    private final Expression outputMax;

    public ExpressionScaleLog(Expression input, Expression inputMin, Expression inputMax, Expression outputMin, Expression outputMax) {
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
    public DataType getDataType(Context context) {
        return DataType.FLOAT;
    }

    @Override
    public float getValueFloat(Context context) {
        if (input.getDataType(context) != DataType.INTEGER && input.getDataType(context) != DataType.FLOAT) throw new IllegalArgumentException("Input expression is not an int or float");
        if (inputMin.getDataType(context) != DataType.INTEGER && inputMin.getDataType(context) != DataType.FLOAT) throw new IllegalArgumentException("InputMin expression is not an int or float");
        if (inputMax.getDataType(context) != DataType.INTEGER && inputMax.getDataType(context) != DataType.FLOAT) throw new IllegalArgumentException("InputMax expression is not an int or float");
        if (outputMin.getDataType(context) != DataType.INTEGER && outputMin.getDataType(context) != DataType.FLOAT) throw new IllegalArgumentException("OutputMin expression is not an int or float");
        if (outputMax.getDataType(context) != DataType.INTEGER && outputMax.getDataType(context) != DataType.FLOAT) throw new IllegalArgumentException("OutputMax expression is not an int or float");
        float inputValue;
        float inputMinValue;
        float inputMaxValue;
        float outputMinValue;
        float outputMaxValue;
        if (input.getDataType(context) == DataType.INTEGER) {
            inputValue = input.getValueInteger(context);
        } else {
            inputValue = input.getValueFloat(context);
        }
        if (inputMin.getDataType(context) == DataType.INTEGER) {
            inputMinValue = inputMin.getValueInteger(context);
        } else {
            inputMinValue = inputMin.getValueFloat(context);
        }
        if (inputMax.getDataType(context) == DataType.INTEGER) {
            inputMaxValue = inputMax.getValueInteger(context);
        } else {
            inputMaxValue = inputMax.getValueFloat(context);
        }
        if (outputMin.getDataType(context) == DataType.INTEGER) {
            outputMinValue = outputMin.getValueInteger(context);
        } else {
            outputMinValue = outputMin.getValueFloat(context);
        }
        if (outputMax.getDataType(context) == DataType.INTEGER) {
            outputMaxValue = outputMax.getValueInteger(context);
        } else {
            outputMaxValue = outputMax.getValueFloat(context);
        }
        inputValue = MathUtils.bound(inputValue, inputMinValue, inputMaxValue);
        return (float) (((Math.log(inputValue) - Math.log(inputMinValue))/(Math.log(inputMaxValue) - Math.log(inputMinValue))) * (outputMaxValue - outputMinValue) + outputMinValue);
    }

}
