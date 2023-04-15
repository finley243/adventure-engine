package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.variable.Variable;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.HashMap;
import java.util.Map;

public class ActionCustom extends Action {

    private final Game game;
    private final WorldObject object;
    private final String template;
    private final Map<String, Variable> parameters;

    public ActionCustom(Game game, WorldObject object, String template, Map<String, Variable> parameters) {
        this.game = game;
        this.object = object;
        this.template = template;
        this.parameters = parameters;
    }

    public Game game() {
        return game;
    }

    public ActionTemplate getTemplate() {
        return game.data().getActionTemplate(template);
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        Map<String, String> contextVars = new HashMap<>();
        for (Map.Entry<String, Variable> entry : getTemplate().getTextVars().entrySet()) {
            contextVars.put(entry.getKey(), entry.getValue().getValueString(new ContextScript(subject.game(), subject, subject, object, parameters)));
        }
        Context context = new Context(contextVars, getContextNounMap(subject).build());
        if (getTemplate().canFail() && !getTemplate().getConditionSuccess().isMet(new ContextScript(subject.game(), subject, subject, object, parameters))) {
            onFailure(subject, context);
        } else {
            onSuccess(subject, context);
        }
    }

    protected MapBuilder<String, Noun> getContextNounMap(Actor subject) {
        MapBuilder<String, Noun> nounMap = new MapBuilder<String, Noun>().put("actor", subject);
        if (object != null) {
            nounMap.put("object", object);
        }
        for (Map.Entry<String, Variable> entry : getTemplate().getCustomNouns().entrySet()) {
            nounMap.put(entry.getKey(), entry.getValue().getValueNoun(new ContextScript(subject.game(), subject, subject, object, parameters)));
        }
        return nounMap;
    }

    public void onSuccess(Actor subject, Context context) {
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getTemplate().getPhrase()), context, this, null, subject, null));
        if (getTemplate().getScript() != null) {
            getTemplate().getScript().execute(new ContextScript(subject.game(), subject, subject, object, parameters));
        }
    }

    public void onFailure(Actor subject, Context context) {
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getTemplate().getPhraseFail()), context, this, null, subject, null));
        if (getTemplate().getScriptFail() != null) {
            getTemplate().getScriptFail().execute(new ContextScript(subject.game(), subject, subject, object, parameters));
        }
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && (getTemplate().getConditionSelect() == null || getTemplate().getConditionSelect().isMet(new ContextScript(subject.game(), subject, subject, object, parameters)));
    }

    @Override
    public int actionPoints(Actor subject) {
        return getTemplate().getActionPoints();
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        Map<String, String> contextVars = new HashMap<>();
        for (Map.Entry<String, Variable> entry : getTemplate().getTextVars().entrySet()) {
            contextVars.put(entry.getKey(), entry.getValue().getValueString(new ContextScript(subject.game(), subject, subject, object, parameters)));
        }
        String promptWithVars = TextGen.generateVarsOnly(getTemplate().getPrompt(), contextVars);
        return new MenuChoice(promptWithVars, canChoose(subject), new String[] {object.getName()}, new String[]{getTemplate().getPrompt()});
    }

    public boolean canShow(Actor subject) {
        return getTemplate().getConditionShow() == null || getTemplate().getConditionShow().isMet(new ContextScript(subject.game(), subject, subject, object, parameters));
    }

}
