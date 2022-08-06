package com.github.finley243.adventureengine.scene;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;

public class SceneLine {
    
	private final Set<String> hasTriggered;

    private final List<String> textList;
    private final Condition condition;
    private final Script script;

    private final boolean once;
    private final boolean exit;
    // If non-null, redirect to this scene after line is played
    private final String redirectID;
    // If non-null, will only play if the previous scene matches this ID
    private final String fromSceneID;

    public SceneLine(List<String> textList, Condition condition, Script script, boolean once, boolean exit, String redirectID, String fromSceneID) {
        this.textList = textList;
        this.condition = condition;
        this.script = script;
        this.once = once;
        this.exit = exit;
        this.redirectID = redirectID;
        this.fromSceneID = fromSceneID;
        this.hasTriggered = new HashSet<>();
    }
    
    public List<String> getTextList() {
    	return textList;
    }
    
    public boolean shouldShow(Actor subject, String lastTopicID) {
    	return (condition == null || condition.isMet(subject))
                && (!once || !hasTriggered.contains(subject.getID()))
                && (fromSceneID == null || fromSceneID.equals(lastTopicID));
    }
    
    public void trigger(Actor subject) {
    	hasTriggered.add(subject.getID());
        if(script != null) {
            script.execute(subject);
        }
    }
    
    public boolean shouldExit() {
    	return exit;
    }
    
    public boolean hasRedirect() {
    	return redirectID != null;
    }
    
    public String getRedirectID() {
    	return redirectID;
    }
    
}
