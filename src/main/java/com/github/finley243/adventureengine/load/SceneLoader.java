package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.SceneChoice;
import com.github.finley243.adventureengine.scene.SceneLine;
import com.github.finley243.adventureengine.script.Script;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SceneLoader {

    private static final String NAME_SCENE = "scene";

    private static final String NAME_ID = "id";
    private static final String NAME_TYPE = "type";
    private static final String NAME_CONDITION = "condition";
    private static final String NAME_ONCE = "once";
    private static final String NAME_PRIORITY = "priority";
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
    private static final int DEFAULT_PRIORITY = 1;

    private static final boolean DEFAULT_LINE_ONCE = false;
    private static final boolean DEFAULT_LINE_EXIT = false;
    private static final Scene.SceneType DEFAULT_LINE_TYPE = Scene.SceneType.ALL;

    private final ScriptParser scriptParser;

    public SceneLoader(ScriptParser scriptParser) {
        this.scriptParser = scriptParser;
    }

    public Map<String, Scene> load(Element element) {
        return LoadUtils.loadAll(element, NAME_SCENE, this::parseScene, Scene::getID);
    }

    Scene parseScene(Element element) {
        if (element == null) return null;
        String sceneID = LoadUtils.attribute(element, NAME_ID, null);
        Scene.SceneType type = LoadUtils.attributeEnum(element, NAME_TYPE, Scene.SceneType.class, DEFAULT_TYPE);
        Condition condition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(element, NAME_CONDITION), scriptParser, "Scene(" + sceneID + ") - condition");
        boolean once = LoadUtils.attributeBool(element, NAME_ONCE, DEFAULT_ONCE);
        int priority = LoadUtils.attributeInt(element, NAME_PRIORITY, DEFAULT_PRIORITY);
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
        return new Scene(sceneID, condition, once, priority, lines, choices, type);
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
            Scene.SceneType type = LoadUtils.attributeEnum(element, NAME_LINE_TYPE, Scene.SceneType.class, DEFAULT_LINE_TYPE);
            Condition condition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(element, NAME_LINE_CONDITION), scriptParser, "Scene(" + sceneID + ") - line condition");
            Script scriptPre = LoadUtils.loadScript(LoadUtils.singleChildWithName(element, NAME_LINE_SCRIPT_PRE), scriptParser, "Scene(" + sceneID + ") - line pre-script");
            Script scriptPost = LoadUtils.loadScript(LoadUtils.singleChildWithName(element, NAME_LINE_SCRIPT_POST), scriptParser, "Scene(" + sceneID + ") - line post-script");
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
