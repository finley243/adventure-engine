package com.github.finley243.adventureengine.load;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.SceneLine;
import com.github.finley243.adventureengine.script.Script;

public class SceneLoader {
	
	public static Scene loadScene(Element sceneElement) throws ParserConfigurationException, SAXException, IOException {
		boolean isRepeatable = LoadUtils.boolAttribute(sceneElement, "isRepeatable", true);
		boolean playImmediately = LoadUtils.boolAttribute(sceneElement, "playImmediately", false);
		String sceneID = sceneElement.getAttribute("id");
		Element conditionElement = (Element) sceneElement.getElementsByTagName("condition").item(0);
		Condition condition = DialogueLoader.loadCondition(conditionElement);
		NodeList lineElements = sceneElement.getElementsByTagName("line");
		List<SceneLine> lines = new ArrayList<>();
		for(int i = 0; i < lineElements.getLength(); i++) {
			if(lineElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element lineElement = (Element) lineElements.item(i);
				SceneLine line = loadSceneLine(lineElement);
				lines.add(line);
			}
		}
		float chance = LoadUtils.singleTagFloat(sceneElement, "chance", 1.0f);
		int cooldown = LoadUtils.singleTagInt(sceneElement, "cooldown", 0);
		return new Scene(sceneID, condition, lines, isRepeatable, playImmediately, chance, cooldown);
	}

	private static SceneLine loadSceneLine(Element lineElement) throws ParserConfigurationException, SAXException, IOException {
		Element conditionElement = (Element) lineElement.getElementsByTagName("condition").item(0);
		Condition condition = DialogueLoader.loadCondition(conditionElement);
		List<String> text = LoadUtils.listOfTags(lineElement, "text");
		List<Script> scripts = DialogueLoader.loadScripts(lineElement);
		return new SceneLine(condition, text, scripts);
	}

}
