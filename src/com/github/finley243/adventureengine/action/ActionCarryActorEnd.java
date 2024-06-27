package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
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
    public void choose(Actor subject, int repeatActionCount) {
        subject.setCarriedActor(null);
        Context context = new Context(subject.game(), subject, carriedActor);
        SensoryEvent.execute(subject.game(), new SensoryEvent(subject.getArea(), Phrases.get("putDownActor"), context, true, this, null));
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
