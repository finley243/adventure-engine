package com.github.finley243.adventureengine.dialogue;

import com.github.finley243.adventureengine.actor.Actor;
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

    public boolean shouldShow(Actor subject) {
        return (condition == null || condition.isMet(subject)) && (!once || !hasTriggered);
    }
    
    public void trigger() {
    	this.hasTriggered = true;
    }

    public String getPrompt() {
        if(condition == null || condition.getChoiceTag() == null) {
        	return prompt;
        } else {
        	return "[" + condition.getChoiceTag() + "] " + prompt;
        }
    }

    public String getLinkedId() {
        return linkedTopicId;
    }

}
