package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScriptIterator extends Script implements ScriptReturnTarget {

    private final Expression setExpression;
    private final String iteratorParameterName;
    private final Script iteratedScript;

    public ScriptIterator(Expression setExpression, String iteratorParameterName, Script iteratedScript) {
        this.setExpression = setExpression;
        this.iteratorParameterName = iteratorParameterName;
        this.iteratedScript = iteratedScript;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
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
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturnData scriptReturnData) {
        runtimeStack.closeContext();
        if (scriptReturnData.error() != null) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturnData);
        } else if (scriptReturnData.isReturn()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturnData);
        } else if (runtimeStack.expressionQueueIsEmpty()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, null));
        } else {
            executeNextIteration(runtimeStack);
        }
    }

}
