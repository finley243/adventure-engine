package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.SceneChoice;
import com.github.finley243.adventureengine.scene.SceneLine;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SceneLoader {

    private static final String NAME_SCENE = "scene";

    private static final String NAME_ID = "id";
    private static final String NAME_TYPE = "type";
    private static final String NAME_CONDITION = "condition";
    private static final String NAME_ONCE = "once";
    private static final String NAME_LINE = "line";
    private static final String NAME_CHOICE = "choice";

    private static final String NAME_LINE_ONCE = "once";
    private static final String NAME_LINE_EXIT = "exit";
    private static final String NAME_LINE_REDIRECT = "redirect";
    private static final String NAME_LINE_FROM = "from";
    private static final String NAME_LINE_TYPE = "type";
    private static final String NAME_LINE_CONDITION = "condition";
    private static final String NAME_LINE_SCRIPT_PRE = "scriptPre";
    private static final String NAME_LINE_SCRIPT_POST = "scriptPost";

    private static final String NAME_CHOICE_LINK = "link";

    private static final Scene.SceneType DEFAULT_TYPE = Scene.SceneType.ALL;
    private static final boolean DEFAULT_ONCE = false;

    private static final boolean DEFAULT_LINE_ONCE = false;
    private static final boolean DEFAULT_LINE_EXIT = false;
    private static final Scene.SceneType DEFAULT_LINE_TYPE = Scene.SceneType.ALL;

    private final ScriptPipeline scriptPipeline;
    private final ScriptRuntime scriptRuntime;
    private final Set<String> knownFunctions;

    public SceneLoader(ScriptPipeline scriptPipeline, ScriptRuntime scriptRuntime, Set<String> knownFunctions) {
        this.scriptPipeline = scriptPipeline;
        this.scriptRuntime = scriptRuntime;
        this.knownFunctions = knownFunctions;
    }

    public Map<String, Scene> load(Element element) {
        return LoadUtils.loadAll(element, NAME_SCENE, this::parseScene, Scene::getID);
    }

    Scene parseScene(Element element) {
        if (element == null) return null;
        String sceneID = LoadUtils.attribute(element, NAME_ID, null);
        Scene.SceneType type;
        try {
            type = LoadUtils.attributeEnum(element, NAME_TYPE, Scene.SceneType.class, DEFAULT_TYPE);
        } catch (IllegalArgumentException e) {
            throw new GameDataException("Scene has invalid type");
        }
        Condition condition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(element, NAME_CONDITION), scriptPipeline, "Scene(" + sceneID + ") - condition", scriptRuntime, knownFunctions);
        boolean once = LoadUtils.attributeBool(element, NAME_ONCE, DEFAULT_ONCE);
        List<Element> lineElements = LoadUtils.directChildrenWithName(element, NAME_LINE);
        List<SceneLine> lines = new ArrayList<>();
        for (Element lineElement : lineElements) {
            SceneLine line = parseSceneLine(lineElement, sceneID);
            lines.add(line);
        }
        List<Element> choiceElements = LoadUtils.directChildrenWithName(element, NAME_CHOICE);
        List<SceneChoice> choices = new ArrayList<>();
        for (Element choiceElement : choiceElements) {
            SceneChoice choice = parseSceneChoice(choiceElement);
            choices.add(choice);
        }
        return new Scene(sceneID, condition, once, lines, choices, type);
    }

    private SceneLine parseSceneLine(Element element, String sceneID) {
        boolean once = LoadUtils.attributeBool(element, NAME_LINE_ONCE, DEFAULT_LINE_ONCE);
        boolean exit = LoadUtils.attributeBool(element, NAME_LINE_EXIT, DEFAULT_LINE_EXIT);
        String redirect = LoadUtils.attribute(element, NAME_LINE_REDIRECT, null);
        String from = LoadUtils.attribute(element, NAME_LINE_FROM, null);
        if (element.getChildNodes().getLength() == 1) {
            String text = element.getTextContent().trim();
            return new SceneLine(text, once, exit, redirect, from);
        } else {
            Scene.SceneType type;
            try {
                type = LoadUtils.attributeEnum(element, NAME_LINE_TYPE, Scene.SceneType.class, DEFAULT_LINE_TYPE);
            } catch (IllegalArgumentException e) {
                throw new GameDataException("SceneLine has invalid type");
            }
            Condition condition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(element, NAME_LINE_CONDITION), scriptPipeline, "Scene(" + sceneID + ") - line condition", scriptRuntime, knownFunctions);
            Script scriptPre = LoadUtils.loadScript(LoadUtils.singleChildWithName(element, NAME_LINE_SCRIPT_PRE), scriptPipeline, "Scene(" + sceneID + ") - line pre-script", knownFunctions);
            Script scriptPost = LoadUtils.loadScript(LoadUtils.singleChildWithName(element, NAME_LINE_SCRIPT_POST), scriptPipeline, "Scene(" + sceneID + ") - line post-script", knownFunctions);
            List<SceneLine> subLines = new ArrayList<>();
            for (Element subLineElement : LoadUtils.directChildrenWithName(element, NAME_LINE)) {
                SceneLine subLine = parseSceneLine(subLineElement, sceneID);
                subLines.add(subLine);
            }
            return new SceneLine(type, subLines, condition, scriptPre, scriptPost, once, exit, redirect, from);
        }
    }

    private SceneChoice parseSceneChoice(Element element) {
        String link = LoadUtils.attribute(element, NAME_CHOICE_LINK, null);
        String prompt = element.getTextContent();
        return new SceneChoice(link, prompt);
    }

}
