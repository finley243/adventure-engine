package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.variable.Variable;

import java.util.Map;

public class ActionTemplate extends GameInstanced {

    private final String prompt;
    private final String phrase;
    private final String phraseFail;
    private final Map<String, Variable> customNouns;
    // The condition under which the action can be selected
    private final Condition conditionSelect;
    // The condition under which the action will succeed
    private final Condition conditionSuccess;
    // The condition under which the action will be added to the list of possible actions
    private final Condition conditionShow;
    private final boolean canFail;
    private final Script script;
    private final Script scriptFail;

    public ActionTemplate(Game game, String ID, String prompt, String phrase, String phraseFail, Map<String, Variable> customNouns, Condition conditionSelect, Condition conditionSuccess, Condition conditionShow, boolean canFail, Script script, Script scriptFail) {
        super(game, ID);
        if (canFail && conditionSuccess == null) throw new IllegalArgumentException("Success condition cannot be null if canFail is true");
        this.prompt = prompt;
        this.phrase = phrase;
        this.phraseFail = phraseFail;
        this.customNouns = customNouns;
        this.conditionSelect = conditionSelect;
        this.conditionSuccess = conditionSuccess;
        this.conditionShow = conditionShow;
        this.canFail = canFail;
        this.script = script;
        this.scriptFail = scriptFail;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getPhrase() {
        return phrase;
    }

    public String getPhraseFail() {
        return phraseFail;
    }

    public Map<String, Variable> getCustomNouns() {
        return customNouns;
    }

    public Condition getConditionSelect() {
        return conditionSelect;
    }

    public Condition getConditionSuccess() {
        return conditionSuccess;
    }

    public Condition getConditionShow() {
        return conditionShow;
    }

    public boolean canFail() {
        return canFail;
    }

    public Script getScript() {
        return script;
    }

    public Script getScriptFail() {
        return scriptFail;
    }

}
