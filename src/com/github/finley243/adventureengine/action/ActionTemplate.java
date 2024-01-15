package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.Script;

import java.util.List;
import java.util.Map;

public class ActionTemplate extends GameInstanced {

    private final String prompt;
    private final Map<String, Script> parameters;
    private final int actionPoints;
    // The conditions under which the action can be selected
    private final List<ConditionWithMessage> selectConditions;
    // The condition under which the action will be added to the list of possible actions
    private final Condition showCondition;
    private final Script script;

    public ActionTemplate(Game game, String ID, String prompt, Map<String, Script> parameters, int actionPoints, List<ConditionWithMessage> selectConditions, Condition showCondition, Script script) {
        super(game, ID);
        this.prompt = prompt;
        this.parameters = parameters;
        this.actionPoints = actionPoints;
        this.selectConditions = selectConditions;
        this.showCondition = showCondition;
        this.script = script;
    }

    public String getPrompt() {
        return prompt;
    }

    public Map<String, Script> getParameters() {
        return parameters;
    }

    public int getActionPoints() {
        return actionPoints;
    }

    public List<ConditionWithMessage> getSelectConditions() {
        return selectConditions;
    }

    public Condition getShowCondition() {
        return showCondition;
    }

    public Script getScript() {
        return script;
    }

    public record ConditionWithMessage(Condition condition, String message) {}

}
