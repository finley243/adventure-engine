package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.HashSet;
import java.util.Set;

public class ExpressionFilterSet extends Expression {

    private final Expression setExpression;
    private final String parameterName;
    private final Condition filterCondition;

    public ExpressionFilterSet(Expression setExpression, String parameterName, Condition filterCondition) {
        if (setExpression == null) throw new IllegalArgumentException("ExpressionFilterSet setExpression is null");
        if (parameterName == null) throw new IllegalArgumentException("ExpressionFilterSet parameterName is null");
        if (filterCondition == null) throw new IllegalArgumentException("ExpressionFilterSet filterCondition is null");
        this.setExpression = setExpression;
        this.parameterName = parameterName;
        this.filterCondition = filterCondition;
    }

    @Override
    public DataType getDataType(Context context) {
        return DataType.STRING_SET;
    }

    @Override
    public Set<String> getValueStringSet(Context context) {
        if (setExpression.getDataType(context) != DataType.STRING_SET) throw new IllegalArgumentException("ExpressionFilterSet setExpression is not a set");
        Set<String> originalValues = setExpression.getValueStringSet(context);
        Set<String> filteredValues = new HashSet<>();
        for (String currentValue : originalValues) {
            Expression parameter = new ExpressionConstantString(currentValue);
            if (filterCondition.isMet(new Context(context, new MapBuilder<String, Expression>().put(parameterName, parameter).build()))) {
                filteredValues.add(currentValue);
            }
        }
        return filteredValues;
    }

}
