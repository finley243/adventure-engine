package com.github.finley243.adventureengine.scene;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;

import java.util.List;

public class SceneLine {
    
	private boolean hasTriggered;

    private final String text;

    private final Scene.SceneType type;
    private final List<SceneLine> subLines;
    private final Condition condition;
    private final Script script;

    private final boolean once;
    private final boolean exit;
    // If non-null, redirect to this scene after line is played
    private final String redirectID;
    // If non-null, will only play if the previous scene matches this ID
    private final String fromSceneID;

    private SceneLine(Scene.SceneType type, List<SceneLine> subLines, String text, Condition condition, Script script, boolean once, boolean exit, String redirectID, String fromSceneID) {
        this.type = type;
        this.subLines = subLines;
        this.text = text;
        this.condition = condition;
        this.script = script;
        this.once = once;
        this.exit = exit;
        this.redirectID = redirectID;
        this.fromSceneID = fromSceneID;
        this.hasTriggered = false;
    }

    public SceneLine(Scene.SceneType type, List<SceneLine> subLines, Condition condition, Script script, boolean once, boolean exit, String redirectID, String fromSceneID) {
        this(type, subLines, null, condition, script, once, exit, redirectID, fromSceneID);
    }

    public SceneLine(String text, boolean once, boolean exit, String redirectID, String fromSceneID) {
        this(null, null, text, null, null, once, exit, redirectID, fromSceneID);
    }

    public Scene.SceneType getType() {
        return type;
    }

    public List<SceneLine> getSubLines() {
        return subLines;
    }
    
    public String getText() {
    	return text;
    }
    
    public boolean shouldShow(Context context, String lastTopicID) {
    	return (condition == null || condition.isMet(context))
                && !(once && hasTriggered)
                && !(fromSceneID != null && !fromSceneID.equals(lastTopicID));
    }
    
    public void trigger(Context context) {
    	hasTriggered = true;
        if (script != null) {
            script.execute(context);
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
