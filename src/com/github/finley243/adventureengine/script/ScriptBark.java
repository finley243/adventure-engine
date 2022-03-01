package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.TextGen;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ScriptBark extends Script {

    // List of possible lines to be selected
    private final ActorReference actor;
    private final List<String> lines;
    private final float chance;

    public ScriptBark(Condition condition, ActorReference actor, List<String> lines, float chance) {
        super(condition);
        if(lines.isEmpty()) throw new IllegalArgumentException("ScriptBark lines cannot be empty");
        this.actor = actor;
        this.lines = lines;
        this.chance = chance;
    }

    @Override
    protected void executeSuccess(Actor subject) {
        if(ThreadLocalRandom.current().nextFloat() < chance) {
            String selectedLine = lines.get(ThreadLocalRandom.current().nextInt(lines.size()));
            Game.EVENT_BUS.post(new RenderTextEvent(TextGen.generate(selectedLine, new Context(actor.getActor(subject)))));
        }
    }

}
