package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentItemUse;
import com.github.finley243.adventureengine.world.object.template.ObjectComponentTemplateItemUse;

public class ActionObjectItemUse extends Action {

    private final ObjectComponentItemUse componentItemUse;

    public ActionObjectItemUse(ObjectComponentItemUse componentItemUse) {
        this.componentItemUse = componentItemUse;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        for (ObjectComponentTemplateItemUse.ItemUseData itemUseData : componentItemUse.getTemplateItemUse().getItemUseData()) {
            if (itemUseData.isConsumed) {
                subject.getInventory().removeItems(itemUseData.itemID, itemUseData.count);
            }
        }
        componentItemUse.setStateBoolean("succeeded", true);
        // TODO - Add items to MultiNoun in context
        Context context = new Context(new NounMapper().put("actor", subject).put("object", componentItemUse.getObject()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(componentItemUse.getTemplateItemUse().getPhrase()), context, this, null, subject, null));
    }

    @Override
    public boolean canChoose(Actor subject) {
        if (componentItemUse.hasSucceeded()) {
            return false;
        }
        if (!super.canChoose(subject)) {
            return false;
        }
        for (ObjectComponentTemplateItemUse.ItemUseData itemUseData : componentItemUse.getTemplateItemUse().getItemUseData()) {
            if (!subject.getInventory().hasItems(itemUseData.itemID, itemUseData.count)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        String componentName = componentItemUse.getTemplate().getName();
        String[] menuPath;
        if (componentName != null) {
            menuPath = new String[]{componentItemUse.getObject().getName(), componentName};
        } else {
            menuPath = new String[]{componentItemUse.getObject().getName()};
        }
        return new MenuChoice(componentItemUse.getTemplateItemUse().getPrompt(), canChoose(subject), menuPath, new String[]{componentItemUse.getTemplateItemUse().getPrompt()});
    }

}
