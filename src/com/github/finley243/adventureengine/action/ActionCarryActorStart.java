package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataActor;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionCarryActorStart extends Action {

    private final Actor carriedActor;

    public ActionCarryActorStart(Actor carriedActor) {
        this.carriedActor = carriedActor;
    }

    @Override
    public String getID() {
        return "carry_actor_start";
    }

    @Override
    public Context getContext(Actor subject) {
        return new Context(subject.game(), subject, carriedActor);
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.setCarriedActor(carriedActor);
        Context context = new Context(subject.game(), subject, carriedActor);
        SensoryEvent.execute(subject.game(), new SensoryEvent(subject.getArea(), Phrases.get("pickUpActor"), context, true, this, null));
    }

    @Override
    public CanChooseResult canChoose(Actor subject) {
        CanChooseResult resultSuper = super.canChoose(subject);
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
    public MenuData getMenuData(Actor subject) {
        return new MenuDataActor(carriedActor);
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Pick up";
    }

}
