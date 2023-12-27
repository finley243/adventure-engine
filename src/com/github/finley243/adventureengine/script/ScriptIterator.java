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

    // TODO - Fix for recursive functions (values will be overwritten)
    private final Deque<Expression> expressionQueue;
    private Context context;

    public ScriptIterator(Condition condition, Expression setExpression, String iteratorParameterName, Script iteratedScript) {
        super(condition);
        this.setExpression = setExpression;
        this.iteratorParameterName = iteratorParameterName;
        this.iteratedScript = iteratedScript;
        this.expressionQueue = new ArrayDeque<>();
    }

    @Override
    protected void executeSuccess(Context context, ScriptReturnTarget returnTarget) {
        this.context = context;
        expressionQueue.clear();
        Set<String> stringSet = setExpression.getValueStringSet(context);
        for (String setValue : stringSet) {
            expressionQueue.addLast(Expression.constant(setValue));
        }
        executeNextIteration();
        /*List<QueuedEvent> scriptEvents = new ArrayList<>();
        for (String currentString : stringSet) {
            Context innerContext = new Context(context);
            Expression iteratorParameter = new ExpressionConstantString(currentString);
            scriptEvents.add(new ScriptEvent(iteratedScript, new Context(innerContext, new MapBuilder<String, Expression>().put(iteratorParameterName, iteratorParameter).build())));
        }
        context.game().eventQueue().addAllToFront(scriptEvents);*/
    }

    private void executeNextIteration() {
        Expression currentExpression = expressionQueue.removeFirst();
        Context innerContext = new Context(context, new MapBuilder<String, Expression>().put(iteratorParameterName, currentExpression).build());
        iteratedScript.execute(innerContext, this);
    }

    @Override
    public void onScriptReturn(ScriptReturn scriptReturn) {
        if (scriptReturn.error() != null) {
            sendReturn(scriptReturn);
        } else if (scriptReturn.isReturn()) {
            sendReturn(scriptReturn);
        } else if (expressionQueue.isEmpty()) {
            sendReturn(new ScriptReturn(null, false, false, null));
        } else {
            executeNextIteration();
        }
    }

}
