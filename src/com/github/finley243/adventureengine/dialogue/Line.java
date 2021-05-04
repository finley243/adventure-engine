package com.github.finley243.adventureengine.dialogue;

import java.util.List;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;

public class Line {
    
    private boolean hasTriggered;

    private List<String> textList;
    private Condition condition;

    private boolean once;
    private boolean exit;
    // If non-null, redirect to this topic after line is spoken
    private String redirectTopicId;

    public Line(List<String> textList, Condition condition, boolean once, boolean exit, String redirectTopicId) {
        this.textList = textList;
        this.condition = condition;
        this.once = once;
        this.exit = exit;
        this.redirectTopicId = redirectTopicId;
    }
    
    public List<String> getTextList() {
    	return textList;
    }
    
    public boolean shouldShow() {
    	return (condition == null || condition.isMet(Data.getActor(Game.PLAYER_ACTOR))) && (!once || !hasTriggered);
    }
    
    public void trigger() {
    	this.hasTriggered = true;
    	executeActions();
    }
    
    public boolean shouldExit() {
    	return exit;
    }
    
    public boolean hasRedirect() {
    	return redirectTopicId != null;
    }
    
    public String getRedirectTopicId() {
    	return redirectTopicId;
    }

    private void executeActions() {
    	// Handle line actions
    }
    
}
