package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.action.ActionTemplate;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import org.w3c.dom.Element;

import java.util.*;

public class ActionLoader {

    private static final String NAME_ACTION = "action";

    private final ScriptPipeline scriptPipeline;
    private final ScriptRuntime scriptRuntime;
    private final Set<String> knownFunctions;

    public ActionLoader(ScriptPipeline scriptPipeline, ScriptRuntime scriptRuntime, Set<String> knownFunctions) {
        this.scriptPipeline = scriptPipeline;
        this.scriptRuntime = scriptRuntime;
        this.knownFunctions = knownFunctions;
    }

    public Map<String, ActionTemplate> load(Element element) {
        return LoadUtils.loadAll(element, NAME_ACTION, this::parseAction, ActionTemplate::getID);
    }

    private ActionTemplate parseAction(Element element) {
        String ID = LoadUtils.attribute(element, "id", null);
        String prompt = LoadUtils.singleTag(element, "prompt", null);
        Set<String> externalParameters = new HashSet<>();
        for (Element externalParameterElement : LoadUtils.directChildrenWithName(element, "parameterExternal")) {
            String parameterName = LoadUtils.attribute(externalParameterElement, "name", null);
            externalParameters.add(parameterName);
        }
        Map<String, Script> parameters = new HashMap<>();
        for (Element parameterElement : LoadUtils.directChildrenWithName(element, "parameter")) {
            String parameterName = LoadUtils.attribute(parameterElement, "name", null);
            Script parameterValue = LoadUtils.loadScriptExpression(parameterElement, scriptPipeline, "ActionTemplate(" + ID + ") - parameter: " + parameterName, knownFunctions);
            parameters.put(parameterName, parameterValue);
        }
        int actionPoints = LoadUtils.attributeInt(element, "actionPoints", 0);
        List<ActionTemplate.ConditionWithMessage> selectConditions = new ArrayList<>();
        int conditionNum = 1;
        for (Element conditionElement : LoadUtils.directChildrenWithName(element, "condition")) {
            Condition condition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(conditionElement, "script"), scriptPipeline, "ActionTemplate(" + ID + ") - condition " + conditionNum, scriptRuntime, knownFunctions);
            String blockMessage = LoadUtils.singleTag(conditionElement, "blockMessage", null);
            selectConditions.add(new ActionTemplate.ConditionWithMessage(condition, blockMessage));
            conditionNum += 1;
        }
        Condition showCondition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(element, "conditionShow"), scriptPipeline, "ActionTemplate(" + ID + ") - show condition", scriptRuntime, knownFunctions);
        Set<String> allParameterNames = new HashSet<>(externalParameters);
        allParameterNames.addAll(parameters.keySet());
        Script script = LoadUtils.loadScript(LoadUtils.singleChildWithName(element, "script"), scriptPipeline, "ActionTemplate(" + ID + ") - script", knownFunctions, allParameterNames);
        return new ActionTemplate(ID, prompt, externalParameters, parameters, actionPoints, selectConditions, showCondition, script);
    }

}
