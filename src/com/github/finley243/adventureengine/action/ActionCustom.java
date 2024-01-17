package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
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

    private final Game game;
    private final Actor actor;
    private final WorldObject object;
    private final Item item;
    private final Area area;
    private final String template;
    private final Map<String, Script> parameters;
    private final MenuData menuData;
    private final boolean isMove;

    public ActionCustom(Game game, Actor actor, WorldObject object, Item item, Area area, String template, Map<String, Script> parameters, MenuData menuData, boolean isMove) {
        this.game = game;
        this.actor = actor;
        this.object = object;
        this.item = item;
        this.area = area;
        this.template = template;
        this.parameters = parameters;
        this.menuData = menuData;
        this.isMove = isMove;
    }

    public Game game() {
        return game;
    }

    public ActionTemplate getTemplate() {
        return game.data().getActionTemplate(template);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return menuData;
    }

    @Override
    public String getPrompt(Actor subject) {
        Map<String, String> contextVars = getParameterStrings(subject);
        return LangUtils.capitalize(TextGen.generateVarsOnly(getTemplate().getPrompt(), contextVars));
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        if (getTemplate().getScript() != null) {
            Context context = new Context(subject.game(), subject, actor, object, item, area, this, new HashMap<>());
            for (Map.Entry<String, Script> templateParameter : getTemplate().getParameters().entrySet()) {
                Script.ScriptReturnData parameterResult = templateParameter.getValue().execute(context);
                if (parameterResult.error() != null) {
                    game.log().print("Action parameter error: " + parameterResult.error());
                } else {
                    context.setLocalVariable(templateParameter.getKey(), parameterResult.value());
                }
            }
            for (Map.Entry<String, Script> instanceParameter : parameters.entrySet()) {
                Script.ScriptReturnData parameterResult = instanceParameter.getValue().execute(context);
                if (parameterResult.error() != null) {
                    game.log().print("Action parameter error: " + parameterResult.error());
                } else {
                    context.setLocalVariable(instanceParameter.getKey(), parameterResult.value());
                }
            }
            getTemplate().getScript().execute(context);
        }
    }

    @Override
    public CanChooseResult canChoose(Actor subject) {
        CanChooseResult resultSuper = super.canChoose(subject);
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        for (ActionTemplate.ConditionWithMessage customCondition : getTemplate().getSelectConditions()) {
            if (!customCondition.condition().isMet(getContextWithParameters(subject))) {
                return new CanChooseResult(false, customCondition.message());
            }
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
    public boolean canShow(Actor subject) {
        return getTemplate().getShowCondition() == null || getTemplate().getShowCondition().isMet(getContextWithParameters(subject));
    }

    private Context getContextWithParameters(Actor subject) {
        Context context = new Context(subject.game(), subject, actor, object, item, area, this, new HashMap<>());
        for (Map.Entry<String, Script> templateParameter : getTemplate().getParameters().entrySet()) {
            Script.ScriptReturnData parameterResult = templateParameter.getValue().execute(context);
            if (parameterResult.error() != null) {
                game.log().print("Action parameter error: " + parameterResult.error());
            } else {
                context.setLocalVariable(templateParameter.getKey(), parameterResult.value());
            }
        }
        for (Map.Entry<String, Script> instanceParameter : parameters.entrySet()) {
            Script.ScriptReturnData parameterResult = instanceParameter.getValue().execute(context);
            if (parameterResult.error() != null) {
                game.log().print("Action parameter error: " + parameterResult.error());
            } else {
                context.setLocalVariable(instanceParameter.getKey(), parameterResult.value());
            }
        }
        return context;
    }

    private Map<String, String> getParameterStrings(Actor subject) {
        Map<String, String> stringMap = new HashMap<>();
        Context context = new Context(subject.game(), subject, actor, object, item, area, this, new HashMap<>());
        for (Map.Entry<String, Script> templateParameter : getTemplate().getParameters().entrySet()) {
            Script.ScriptReturnData parameterResult = templateParameter.getValue().execute(context);
            if (parameterResult.error() != null) {
                game.log().print("Action parameter error: " + parameterResult.error());
                break;
            }
            Expression parameterValue = parameterResult.value();
            if (parameterValue != null && parameterValue.getDataType() == Expression.DataType.STRING) {
                stringMap.put(templateParameter.getKey(), parameterValue.getValueString());
            }
        }
        for (Map.Entry<String, Script> instanceParameter : parameters.entrySet()) {
            Script.ScriptReturnData parameterResult = instanceParameter.getValue().execute(context);
            if (parameterResult.error() != null) {
                game.log().print("Action parameter error: " + parameterResult.error());
                break;
            }
            Expression parameterValue = parameterResult.value();
            if (parameterValue != null && parameterValue.getDataType() == Expression.DataType.STRING) {
                stringMap.put(instanceParameter.getKey(), parameterValue.getValueString());
            }
        }
        return stringMap;
    }

    public record CustomActionHolder(String action, Map<String, Script> parameters) {}

}
