package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.variable.Variable;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.HashMap;
import java.util.Map;

public class ContextScript {

    private final Game game;
    private final Actor subject;
    private final Actor target;
    private final WorldObject parentObject;
    private final Item parentItem;
    private final Map<String, Variable> parameters;

    public ContextScript(Game game, Actor subject, Actor target) {
        this(game, subject, target, null, null, new HashMap<>());
    }

    public ContextScript(Game game, Actor subject, Actor target, WorldObject parentObject) {
        this(game, subject, target, parentObject, null, new HashMap<>());
    }

    public ContextScript(Game game, Actor subject, Actor target, WorldObject parentObject, Map<String, Variable> parameters) {
        this(game, subject, target, parentObject, null, parameters);
    }

    public ContextScript(Game game, Actor subject, Actor target, Item parentItem) {
        this(game, subject, target, null, parentItem, new HashMap<>());
    }

    public ContextScript(Game game, Actor subject, Actor target, WorldObject parentObject, Item parentItem, Map<String, Variable> parameters) {
        this.game = game;
        this.subject = subject;
        this.target = target;
        this.parentObject = parentObject;
        this.parentItem = parentItem;
        this.parameters = parameters;
    }

    public ContextScript(ContextScript context, Map<String, Variable> addedParameters) {
        this.game = context.game;
        this.subject = context.subject;
        this.target = context.target;
        this.parentObject = context.parentObject;
        this.parentItem = context.parentItem;
        this.parameters = new HashMap<>();
        this.parameters.putAll(context.parameters);
        this.parameters.putAll(addedParameters);
    }

    public Game game() {
        return game;
    }

    public Actor getSubject() {
        return subject;
    }

    public Actor getTarget() {
        return target;
    }

    public WorldObject getParentObject() {
        return parentObject;
    }

    public Item getParentItem() {
        return parentItem;
    }

    public Map<String, Variable> getParameters() {
        return parameters;
    }

}
