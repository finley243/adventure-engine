package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ContextScript {

    private final Game game;
    private final Actor subject;
    private final Actor target;
    private final WorldObject parentObject;
    private final Item parentItem;

    public ContextScript(Game game, Actor subject, Actor target, WorldObject parentObject, Item parentItem) {
        this.game = game;
        this.subject = subject;
        this.target = target;
        this.parentObject = parentObject;
        this.parentItem = parentItem;
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

}
