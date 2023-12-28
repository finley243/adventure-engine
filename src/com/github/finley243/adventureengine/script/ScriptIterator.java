package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.*;

public class ScriptIterator extends Script implements ScriptReturnTarget {

    private final Expression setExpression;
    private final String iteratorParameterName;
    private final Script iteratedScript;

    public ScriptIterator(Condition condition, Expression setExpression, String iteratorParameterName, Script iteratedScript) {
        super(condition);
        this.setExpression = setExpression;
        this.iteratorParameterName = iteratorParameterName;
        this.iteratedScript = iteratedScript;
    }

    @Override
    protected void executeSuccess(RuntimeStack runtimeStack) {
        Set<String> stringSet = setExpression.getValueStringSet(runtimeStack.getContext());
        List<Expression> expressions = new ArrayList<>(stringSet.size());
        for (String setValue : stringSet) {
            expressions.add(Expression.constant(setValue));
        }
        runtimeStack.addContextExpressionIterator(runtimeStack.getContext(), null, expressions);
        executeNextIteration(runtimeStack);
    }

    private void executeNextIteration(RuntimeStack runtimeStack) {
        Expression currentExpression = runtimeStack.removeQueuedExpression();
        Context innerContext = new Context(runtimeStack.getContext(), new MapBuilder<String, Expression>().put(iteratorParameterName, currentExpression).build());
        runtimeStack.addContext(innerContext, this);
        iteratedScript.execute(runtimeStack);
    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturn scriptReturn) {
        runtimeStack.closeContext();
        if (scriptReturn.error() != null) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturn);
        } else if (scriptReturn.isReturn()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturn);
        } else if (runtimeStack.expressionQueueIsEmpty()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturn(null, false, false, null));
        } else {
            executeNextIteration(runtimeStack);
        }
    }

}
