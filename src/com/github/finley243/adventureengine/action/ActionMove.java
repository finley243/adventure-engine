package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;
import com.github.finley243.adventureengine.menu.action.MenuDataMove;
import com.github.finley243.adventureengine.world.environment.Area;

public abstract class ActionMove extends Action {

    public ActionMove() {}

    @Override
    public CanChooseResult canChoose(Actor subject) {
        CanChooseResult resultSuper = super.canChoose(subject);
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        if (!subject.canMove(new Context(subject.game(), subject, subject))) {
            return new CanChooseResult(false, "You cannot move");
        }
        return new CanChooseResult(true, null);
    }

    @Override
    public ActionCategory getCategory(Actor subject) {
        return ActionCategory.MOVE;
    }

    @Override
    public int repeatCount(Actor subject) {
        return subject.getMovePoints();
    }

    @Override
    public boolean isRepeatMatch(Action action) {
        return action instanceof ActionMove;
    }

    @Override
    public boolean isBlockedMatch(Action action) {
        return action instanceof ActionMove;
    }

    public abstract Area getDestinationArea();

    @Override
    public ActionDetectionChance detectionChance() {
        return ActionDetectionChance.HIGH;
    }

}
