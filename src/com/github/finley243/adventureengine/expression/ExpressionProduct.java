package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.List;
import java.util.Set;

public class ExpressionProduct extends Expression {

    private final List<Expression> expressions;
    private final boolean isFloat;

    public ExpressionProduct(List<Expression> expressions) {
        if (expressions.isEmpty()) throw new IllegalArgumentException("Variable list is empty");
        this.expressions = expressions;
        boolean hasFloatVariable = false;
        for (Expression expression : expressions) {
            if (expression.getDataType() == DataType.FLOAT) {
                hasFloatVariable = true;
            } else if (expression.getDataType() != DataType.INTEGER) {
                throw new IllegalArgumentException("Only integers and floats are allowed");
            }
        }
        this.isFloat = hasFloatVariable;
    }

    @Override
    public DataType getDataType() {
        if (isFloat) {
            return DataType.FLOAT;
        } else {
            return DataType.INTEGER;
        }
    }

    @Override
    public boolean getValueBoolean(ContextScript context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getValueInteger(ContextScript context) {
        if (getDataType() != DataType.INTEGER) throw new UnsupportedOperationException();
        int product = 1;
        for (Expression expression : expressions) {
            product *= expression.getValueInteger(context);
        }
        return product;
    }

    @Override
    public float getValueFloat(ContextScript context) {
        if (getDataType() != DataType.FLOAT) throw new UnsupportedOperationException();
        float product = 1.0f;
        for (Expression expression : expressions) {
            if (expression.getDataType() == DataType.FLOAT) {
                product *= expression.getValueFloat(context);
            } else {
                product *= expression.getValueInteger(context);
            }
        }
        return product;
    }

    @Override
    public String getValueString(ContextScript context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getValueStringSet(ContextScript context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Inventory getValueInventory(ContextScript context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Noun getValueNoun(ContextScript context) {
        throw new UnsupportedOperationException();
    }

}
