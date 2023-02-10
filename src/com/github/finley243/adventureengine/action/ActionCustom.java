package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.script.Script;

public class ActionCustom extends Action {

    private final String object;
    private final String prompt;
    private final String description;
    // The condition under which the action can be selected
    private final Condition condition;
    // The condition under which the action will be added to the list of possible actions
    private final Condition conditionShow;
    private final Script script;

    public ActionCustom(String prompt, String description, String object, Condition condition, Condition conditionShow, Script script) {
        super(ActionDetectionChance.LOW);
        this.prompt = prompt;
        this.description = description;
        this.object = object;
        this.condition = condition;
        this.conditionShow = conditionShow;
        this.script = script;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.game().eventBus().post(new RenderTextEvent(description));
        if(script != null) {
            script.execute(new ContextScript(subject.game(), subject, subject));
        }
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && (condition == null || condition.isMet(new ContextScript(subject.game(), subject, subject)));
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice(prompt, canChoose(subject), new String[] {subject.game().data().getObject(object).getName()}, new String[]{prompt});
    }

    public boolean canShow(Actor subject) {
        return conditionShow == null || conditionShow.isMet(new ContextScript(subject.game(), subject, subject));
    }

}
