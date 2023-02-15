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
    // The condition under which the action can be selected
    private final Condition condition;
    // The condition under which the action will be added to the list of possible actions
    private final Condition conditionShow;
    private final Script script;
    // Updated each time the action is retrieved
    private WorldObject object;

    public ActionCustom(String prompt, String phrase, Condition condition, Condition conditionShow, Script script) {
        super(ActionDetectionChance.LOW);
        this.prompt = prompt;
        this.phrase = phrase;
        this.condition = condition;
        this.conditionShow = conditionShow;
        this.script = script;
    }

    public void setObject(WorldObject object) {
        this.object = object;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        if (object == null) throw new UnsupportedOperationException("Object was not set in ActionCustom before execution");
        Context context = new Context(new NounMapper().put("actor", subject).put("object", object).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(phrase), context, this, null, subject, null));
        if(script != null) {
            script.execute(new ContextScript(subject.game(), subject, subject, object));
        }
    }

    @Override
    public boolean canChoose(Actor subject) {
        if (object == null) throw new UnsupportedOperationException("Object was not set in ActionCustom before execution");
        return super.canChoose(subject) && (condition == null || condition.isMet(new ContextScript(subject.game(), subject, subject, object)));
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
