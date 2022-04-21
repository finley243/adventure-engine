package com.github.finley243.adventureengine.dialogue;

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
