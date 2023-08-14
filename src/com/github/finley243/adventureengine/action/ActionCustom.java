package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.ScriptEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.HashMap;
import java.util.Map;

public class ActionCustom extends Action {

    private final Game game;
    private final Actor actor;
    private final WorldObject object;
    private final Item item;
    private final Area area;
    private final String template;
    private final Map<String, Expression> parameters;
    private final String[] menuPath;
    private final boolean isMove;

    public ActionCustom(Game game, Actor actor, WorldObject object, Item item, Area area, String template, Map<String, Expression> parameters, String[] menuPath, boolean isMove) {
        this.game = game;
        this.actor = actor;
        this.object = object;
        this.item = item;
        this.area = area;
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
            Map<String, Expression> combinedParameters = new HashMap<>();
            combinedParameters.putAll(getTemplate().getParameters());
            combinedParameters.putAll(parameters);
            game.eventQueue().addToEnd(new ScriptEvent(getTemplate().getScript(), new Context(subject.game(), subject, actor, object, item, area, combinedParameters)));
        }
        subject.game().eventQueue().addToEnd(new CompleteActionEvent(subject, this, repeatActionCount));
    }

    @Override
    public CanChooseResult canChoose(Actor subject) {
        CanChooseResult resultSuper = super.canChoose(subject);
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        if (getTemplate().getConditionSelect() != null && !getTemplate().getConditionSelect().isMet(new Context(subject.game(), subject, actor, object, item, area, parameters))) {
            // TODO - Add custom condition reason text
            return new CanChooseResult(false, "CUSTOM CONDITION NOT MET");
        }
        return new CanChooseResult(true, null);
    }

    @Override
    public int actionPoints(Actor subject) {
        return getTemplate().getActionPoints();
    }

    @Override
    public int repeatCount(Actor subject) {
        if (isMove) {
            return subject.getMovePoints();
        }
        return 0;
    }

    @Override
    public boolean isRepeatMatch(Action action) {
        if (isMove) {
            return action instanceof ActionCustom actionCustom && actionCustom.isMove;
        }
        return false;
    }

    @Override
    public boolean isBlockedMatch(Action action) {
        if (isMove) {
            return action instanceof ActionCustom actionCustom && actionCustom.isMove;
        }
        return false;
    }

    @Override
    public float utility(Actor subject) {
        if (isMove) {
            return UtilityUtils.getMovementUtility(subject, area) * UtilityUtils.MOVE_UTILITY_MULTIPLIER;
        }
        return 0.0f;
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        Map<String, String> contextVars = new HashMap<>();
        for (Map.Entry<String, Expression> entry : getTemplate().getParameters().entrySet()) {
            if (entry.getValue().getDataType() == Expression.DataType.STRING) {
                contextVars.put(entry.getKey(), entry.getValue().getValueString(new Context(subject.game(), subject, actor, object, item, area, parameters)));
            }
        }
        for (Map.Entry<String, Expression> entry : parameters.entrySet()) {
            if (entry.getValue().getDataType() == Expression.DataType.STRING) {
                contextVars.put(entry.getKey(), entry.getValue().getValueString(new Context(subject.game(), subject, actor, object, item, area, parameters)));
            }
        }
        String promptWithVars = LangUtils.capitalize(TextGen.generateVarsOnly(getTemplate().getPrompt(), contextVars));
        return new MenuChoice(promptWithVars, canChoose(subject).canChoose(), menuPath, new String[]{getTemplate().getPrompt()});
    }

    @Override
    public boolean canShow(Actor subject) {
        return getTemplate().getConditionShow() == null || getTemplate().getConditionShow().isMet(new Context(subject.game(), subject, actor, object, item, area, parameters));
    }

    public record CustomActionHolder(String action, Map<String, Expression> parameters) {}

}
