package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionObjectVendingBuy;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplate;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateVending;

import java.util.ArrayList;
import java.util.List;

public class ObjectComponentVending extends ObjectComponent {

    private final String templateID;

    public ObjectComponentVending(String ID, WorldObject object, String templateID) {
        super(ID, object);
        this.templateID = templateID;
    }

    @Override
    public ObjectComponentTemplate getTemplate() {
        return getTemplateVending();
    }

    public ObjectComponentTemplateVending getTemplateVending() {
        return (ObjectComponentTemplateVending) getObject().game().data().getObjectComponentTemplate(templateID);
    }

    @Override
    public List<Action> getActions(Actor subject) {
        List<Action> actions = new ArrayList<>();
        for (String item : getTemplateVending().getVendingItems()) {
            actions.add(new ActionObjectVendingBuy(this, item));
        }
        return actions;
    }

}
