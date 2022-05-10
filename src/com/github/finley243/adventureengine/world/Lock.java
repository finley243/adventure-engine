package com.github.finley243.adventureengine.world;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionLockHotwire;
import com.github.finley243.adventureengine.action.ActionLockKey;
import com.github.finley243.adventureengine.action.ActionLockPick;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Lock {

    private final String objectID;
    private final Set<String> keyItems;
    // If level = 0, that skill cannot be used
    private final int lockpickLevel;
    private final int hotwireLevel;

    private boolean isLocked;

    public Lock(String objectID, boolean startLocked, Set<String> keyItems, int lockpickLevel, int hotwireLevel) {
        this.objectID = objectID;
        this.isLocked = startLocked;
        this.keyItems = keyItems;
        this.lockpickLevel = lockpickLevel;
        this.hotwireLevel = hotwireLevel;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        if (isLocked) {
            WorldObject object = subject.game().data().getObject(objectID);
            if (!keyItems.isEmpty()) {
                actions.add(new ActionLockKey(this, object, keyItems));
            }
            if (lockpickLevel != 0) {
                actions.add(new ActionLockPick(this, object, lockpickLevel));
            }
            if (hotwireLevel != 0) {
                actions.add(new ActionLockHotwire(this, object, hotwireLevel));
            }
        }
        return actions;
    }

}
