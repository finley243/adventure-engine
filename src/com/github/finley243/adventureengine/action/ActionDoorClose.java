package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectDoor;

public class ActionDoorClose extends Action {

    private final ObjectDoor door;

    public ActionDoorClose(ObjectDoor door) {
        super(ActionDetectionChance.NONE);
        this.door = door;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        Context context = new Context(new NounMapper().put("actor", subject).put("door", door).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("doorClose"), context, this, null, subject, null));
        door.setOpen(false);
        door.getLinkedDoor().setOpen(false);
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && door.isOpen();
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Close", canChoose(subject), new String[]{door.getName()});
    }

}
