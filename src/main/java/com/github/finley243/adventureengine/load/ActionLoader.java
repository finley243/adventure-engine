package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.action.ActionTemplate;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionLoader {

    private static final String NAME_ACTION = "action";

    private final ScriptParser scriptParser;

    public ActionLoader(ScriptParser scriptParser) {
        this.scriptParser = scriptParser;
    }

    public Map<String, ActionTemplate> load(Element element) {
        return LoadUtils.loadAll(element, NAME_ACTION, this::parseAction, ActionTemplate::getID);
    }

    private ActionTemplate parseAction(Element element) {
        String ID = LoadUtils.attribute(element, "id", null);
        String prompt = LoadUtils.singleTag(element, "prompt", null);
        Map<String, Script> parameters = new HashMap<>();
        for (Element parameterElement : LoadUtils.directChildrenWithName(element, "parameter")) {
            String parameterName = LoadUtils.attribute(parameterElement, "name", null);
            Script parameterValue = LoadUtils.loadScriptExpression(parameterElement, scriptParser, "ActionTemplate(" + ID + ") - parameter: " + parameterName);
            parameters.put(parameterName, parameterValue);
        }
        int actionPoints = LoadUtils.attributeInt(element, "actionPoints", 0);
        List<ActionTemplate.ConditionWithMessage> selectConditions = new ArrayList<>();
        int conditionNum = 1;
        for (Element conditionElement : LoadUtils.directChildrenWithName(element, "condition")) {
            Condition condition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(conditionElement, "script"), scriptParser, "ActionTemplate(" + ID + ") - condition " + conditionNum);
            String blockMessage = LoadUtils.singleTag(conditionElement, "blockMessage", null);
            selectConditions.add(new ActionTemplate.ConditionWithMessage(condition, blockMessage));
            conditionNum += 1;
        }
        Condition showCondition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(element, "conditionShow"), scriptParser, "ActionTemplate(" + ID + ") - show condition");
        Script script = LoadUtils.loadScript(LoadUtils.singleChildWithName(element, "script"), scriptParser, "ActionTemplate(" + ID + ") - script");
        return new ActionTemplate(ID, prompt, parameters, actionPoints, selectConditions, showCondition, script);
    }

}
