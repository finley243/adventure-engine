package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentCheck;

public class ActionObjectCheck extends Action {

    private final ObjectComponentCheck componentCheck;

    public ActionObjectCheck(ObjectComponentCheck componentCheck) {
        super(ActionDetectionChance.NONE);
        this.componentCheck = componentCheck;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        if (!componentCheck.getTemplateCheck().canFail() || componentCheck.getTemplateCheck().getCheckCondition().isMet(new ContextScript(subject.game(), subject, subject, componentCheck.getObject()))) {
            componentCheck.setStateBoolean("succeeded", true);
            Context context = new Context(new NounMapper().put("actor", subject).put("object", componentCheck.getObject()).build());
            subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(componentCheck.getTemplateCheck().getPhraseSuccess()), context, this, null, subject, null));
        } else {
            Context context = new Context(new NounMapper().put("actor", subject).put("object", componentCheck.getObject()).build());
            subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(componentCheck.getTemplateCheck().getPhraseFailure()), context, this, null, subject, null));
        }
    }

    @Override
    public boolean canChoose(Actor subject) {
        if (componentCheck.hasSucceeded()) {
            return false;
        }
        if (componentCheck.getTemplateCheck().canFail()) {
            return super.canChoose(subject);
        } else {
            return super.canChoose(subject) && componentCheck.getTemplateCheck().getCheckCondition().isMet(new ContextScript(subject.game(), subject, subject, componentCheck.getObject()));
        }
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        String componentName = componentCheck.getTemplate().getName();
        String[] menuPath;
        if (componentName != null) {
            menuPath = new String[]{componentCheck.getObject().getName(), componentName};
        } else {
            menuPath = new String[]{componentCheck.getObject().getName()};
        }
        return new MenuChoice(componentCheck.getTemplateCheck().getPrompt(), canChoose(subject), menuPath, new String[]{componentCheck.getTemplateCheck().getPrompt()});
    }

}
