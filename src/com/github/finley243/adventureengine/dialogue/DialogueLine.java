package com.github.finley243.adventureengine.dialogue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;

public class DialogueLine {
    
	private final Set<String> hasTriggered;

    private final List<String> textList;
    private final Condition condition;
    private final Script script;

    private final boolean once;
    private final boolean exit;
    // If non-null, redirect to this topic after line is spoken
    private final String redirectTopicID;
    // If non-null, will only play if the previous topic matches this ID
    private final String fromTopicID;

    public DialogueLine(List<String> textList, Condition condition, Script script, boolean once, boolean exit, String redirectTopicID, String fromTopicID) {
        this.textList = textList;
        this.condition = condition;
        this.script = script;
        this.once = once;
        this.exit = exit;
        this.redirectTopicID = redirectTopicID;
        this.fromTopicID = fromTopicID;
        this.hasTriggered = new HashSet<>();
    }
    
    public List<String> getTextList() {
    	return textList;
    }
    
    public boolean shouldShow(Actor subject, String lastTopicID) {
    	return (condition == null || condition.isMet(subject))
                && (!once || !hasTriggered.contains(subject.getID()))
                && (fromTopicID == null || fromTopicID.equals(lastTopicID));
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
    	return redirectTopicID != null;
    }
    
    public String getRedirectTopicID() {
    	return redirectTopicID;
    }
    
}
