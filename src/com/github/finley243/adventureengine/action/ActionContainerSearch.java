package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectContainer;

public class ActionContainerSearch extends Action {

    private final ObjectContainer container;

    public ActionContainerSearch(ObjectContainer container) {
        super(ActionDetectionChance.LOW);
        this.container = container;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        container.search();
        Context context = new Context(new NounMapper().put("actor", subject).put("container", container).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("searchContainer"), context, this, subject, null));
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && !container.isLocked();
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Search", canChoose(subject), new String[]{container.getName()});
    }

}
