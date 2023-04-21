package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ActionTemplate extends GameInstanced {

    private final String prompt;
    private final Map<String, Expression> parameters;
    private final int actionPoints;
    // The condition under which the action can be selected
    private final Condition conditionSelect;
    // The condition under which the action will be added to the list of possible actions
    private final Condition conditionShow;
    private final Script script;

    public ActionTemplate(Game game, String ID, String prompt, Map<String, Expression> parameters, int actionPoints, Condition conditionSelect, Condition conditionShow, Script script) {
        super(game, ID);
        this.prompt = prompt;
        this.parameters = parameters;
        this.actionPoints = actionPoints;
        this.conditionSelect = conditionSelect;
        this.conditionShow = conditionShow;
        this.script = script;
    }

    public String getPrompt() {
        return prompt;
    }

    public Map<String, Expression> getParameters() {
        return parameters;
    }

    public int getActionPoints() {
        return actionPoints;
    }

    public Condition getConditionSelect() {
        return conditionSelect;
    }

    public Condition getConditionShow() {
        return conditionShow;
    }

    public Script getScript() {
        return script;
    }

}
