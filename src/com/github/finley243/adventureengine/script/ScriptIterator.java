package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantString;

import java.util.Map;
import java.util.Set;

public class ScriptIterator extends Script {

    private final Expression setExpression;
    private final String iteratorParameterName;
    private final Script iteratedScript;

    public ScriptIterator(Condition condition, Map<String, Expression> localParameters, Expression setExpression, String iteratorParameterName, Script iteratedScript) {
        super(condition, localParameters);
        this.setExpression = setExpression;
        this.iteratorParameterName = iteratorParameterName;
        this.iteratedScript = iteratedScript;
    }

    @Override
    protected void executeSuccess(Context context) {
        Set<String> stringSet = setExpression.getValueStringSet(context);
        for (String currentString : stringSet) {
            Expression iteratorParameter = new ExpressionConstantString(currentString);
            iteratedScript.execute(new Context(context, new MapBuilder<String, Expression>().put(iteratorParameterName, iteratorParameter).build()));
        }
    }

}
