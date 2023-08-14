package com.github.finley243.adventureengine.scene;

public class SceneChoice {

    private final String linkedTopicID;
    private final String prompt;

    public SceneChoice(String linkedTopicID, String prompt) {
        this.linkedTopicID = linkedTopicID;
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getLinkedID() {
        return linkedTopicID;
    }

}
