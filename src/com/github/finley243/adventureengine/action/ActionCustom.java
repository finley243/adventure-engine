package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionCustom extends Action {

    private final String prompt;
    private final String phrase;
    private final String phraseFail;
    // The condition under which the action can be selected, or alternatively the condition under which the action will succeed
    private final Condition condition;
    // The condition under which the action will be added to the list of possible actions
    private final Condition conditionShow;
    private final boolean canFail;
    private final Script script;
    private final Script scriptFail;
    // Updated each time the action is retrieved
    private WorldObject object;

    public ActionCustom(boolean canFail, String prompt, String phrase, String phraseFail, Condition condition, Condition conditionShow, Script script, Script scriptFail) {
        if (canFail && condition == null) throw new IllegalArgumentException("Condition cannot be null if canFail is true");
        this.canFail = canFail;
        this.prompt = prompt;
        this.phrase = phrase;
        this.phraseFail = phraseFail;
        this.condition = condition;
        this.conditionShow = conditionShow;
        this.script = script;
        this.scriptFail = scriptFail;
    }

    public void setObject(WorldObject object) {
        this.object = object;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        if (object == null) throw new UnsupportedOperationException("Object was not set in ActionCustom before execution");
        Context context = new Context(new NounMapper().put("actor", subject).put("object", object).build());
        if (canFail && !condition.isMet(new ContextScript(subject.game(), subject, subject, object))) {
            subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(phraseFail), context, this, null, subject, null));
            if (scriptFail != null) {
                scriptFail.execute(new ContextScript(subject.game(), subject, subject, object));
            }
        } else {
            subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(phrase), context, this, null, subject, null));
            if (script != null) {
                script.execute(new ContextScript(subject.game(), subject, subject, object));
            }
        }
    }

    @Override
    public boolean canChoose(Actor subject) {
        if (object == null) throw new UnsupportedOperationException("Object was not set in ActionCustom before execution");
        return super.canChoose(subject) && (condition == null || canFail || condition.isMet(new ContextScript(subject.game(), subject, subject, object)));
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        if (object == null) throw new UnsupportedOperationException("Object was not set in ActionCustom before execution");
        return new MenuChoice(prompt, canChoose(subject), new String[] {object.getName()}, new String[]{prompt});
    }

    public boolean canShow(Actor subject) {
        if (object == null) throw new UnsupportedOperationException("Object was not set in ActionCustom before execution");
        return conditionShow == null || conditionShow.isMet(new ContextScript(subject.game(), subject, subject, object));
    }

}
