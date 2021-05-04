package com.github.finley243.adventureengine.dialogue;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;

public class Choice {

    private boolean hasTriggered;

    private String linkedTopicId;
    private String prompt;
    private Condition condition;

    private boolean once;

    public Choice(String linkedTopicId, String prompt, Condition condition, boolean once) {
        this.linkedTopicId = linkedTopicId;
        this.prompt = prompt;
        this.condition = condition;
        this.once = once;
        this.hasTriggered = false;
    }

    public boolean shouldShow() {
        return (condition == null || condition.isMet(Data.getActor(Game.PLAYER_ACTOR))) && (!once || !hasTriggered);
    }
    
    public void trigger() {
    	this.hasTriggered = true;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getLinkedId() {
        return linkedTopicId;
    }

}
