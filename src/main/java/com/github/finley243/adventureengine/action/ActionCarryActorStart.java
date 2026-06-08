package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
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
    public Context getContext(Game game, Actor subject) {
        return Context.builder(game).subject(subject).target(carriedActor).build();
    }

    @Override
    public void choose(Game game, int repeatActionCount, Actor subject) {
        if (subject.isPlayer()) {
            carriedActor.setKnown();
        }
        subject.setCarriedActor(carriedActor);
        Context context = Context.builder(game).subject(subject).target(carriedActor).build();
        SensoryEvent.execute(game, new SensoryEvent(subject.getArea(), Phrases.get("pickUpActor"), context, true, this, null));
    }

    @Override
    public CanChooseResult canChoose(Game game, Actor subject) {
        CanChooseResult resultSuper = super.canChoose(game, subject);
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
    public String getPrompt(Game game, Actor subject) {
        return "Pick up";
    }

}
