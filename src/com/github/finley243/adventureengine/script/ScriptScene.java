package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.scene.SceneManager;

import java.util.List;

public class ScriptScene extends Script {

    private final List<String> scenes;

    public ScriptScene(Condition condition, List<String> scenes) {
        super(condition);
        this.scenes = scenes;
    }

    @Override
    public void executeSuccess(Actor subject) {
        SceneManager.trigger(subject.game(), scenes);
    }

}
