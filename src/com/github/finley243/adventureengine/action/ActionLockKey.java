package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.Lock;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.Set;

public class ActionLockKey extends Action {

    private final Lock lock;
    private final WorldObject object;
    private final Set<String> keys;

    public ActionLockKey(Lock lock, WorldObject object, Set<String> keys) {
        super(ActionDetectionChance.NONE);
        this.lock = lock;
        this.object = object;
        this.keys = keys;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        lock.setLocked(false);
        Context context = new Context(new NounMapper().put("actor", subject).put("object", object).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("unlock"), context, this, subject, null));
    }

    @Override
    public boolean canChoose(Actor subject) {
        boolean hasKey = false;
        for(String keyID : keys) {
            if(subject.inventory().hasItem(keyID)) {
                hasKey = true;
                break;
            }
        }
        return super.canChoose(subject) && hasKey;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Use key", canChoose(subject), new String[]{object.getName()});
    }

}
