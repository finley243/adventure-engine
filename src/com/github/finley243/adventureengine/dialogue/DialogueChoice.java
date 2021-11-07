package com.github.finley243.adventureengine.dialogue;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.HashSet;
import java.util.Set;

public class DialogueChoice {

    private final Set<String> hasTriggered;

    private final String linkedTopicId;
    private final String prompt;
    private final Condition condition;

    private final boolean once;

    public DialogueChoice(String linkedTopicId, String prompt, Condition condition, boolean once) {
        this.linkedTopicId = linkedTopicId;
        this.prompt = prompt;
        this.condition = condition;
        this.once = once;
        this.hasTriggered = new HashSet<>();
    }

    public boolean shouldShow(Actor subject) {
        return (condition == null || condition.isMet(subject)) && (!once || !hasTriggered.contains(subject.getID()));
    }
    
    public void trigger(Actor subject) {
    	this.hasTriggered.add(subject.getID());
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
