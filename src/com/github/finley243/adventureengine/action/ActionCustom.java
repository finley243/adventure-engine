package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuChoice;
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
        if (getTemplate().getScript() != null) {
            Map<String, Variable> combinedParameters = new HashMap<>();
            combinedParameters.putAll(getTemplate().getParameters());
            combinedParameters.putAll(parameters);
            getTemplate().getScript().execute(new ContextScript(subject.game(), subject, subject, object, combinedParameters));
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
        for (Map.Entry<String, Variable> entry : getTemplate().getParameters().entrySet()) {
            if (entry.getValue().getDataType() == Variable.DataType.STRING) {
                contextVars.put(entry.getKey(), entry.getValue().getValueString(new ContextScript(subject.game(), subject, subject, object, parameters)));
            }
        }
        for (Map.Entry<String, Variable> entry : parameters.entrySet()) {
            if (entry.getValue().getDataType() == Variable.DataType.STRING) {
                contextVars.put(entry.getKey(), entry.getValue().getValueString(new ContextScript(subject.game(), subject, subject, object, parameters)));
            }
        }
        String promptWithVars = TextGen.generateVarsOnly(getTemplate().getPrompt(), contextVars);
        String[] menuPath;
        if (object != null) {
            menuPath = new String[] {object.getName()};
        } else {
            menuPath = new String[0];
        }
        return new MenuChoice(promptWithVars, canChoose(subject), menuPath, new String[]{getTemplate().getPrompt()});
    }

    public boolean canShow(Actor subject) {
        return getTemplate().getConditionShow() == null || getTemplate().getConditionShow().isMet(new ContextScript(subject.game(), subject, subject, object, parameters));
    }

}
