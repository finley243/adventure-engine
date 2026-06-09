package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.DebugLogger;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.HashMap;
import java.util.Map;

public class ActionCustom extends Action {

    private final Actor actor;
    private final WorldObject object;
    private final Item item;
    private final Area area;
    private final ActionTemplate template;
    private final Map<String, Script> parameters;
    private final MenuData menuData;
    private final boolean isMove;

    public ActionCustom(Actor actor, WorldObject object, Item item, Area area, ActionTemplate template, Map<String, Script> parameters, MenuData menuData, boolean isMove) {
        this.actor = actor;
        this.object = object;
        this.item = item;
        this.area = area;
        this.template = template;
        this.parameters = parameters;
        this.menuData = menuData;
        this.isMove = isMove;
    }

    @Override
    public String getID() {
        return getTemplate().getID();
    }

    @Override
    public Context getContext(Game game, Actor subject) {
        return getContextWithParameters(game, subject);
    }

    public ActionTemplate getTemplate() {
        return template;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return menuData;
    }

    @Override
    public String getPrompt(Game game, Actor subject) {
        Map<String, String> contextVars = getParameterStrings(game, subject);
        return LangUtils.capitalize(TextGen.generateVarsOnly(getTemplate().getPrompt(), contextVars));
    }

    @Override
    public void choose(Game game, int repeatActionCount, Actor subject) {
        if (subject.isPlayer()) {
            if (area != null) {
                area.setKnown();
            }
            if (object != null) {
                object.setKnown();
            }
            if (item != null) {
                item.setKnown();
            }
            if (actor != null) {
                actor.setKnown();
            }
        }
        if (getTemplate().getScript() != null) {
            Context context = getContextWithParameters(game, subject);
            Script.ScriptReturnData actionScriptResult = getTemplate().getScript().execute(context);
            if (actionScriptResult.error() != null) {
                DebugLogger.print("Action script error: " + actionScriptResult.stackTrace());
            }
        }
    }

    @Override
    public CanChooseResult canChoose(Game game, Actor subject) {
        CanChooseResult resultSuper = super.canChoose(game, subject);
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        for (ActionTemplate.ConditionWithMessage customCondition : getTemplate().getSelectConditions()) {
            if (!customCondition.condition().isMet(getContextWithParameters(game, subject))) {
                return new CanChooseResult(false, customCondition.message());
            }
        }
        return new CanChooseResult(true, null);
    }

    @Override
    public int actionPoints(Game game, Actor subject) {
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
    public boolean canShow(Game game, Actor subject) {
        return getTemplate().getShowCondition() == null || getTemplate().getShowCondition().isMet(getContextWithParameters(game, subject));
    }

    private Context getContextWithParameters(Game game, Actor subject) {
        Context context = Context.builder(game).subject(subject).target(actor).parentObject(object).parentItem(item).parentArea(area).parentAction(this).build();
        for (Map.Entry<String, Script> templateParameter : getTemplate().getParameters().entrySet()) {
            Script.ScriptReturnData parameterResult = templateParameter.getValue().execute(context);
            if (parameterResult.error() != null) {
                DebugLogger.print("Action parameter error: " + parameterResult.stackTrace());
            } else {
                context.setLocalVariable(templateParameter.getKey(), parameterResult.value());
            }
        }
        for (Map.Entry<String, Script> instanceParameter : parameters.entrySet()) {
            Script.ScriptReturnData parameterResult = instanceParameter.getValue().execute(context);
            if (parameterResult.error() != null) {
                DebugLogger.print("Action parameter error: " + parameterResult.stackTrace());
            } else {
                context.setLocalVariable(instanceParameter.getKey(), parameterResult.value());
            }
        }
        return context;
    }

    private Map<String, String> getParameterStrings(Game game, Actor subject) {
        Map<String, String> stringMap = new HashMap<>();
        Context context = getContextWithParameters(game, subject);
        for (Map.Entry<String, Context.Variable> variable : context.getLocalVariables().entrySet()) {
            if (variable.getValue().getExpression() != null && variable.getValue().getExpression().getDataType() == Expression.DataType.STRING) {
                stringMap.put(variable.getKey(), variable.getValue().getExpression().getValueString());
            }
        }
        return stringMap;
    }

    public record CustomActionHolder(String action, Map<String, Script> parameters) {}

}
