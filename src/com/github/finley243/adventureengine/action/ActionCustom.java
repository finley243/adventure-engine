package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.script.Script;

public class ActionCustom extends Action {

    private final String object;
    private final String prompt;
    private final String description;
    private final Condition condition;
    private final Script script;

    public ActionCustom(String prompt, String description, String object, Condition condition, Script script) {
        super(ActionDetectionChance.LOW);
        this.prompt = prompt;
        this.description = description;
        this.object = object;
        this.condition = condition;
        this.script = script;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.game().eventBus().post(new RenderTextEvent(description));
        if(script != null) {
            script.execute(subject);
        }
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && (condition == null || condition.isMet(subject));
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData(prompt, canChoose(subject), new String[] {subject.game().data().getObject(object).getName()});
    }

}
