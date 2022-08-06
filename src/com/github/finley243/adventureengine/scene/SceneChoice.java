package com.github.finley243.adventureengine.scene;

public class SceneChoice {

    private final String linkedTopicId;
    private final String prompt;

    public SceneChoice(String linkedTopicId, String prompt) {
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
