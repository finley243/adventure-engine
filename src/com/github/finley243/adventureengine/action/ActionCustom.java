package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionCustom extends Action {

    private WorldObject object;
    private final String prompt;
    private final String description;
    private final Condition condition;
    private final Script script;

    public ActionCustom(String prompt, String description, Condition condition, Script script) {
        this.prompt = prompt;
        this.description = description;
        this.condition = condition;
        this.script = script;
    }

    public void setObject(WorldObject object) {
        //TODO - Find a better way to get object (ideally keeping it final)
        this.object = object;
    }

    @Override
    public void choose(Actor subject) {
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
        return new MenuData(prompt, canChoose(subject), new String[] {object.getName()});
    }

}
