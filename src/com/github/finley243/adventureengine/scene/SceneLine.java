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
    private final Script scriptPre;
    private final Script scriptPost;

    private final boolean once;
    private final boolean exit;
    // If non-null, redirect to this scene after line is played
    private final String redirectID;
    // If non-null, will only play if the previous scene matches this ID
    private final String fromSceneID;

    private SceneLine(Scene.SceneType type, List<SceneLine> subLines, String text, Condition condition, Script scriptPre, Script scriptPost, boolean once, boolean exit, String redirectID, String fromSceneID) {
        this.type = type;
        this.subLines = subLines;
        this.text = text;
        this.condition = condition;
        this.scriptPre = scriptPre;
        this.scriptPost = scriptPost;
        this.once = once;
        this.exit = exit;
        this.redirectID = redirectID;
        this.fromSceneID = fromSceneID;
        this.hasTriggered = false;
    }

    public SceneLine(Scene.SceneType type, List<SceneLine> subLines, Condition condition, Script scriptPre, Script scriptPost, boolean once, boolean exit, String redirectID, String fromSceneID) {
        this(type, subLines, null, condition, scriptPre, scriptPost, once, exit, redirectID, fromSceneID);
    }

    public SceneLine(String text, boolean once, boolean exit, String redirectID, String fromSceneID) {
        this(null, null, text, null, null, null, once, exit, redirectID, fromSceneID);
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
    
    public void setTriggered() {
    	hasTriggered = true;
    }

    public Script getScriptPre() {
        return scriptPre;
    }

    public Script getScriptPost() {
        return scriptPost;
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
