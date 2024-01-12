package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionCompare;

public class ScriptComparator extends Script implements ScriptReturnTarget {

    private final Script firstScript;
    private final Script secondScript;
    private final ExpressionCompare.Comparator comparator;

    public ScriptComparator(Script firstScript, Script secondScript, ExpressionCompare.Comparator comparator) {
        this.firstScript = firstScript;
        this.secondScript = secondScript;
        this.comparator = comparator;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
        runtimeStack.addContext(runtimeStack.getContext(), this);
        firstScript.execute(runtimeStack);
    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturnData scriptReturnData) {
        if (scriptReturnData.error() != null) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturnData);
        } else if (scriptReturnData.isReturn()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Expression cannot contain a return statement"));
        } else if (scriptReturnData.value() == null) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Expression did not receive a value"));
        } else if (runtimeStack.getTempExpressionList().size() == 1) {
            Expression firstScriptValue = runtimeStack.getTempExpressionList().getFirst();
            Expression secondScriptValue = scriptReturnData.value();
            if (!firstScriptValue.canCompareTo(secondScriptValue, runtimeStack.getContext())) {
                runtimeStack.closeContext();
                sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Expression received values that could not be compared"));
            } else {
                boolean comparatorResult = compareExpressions(firstScriptValue, secondScriptValue, runtimeStack.getContext());
                runtimeStack.closeContext();
                sendReturn(runtimeStack, new ScriptReturnData(Expression.constant(comparatorResult), false, false, null));
            }
        } else {
            Expression firstScriptValue = scriptReturnData.value();
            runtimeStack.addTempExpressionToList(firstScriptValue);
            secondScript.execute(runtimeStack);
        }
    }

    private boolean compareExpressions(Expression expression1, Expression expression2, Context context) {
        if ((expression1.getDataType(context) == Expression.DataType.INTEGER || expression1.getDataType(context) == Expression.DataType.FLOAT) &&
                (expression2.getDataType(context) == Expression.DataType.INTEGER || expression2.getDataType(context) == Expression.DataType.FLOAT)) {
            float value1;
            float value2;
            if (expression1.getDataType(context) == Expression.DataType.INTEGER) {
                value1 = expression1.getValueInteger(context);
            } else {
                value1 = expression1.getValueFloat(context);
            }
            if (expression2.getDataType(context) == Expression.DataType.INTEGER) {
                value2 = expression2.getValueInteger(context);
            } else {
                value2 = expression2.getValueFloat(context);
            }
            return comparatorCheckFloat(value1, value2, comparator);
        } else if (expression1.getDataType(context) == Expression.DataType.BOOLEAN) {
            if (comparator == ExpressionCompare.Comparator.NOT_EQUAL) {
                return expression1.getValueBoolean(context) != expression2.getValueBoolean(context);
            }
            return expression1.getValueBoolean(context) == expression2.getValueBoolean(context);
        } else if (expression1.getDataType(context) == Expression.DataType.STRING) {
            if (comparator == ExpressionCompare.Comparator.NOT_EQUAL) {
                return !expression1.getValueString(context).equals(expression2.getValueString(context));
            }
            return expression1.getValueString(context).equals(expression2.getValueString(context));
        } else if (expression1.getDataType(context) == Expression.DataType.STRING_SET) {
            if (comparator == ExpressionCompare.Comparator.NOT_EQUAL) {
                return !expression1.getValueStringSet(context).equals(expression2.getValueStringSet(context));
            }
            return expression1.getValueStringSet(context).equals(expression2.getValueStringSet(context));
        }
        return false;
    }

    private boolean comparatorCheckFloat(float value1, float value2, ExpressionCompare.Comparator comparator) {
        return switch (comparator) {
            case LESS -> (value1 < value2);
            case GREATER -> (value1 > value2);
            case LESS_EQUAL -> (value1 <= value2);
            case EQUAL -> (value1 == value2);
            case NOT_EQUAL -> (value1 != value2);
            default -> (value1 >= value2);
        };
    }

}
