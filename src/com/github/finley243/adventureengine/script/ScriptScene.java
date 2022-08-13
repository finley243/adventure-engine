package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.scene.SceneManager;

import java.util.List;

public class ScriptScene extends Script {

    private final ActorReference actor;
    private final List<String> scenes;

    public ScriptScene(Condition condition, ActorReference actor, List<String> scenes) {
        super(condition);
        this.actor = actor;
        this.scenes = scenes;
    }

    @Override
    public void executeSuccess(Actor subject, Actor target) {
        SceneManager.triggerFromIDs(subject.game(), actor.getActor(subject, target), actor.getActor(subject, target), scenes);
    }

}
