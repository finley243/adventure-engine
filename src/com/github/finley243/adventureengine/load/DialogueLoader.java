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
import com.github.finley243.adventureengine.actor.Actor.Attribute;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.condition.ConditionAttribute;
import com.github.finley243.adventureengine.condition.ConditionCompound;
import com.github.finley243.adventureengine.condition.ConditionKnowledge;
import com.github.finley243.adventureengine.condition.ConditionMoney;
import com.github.finley243.adventureengine.dialogue.Choice;
import com.github.finley243.adventureengine.dialogue.Line;
import com.github.finley243.adventureengine.dialogue.Topic;
import com.github.finley243.adventureengine.dialogue.Topic.TopicType;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptAddItem;
import com.github.finley243.adventureengine.script.ScriptKnowledge;
import com.github.finley243.adventureengine.script.ScriptMoney;
import com.github.finley243.adventureengine.script.ScriptTrade;

public class DialogueLoader {

	public static void loadDialogue(File dir) throws ParserConfigurationException, SAXException, IOException {
		if(dir.isDirectory()) {
			File[] files = dir.listFiles();
			for(File file : files) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(file);
				Element rootElement = document.getDocumentElement();
				NodeList topics = rootElement.getElementsByTagName("topic");
				for(int i = 0; i < topics.getLength(); i++) {
					if(topics.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Element topicElement = (Element) topics.item(i);
						Topic topic = loadTopic(topicElement);
						Data.addTopic(topic.getID(), topic);
					}
				}
			}
		}
	}
	
	private static Topic loadTopic(Element topicElement) throws ParserConfigurationException, SAXException, IOException {
		String topicID = topicElement.getAttribute("id");
		TopicType type;
		switch(topicElement.getAttribute("type")) {
			case "sel":
				type = TopicType.SELECTOR;
				break;
			case "seq":
			default:
				type = TopicType.SEQUENTIAL;
				break;
		}
		NodeList lineElements = topicElement.getElementsByTagName("line");
		List<Line> lines = new ArrayList<Line>();
		for(int i = 0; i < lineElements.getLength(); i++) {
			if(lineElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element lineElement = (Element) lineElements.item(i);
				Line line = loadLine(lineElement);
				lines.add(line);
			}
		}
		NodeList choiceElements = topicElement.getElementsByTagName("choice");
		List<Choice> choices = new ArrayList<Choice>();
		for(int i = 0; i < choiceElements.getLength(); i++) {
			if(choiceElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element choiceElement = (Element) choiceElements.item(i);
				Choice choice = loadChoice(choiceElement);
				choices.add(choice);
			}
		}
		return new Topic(topicID, lines, choices, type);
	}
	
	private static Line loadLine(Element lineElement) throws ParserConfigurationException, SAXException, IOException {
		boolean once = LoadUtils.boolAttribute(lineElement, "once", false);
		boolean exit = LoadUtils.boolAttribute(lineElement, "exit", false);
		String redirect = lineElement.getAttribute("redirect");
		if(redirect.isEmpty()) {
			redirect = null;
		}
		List<String> texts = LoadUtils.listOfTags(lineElement, "text");
		Element conditionElement = (Element) lineElement.getElementsByTagName("condition").item(0);
		Condition condition = loadCondition(conditionElement);
		List<Script> scripts = loadScripts(lineElement);
		return new Line(texts, condition, scripts, once, exit, redirect);
	}
	
	private static Choice loadChoice(Element choiceElement) throws ParserConfigurationException, SAXException, IOException {
		boolean once = LoadUtils.boolAttribute(choiceElement, "once", false);
		String link = choiceElement.getAttribute("link");
		String prompt = LoadUtils.singleTag(choiceElement, "prompt", null);
		Element conditionElement = (Element) choiceElement.getElementsByTagName("condition").item(0);
		Condition condition = loadCondition(conditionElement);
		return new Choice(link, prompt, condition, once);
	}
	
	private static Condition loadCondition(Element conditionElement) throws ParserConfigurationException, SAXException, IOException {
		if(conditionElement == null) return null;
		String type = conditionElement.getAttribute("type");
		switch(type) {
		case "compound":
			List<Condition> subConditions = loadSubConditions(conditionElement);
			boolean useOr = conditionElement.getAttribute("logic").equalsIgnoreCase("or");
			return new ConditionCompound(subConditions, useOr);
		case "money":
			int moneyAmount = LoadUtils.singleTagInt(conditionElement, "value", 0);
			return new ConditionMoney(moneyAmount);
		case "knowledge":
			String knowledgeID = LoadUtils.singleTag(conditionElement, "knowledge", null);
			boolean knowledgeValue = LoadUtils.singleTagBoolean(conditionElement, "value", true);
			return new ConditionKnowledge(knowledgeID, knowledgeValue);
		case "attribute":
			Attribute attribute = Attribute.valueOf(LoadUtils.singleTag(conditionElement, "attribute", null).toUpperCase());
			int attributeValue = LoadUtils.singleTagInt(conditionElement, "value", 0);
			return new ConditionAttribute(attribute, attributeValue);
		default:
			return null;
		}
	}
	
	private static List<Condition> loadSubConditions(Element conditionElement) throws ParserConfigurationException, SAXException, IOException {
		NodeList subConditionElements = conditionElement.getElementsByTagName("condition");
		List<Condition> subConditions = new ArrayList<Condition>();
		for(int i = 0; i < subConditionElements.getLength(); i++) {
			if(subConditionElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element subConditionElement = (Element) subConditionElements.item(i);
				Condition subCondition = loadCondition(subConditionElement);
				subConditions.add(subCondition);
			}
		}
		return subConditions;
	}
	
	private static List<Script> loadScripts(Element parentElement) {
		NodeList scriptElements = parentElement.getElementsByTagName("script");
		List<Script> scripts = new ArrayList<Script>();
		for(int i = 0; i < scriptElements.getLength(); i++) {
			if(scriptElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element scriptElement = (Element) scriptElements.item(i);
				Script script = loadScript(scriptElement);
				scripts.add(script);
			}
		}
		return scripts;
	}
	
	private static Script loadScript(Element scriptElement) {
		if(scriptElement == null) return null;
		String type = scriptElement.getAttribute("type");
		switch(type) {
		case "money":
			int moneyValue = LoadUtils.singleTagInt(scriptElement, "value", 0);
			return new ScriptMoney(moneyValue);
		case "add_item":
			String addItemID = LoadUtils.singleTag(scriptElement, "item", null);
			return new ScriptAddItem(addItemID);
		case "knowledge":
			String knowledgeID = LoadUtils.singleTag(scriptElement, "knowledge", null);
			return new ScriptKnowledge(knowledgeID);
		case "trade":
			return new ScriptTrade();
		default:
			return null;
		}
	}
	
}
