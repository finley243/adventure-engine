package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.Lock;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionUnlockPick extends Action {

    private final Lock lock;
    private final Lock linkedLock;
    private final WorldObject object;
    private final int difficulty;

    public ActionUnlockPick(Lock lock, Lock linkedLock, WorldObject object, int difficulty) {
        super(ActionDetectionChance.LOW);
        this.lock = lock;
        this.linkedLock = linkedLock;
        this.object = object;
        this.difficulty = difficulty;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        lock.setLocked(false);
        if (linkedLock != null) {
            linkedLock.setLocked(false);
        }
        Context context = new Context(new NounMapper().put("actor", subject).put("object", object).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("pickLock"), context, this, null, subject, null));
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && subject.getSkill(Actor.Skill.LOCKPICK) >= difficulty;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Pick lock", canChoose(subject), new String[]{object.getName()});
    }

    @Override
    public ActionResponseType responseType() {
        return ActionResponseType.BREAK_LOCK;
    }

}
