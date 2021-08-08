package com.github.finley243.adventureengine.dialogue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;

public class Line {
    
	private Set<String> hasTriggered;

    private List<String> textList;
    private Condition condition;
    private List<Script> scripts;

    private boolean once;
    private boolean exit;
    // If non-null, redirect to this topic after line is spoken
    private String redirectTopicId;

    public Line(List<String> textList, Condition condition, List<Script> scripts, boolean once, boolean exit, String redirectTopicId) {
        this.textList = textList;
        this.condition = condition;
        this.scripts = scripts;
        this.once = once;
        this.exit = exit;
        this.redirectTopicId = redirectTopicId;
        this.hasTriggered = new HashSet<String>();
    }
    
    public List<String> getTextList() {
    	return textList;
    }
    
    public boolean shouldShow(Actor subject, Actor target) {
    	return (condition == null || condition.isMet(subject)) && (!once || !hasTriggered.contains(target.getID()));
    }
    
    public void trigger(Actor subject, Actor target) {
    	hasTriggered.add(target.getID());
    	executeScripts(subject, target);
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

    private void executeScripts(Actor subject, Actor target) {
    	for(Script script : scripts) {
    		script.execute(subject, target);
    	}
    }
    
}
