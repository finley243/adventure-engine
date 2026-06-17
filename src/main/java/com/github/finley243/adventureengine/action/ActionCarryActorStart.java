package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataActor;

public class ActionCarryActorStart extends Action {

    private final Actor carriedActor;

    public ActionCarryActorStart(Actor subject, ActionDependencies dependencies, Actor carriedActor) {
        super(subject, dependencies);
        this.carriedActor = carriedActor;
    }

    @Override
    public String getID() {
        return "carry_actor_start";
    }

    @Override
    public Context getContext() {
        return Context.builder().subject(subject).target(carriedActor).build();
    }

    @Override
    public void choose(int repeatActionCount) {
        if (subject.isPlayer()) {
            carriedActor.setKnown();
        }
        subject.setCarriedActor(carriedActor);
        Context context = Context.builder().subject(subject).target(carriedActor).build();
        sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), "@pickUpActor", context, true, this, null));
    }

    @Override
    public CanChooseResult canChoose() {
        CanChooseResult resultSuper = super.canChoose();
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        if (subject.isUsingObject()) {
            return new CanChooseResult(false, "Cannot carry while using object");
        }
        if (subject.isCarryingActor()) {
            return new CanChooseResult(false, "Already carrying someone");
        }
        if (!carriedActor.canBeCarried()) {
            return new CanChooseResult(false, "Cannot be carried");
        }
        return new CanChooseResult(true, null);
    }

    @Override
    public MenuData getMenuData() {
        return new MenuDataActor(carriedActor);
    }

    @Override
    public String getPrompt() {
        return "Pick up";
    }

}
