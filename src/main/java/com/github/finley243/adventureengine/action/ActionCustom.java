package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.HashMap;
import java.util.Map;

public class ActionCustom extends Action {

    private final ScriptRuntime scriptRuntime;

    private final Actor actor;
    private final WorldObject object;
    private final Item item;
    private final Area area;
    private final ActionTemplate template;
    private final Map<String, Script> parameters;
    private final MenuData menuData;
    private final boolean isMove;

    public ActionCustom(ScriptRuntime scriptRuntime, Actor actor, WorldObject object, Item item, Area area, ActionTemplate template, Map<String, Script> parameters, MenuData menuData, boolean isMove) {
        this.scriptRuntime = scriptRuntime;
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
    public Context getContext(Actor subject) {
        return getContextWithParameters(subject);
    }

    public ActionTemplate getTemplate() {
        return template;
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
    public void choose(Actor subject, int repeatActionCount, SensoryEventDispatcher sensoryEventDispatcher) {
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
            Context context = getContextWithParameters(subject);
            getTemplate().getScript().run(scriptRuntime, context);
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
        Context context = Context.builder().subject(subject).target(actor).parentObject(object).parentItem(item).parentArea(area).parentAction(this).build();
        for (Map.Entry<String, Script> templateParameter : getTemplate().getParameters().entrySet()) {
            Expression parameterValue = templateParameter.getValue().run(scriptRuntime, context);
            context.setLocalVariable(templateParameter.getKey(), parameterValue);
        }
        for (Map.Entry<String, Script> instanceParameter : parameters.entrySet()) {
            Expression parameterValue = instanceParameter.getValue().run(scriptRuntime, context);
            context.setLocalVariable(instanceParameter.getKey(), parameterValue);
        }
        return context;
    }

    private Map<String, String> getParameterStrings(Actor subject) {
        Map<String, String> stringMap = new HashMap<>();
        Context context = getContextWithParameters(subject);
        for (Map.Entry<String, Context.Variable> variable : context.getLocalVariables().entrySet()) {
            if (variable.getValue().getExpression() != null && variable.getValue().getExpression().getDataType() == Expression.DataType.STRING) {
                stringMap.put(variable.getKey(), variable.getValue().getExpression().getValueString());
            }
        }
        return stringMap;
    }

    public record CustomActionHolder(ActionTemplate action, Map<String, Script> parameters) {}

}
