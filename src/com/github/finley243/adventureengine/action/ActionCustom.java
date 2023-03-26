package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionCustom extends Action {

    private final WorldObject object;
    private final String template;

    public ActionCustom(WorldObject object, String template) {
        this.object = object;
        this.template = template;
    }

    public ActionTemplate getTemplate() {
        return object.game().data().getActionTemplate(template);
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        Context context = new Context(new MapBuilder<String, Noun>().put("actor", subject).put("object", object).build());
        if (getTemplate().canFail() && !getTemplate().getConditionSuccess().isMet(new ContextScript(subject.game(), subject, subject, object, null))) {
            subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getTemplate().getPhraseFail()), context, this, null, subject, null));
            if (getTemplate().getScriptFail() != null) {
                getTemplate().getScriptFail().execute(new ContextScript(subject.game(), subject, subject, object, null));
            }
        } else {
            subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getTemplate().getPhrase()), context, this, null, subject, null));
            if (getTemplate().getScript() != null) {
                getTemplate().getScript().execute(new ContextScript(subject.game(), subject, subject, object, null));
            }
        }
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && (getTemplate().getConditionSelect() == null || getTemplate().getConditionSelect().isMet(new ContextScript(subject.game(), subject, subject, object, null)));
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice(getTemplate().getPrompt(), canChoose(subject), new String[] {object.getName()}, new String[]{getTemplate().getPrompt()});
    }

    public boolean canShow(Actor subject) {
        return getTemplate().getConditionShow() == null || getTemplate().getConditionShow().isMet(new ContextScript(subject.game(), subject, subject, object, null));
    }

}
