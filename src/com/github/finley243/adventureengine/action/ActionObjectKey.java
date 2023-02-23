package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentKey;

public class ActionObjectKey extends Action {

    private final ObjectComponentKey componentKey;

    public ActionObjectKey(ObjectComponentKey componentKey) {
        super(ActionDetectionChance.NONE);
        this.componentKey = componentKey;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        componentKey.setStateBoolean("succeeded", true);
        Context context = new Context(new NounMapper().put("actor", subject).put("object", componentKey.getObject()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(componentKey.getTemplateKey().getPhrase()), context, this, null, subject, null));
    }

    @Override
    public boolean canChoose(Actor subject) {
        if (componentKey.hasSucceeded()) {
            return false;
        }
        if (!super.canChoose(subject)) {
            return false;
        }
        for (String keyItem : componentKey.getKeyItems()) {
            if (subject.inventory().hasItem(keyItem)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        String componentName = componentKey.getTemplate().getName();
        String[] menuPath;
        if (componentName != null) {
            menuPath = new String[]{componentKey.getObject().getName(), componentName};
        } else {
            menuPath = new String[]{componentKey.getObject().getName()};
        }
        return new MenuChoice(componentKey.getTemplateKey().getPrompt(), canChoose(subject), menuPath, new String[]{componentKey.getTemplateKey().getPrompt()});
    }

}
