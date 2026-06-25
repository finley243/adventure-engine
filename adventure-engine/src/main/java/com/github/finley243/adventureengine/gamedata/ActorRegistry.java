package com.github.finley243.adventureengine.gamedata;

import com.github.finley243.adventureengine.actor.Actor;

import java.util.Map;

public class ActorRegistry extends Registry<Actor> {

    private final Actor player;

    public ActorRegistry(Map<String, Actor> entries, Actor player) {
        super(entries);
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        if (!entries.containsKey(player.getID())) throw new IllegalArgumentException("Player actor is not present in map");
        this.player = player;
    }

    public Actor getPlayer() {
        return player;
    }

}
