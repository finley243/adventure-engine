package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
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
    public Context getContext(Actor subject) {
        return Context.builder(game).subject(subject).target(carriedActor).build();
    }

    @Override
    public void choose(Actor subject, int repeatActionCount, SensoryEventDispatcher sensoryEventDispatcher) {
        subject.setCarriedActor(null);
        Context context = Context.builder(game).subject(subject).target(carriedActor).build();
        SensoryEvent.execute(game, new SensoryEvent(subject.getArea(), Phrases.get("putDownActor"), context, true, this, null));
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataActor(carriedActor);
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Put down";
    }

}
