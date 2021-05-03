package personal.finley.adventure_engine.dialogue;

public class Choice {

    private boolean hasTriggered;

    private String linkedTopicId;
    private String prompt;
    private Condition condition;

    private boolean once;

    public Choice(String linkedTopicId, String prompt, Condition condition) {
        this.linkedTopicId = linkedTopicId;
        this.prompt = prompt;
        this.condition = condition;
        this.hasTriggered = false;
    }

    public boolean shouldShow() {
        return (condition == null || condition.isMet()) && (!once || !hasTriggered);
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
