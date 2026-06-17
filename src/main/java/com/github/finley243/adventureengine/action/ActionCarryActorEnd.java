package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataActor;

public class ActionCarryActorEnd extends Action {

    private final Actor carriedActor;

    public ActionCarryActorEnd(Actor subject, ActionDependencies dependencies, Actor carriedActor) {
        super(subject, dependencies);
        this.carriedActor = carriedActor;
    }

    @Override
    public String getID() {
        return "carry_actor_end";
    }

    @Override
    public Context getContext() {
        return Context.builder().subject(subject).target(carriedActor).build();
    }

    @Override
    public void choose(int repeatActionCount) {
        subject.setCarriedActor(null);
        Context context = Context.builder().subject(subject).target(carriedActor).build();
        sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), "@putDownActor", context, true, this, null));
    }

    @Override
    public MenuData getMenuData() {
        return new MenuDataActor(carriedActor);
    }

    @Override
    public String getPrompt() {
        return "Put down";
    }

}
