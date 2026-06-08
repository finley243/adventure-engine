package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataActor;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionCarryActorEnd extends Action {

    private final Actor carriedActor;

    public ActionCarryActorEnd(Actor carriedActor) {
        this.carriedActor = carriedActor;
    }

    @Override
    public String getID() {
        return "carry_actor_end";
    }

    @Override
    public Context getContext(Game game, Actor subject) {
        return Context.builder(game).subject(subject).target(carriedActor).build();
    }

    @Override
    public void choose(Game game, int repeatActionCount, Actor subject) {
        subject.setCarriedActor(null);
        Context context = Context.builder(game).subject(subject).target(carriedActor).build();
        SensoryEvent.execute(game, new SensoryEvent(subject.getArea(), Phrases.get("putDownActor"), context, true, this, null));
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataActor(carriedActor);
    }

    @Override
    public String getPrompt(Game game, Actor subject) {
        return "Put down";
    }

}
