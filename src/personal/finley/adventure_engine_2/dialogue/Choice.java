package personal.finley.adventure_engine_2.dialogue;

import personal.finley.adventure_engine_2.Condition;

public class Choice {

    private boolean hasChosen;

    private String topicId;
    private String prompt;
    private Condition condition;

    private String altTopicId;
    private String altPrompt;

    public Choice(String topicId, String prompt, Condition condition) {
        this.topicId = topicId;
        this.prompt = prompt;
        this.condition = condition;
        this.altTopicId = null;
        this.altPrompt = null;
        this.hasChosen = false;
    }

    public Choice(String topicId, String prompt, Condition condition, String altTopicId, String altPrompt) {
        this(topicId, prompt, condition);
        this.altTopicId = altTopicId;
        this.altPrompt = altPrompt;
    }

}
