package com.github.finley243.adventureengine.dialogue;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.HashSet;
import java.util.Set;

public class DialogueChoice {

    private final String linkedTopicId;
    private final String prompt;

    public DialogueChoice(String linkedTopicId, String prompt) {
        this.linkedTopicId = linkedTopicId;
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getLinkedId() {
        return linkedTopicId;
    }

}
