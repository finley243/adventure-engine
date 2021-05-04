package personal.finley.adventure_engine.dialogue;

import personal.finley.adventure_engine.Data;
import personal.finley.adventure_engine.Game;
import personal.finley.adventure_engine.condition.Condition;

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
