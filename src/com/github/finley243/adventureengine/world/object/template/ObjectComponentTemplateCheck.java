package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;

public class ObjectComponentTemplateCheck extends ObjectComponentTemplate {

    private final String prompt;
    private final Condition checkCondition;
    private final boolean canFail;
    private final String phraseSuccess;
    private final String phraseFailure;

    public ObjectComponentTemplateCheck(Game game, String ID, boolean startEnabled, String name, String prompt, Condition checkCondition, boolean canFail, String phraseSuccess, String phraseFailure) {
        super(game, ID, startEnabled, name);
        this.prompt = prompt;
        this.checkCondition = checkCondition;
        this.canFail = canFail;
        this.phraseSuccess = phraseSuccess;
        this.phraseFailure = phraseFailure;
    }

    public String getPrompt() {
        return prompt;
    }

    public Condition getCheckCondition() {
        return checkCondition;
    }

    public boolean canFail() {
        return canFail;
    }

    public String getPhraseSuccess() {
        return phraseSuccess;
    }

    public String getPhraseFailure() {
        return phraseFailure;
    }

}
