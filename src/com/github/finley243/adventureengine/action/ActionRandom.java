package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.CompleteActionEvent;

public abstract class ActionRandom extends Action {

    public ActionRandom() {}

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        boolean continueAfterStart = onStart(subject, repeatActionCount);
        if (continueAfterStart) {
            if (MathUtils.randomCheck(chance(subject))) {
                onSuccess(subject, repeatActionCount);
            } else {
                onFail(subject, repeatActionCount);
            }
        }
        onEnd(subject, repeatActionCount);
        subject.onCompleteAction(new CompleteActionEvent(this, repeatActionCount));
    }

    public String getChanceTag(Actor subject) {
        return ((int) Math.ceil(chance(subject)*100)) + "%";
    }

    /** If onStart returns false, the action will not process random success (onSuccess/onFail will be skipped) */
    public boolean onStart(Actor subject, int repeatActionCount) {
        return true;
    }

    public void onEnd(Actor subject, int repeatActionCount) {}

    public abstract void onSuccess(Actor subject, int repeatActionCount);

    public abstract void onFail(Actor subject, int repeatActionCount);

    public abstract float chance(Actor subject);

}
