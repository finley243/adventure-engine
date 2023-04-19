package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.variable.Variable;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.HashMap;
import java.util.Map;

public class ActionCustom extends Action {

    private final Game game;
    private final WorldObject object;
    private final String template;
    private final Map<String, Variable> parameters;
    private final String[] menuPath;
    private final boolean isMove;

    public ActionCustom(Game game, WorldObject object, String template, Map<String, Variable> parameters, String[] menuPath, boolean isMove) {
        this.game = game;
        this.object = object;
        this.template = template;
        this.parameters = parameters;
        this.menuPath = menuPath;
        this.isMove = isMove;
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
    public float utility(Actor subject) {
        if (isMove) {
            Area destinationArea = game.data().getArea(parameters.get("areaID").getValueString(new ContextScript(game, subject, subject)));
            return UtilityUtils.getMovementUtility(subject, destinationArea, true) * ActionMoveArea.MOVE_UTILITY_MULTIPLIER;
        }
        return 0.0f;
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
        String promptWithVars = LangUtils.capitalize(TextGen.generateVarsOnly(getTemplate().getPrompt(), contextVars));
        return new MenuChoice(promptWithVars, canChoose(subject), menuPath, new String[]{getTemplate().getPrompt()});
    }

    public boolean canShow(Actor subject) {
        return getTemplate().getConditionShow() == null || getTemplate().getConditionShow().isMet(new ContextScript(subject.game(), subject, subject, object, parameters));
    }

}
