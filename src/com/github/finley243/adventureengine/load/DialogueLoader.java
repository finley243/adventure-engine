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
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.actor.ActorReference.ReferenceType;
import com.github.finley243.adventureengine.actor.Faction.FactionRelation;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.condition.ConditionActorAvailableForScene;
import com.github.finley243.adventureengine.condition.ConditionActorDead;
import com.github.finley243.adventureengine.condition.ConditionActorLocation;
import com.github.finley243.adventureengine.condition.ConditionAttribute;
import com.github.finley243.adventureengine.condition.ConditionCompound;
import com.github.finley243.adventureengine.condition.ConditionKnowledge;
import com.github.finley243.adventureengine.condition.ConditionMoney;
import com.github.finley243.adventureengine.dialogue.DialogueChoice;
import com.github.finley243.adventureengine.dialogue.DialogueLine;
import com.github.finley243.adventureengine.dialogue.DialogueTopic;
import com.github.finley243.adventureengine.dialogue.DialogueTopic.TopicType;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptAddItem;
import com.github.finley243.adventureengine.script.ScriptDialogue;
import com.github.finley243.adventureengine.script.ScriptFactionRelation;
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
						DialogueTopic topic = loadTopic(topicElement);
						Data.addTopic(topic.getID(), topic);
					}
				}
			}
		}
	}
	
	private static DialogueTopic loadTopic(Element topicElement) throws ParserConfigurationException, SAXException, IOException {
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
		List<DialogueLine> lines = new ArrayList<>();
		for(int i = 0; i < lineElements.getLength(); i++) {
			if(lineElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element lineElement = (Element) lineElements.item(i);
				DialogueLine line = loadLine(lineElement);
				lines.add(line);
			}
		}
		NodeList choiceElements = topicElement.getElementsByTagName("choice");
		List<DialogueChoice> choices = new ArrayList<>();
		for(int i = 0; i < choiceElements.getLength(); i++) {
			if(choiceElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element choiceElement = (Element) choiceElements.item(i);
				DialogueChoice choice = loadChoice(choiceElement);
				choices.add(choice);
			}
		}
		return new DialogueTopic(topicID, lines, choices, type);
	}
	
	private static DialogueLine loadLine(Element lineElement) throws ParserConfigurationException, SAXException, IOException {
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
		return new DialogueLine(texts, condition, scripts, once, exit, redirect);
	}
	
	private static DialogueChoice loadChoice(Element choiceElement) throws ParserConfigurationException, SAXException, IOException {
		boolean once = LoadUtils.boolAttribute(choiceElement, "once", false);
		String link = choiceElement.getAttribute("link");
		String prompt = LoadUtils.singleTag(choiceElement, "prompt", null);
		Element conditionElement = (Element) choiceElement.getElementsByTagName("condition").item(0);
		Condition condition = loadCondition(conditionElement);
		return new DialogueChoice(link, prompt, condition, once);
	}
	
	protected static Condition loadCondition(Element conditionElement) throws ParserConfigurationException, SAXException, IOException {
		if(conditionElement == null) return null;
		String type = conditionElement.getAttribute("type");
		ActorReference actorRef = loadActorReference(conditionElement);
		switch(type) {
		case "compound":
			List<Condition> subConditions = loadSubConditions(conditionElement);
			boolean useOr = conditionElement.getAttribute("logic").equalsIgnoreCase("or");
			return new ConditionCompound(subConditions, useOr);
		case "money":
			int moneyAmount = LoadUtils.singleTagInt(conditionElement, "value", 0);
			return new ConditionMoney(actorRef, moneyAmount);
		case "knowledge":
			String knowledgeID = LoadUtils.singleTag(conditionElement, "knowledge", null);
			boolean knowledgeValue = LoadUtils.singleTagBoolean(conditionElement, "value", true);
			return new ConditionKnowledge(knowledgeID, knowledgeValue);
		case "attribute":
			Attribute attribute = Attribute.valueOf(LoadUtils.singleTag(conditionElement, "attribute", null).toUpperCase());
			int attributeValue = LoadUtils.singleTagInt(conditionElement, "value", 0);
			return new ConditionAttribute(actorRef, attribute, attributeValue);
		case "actorLocation":
			String actorArea = LoadUtils.singleTag(conditionElement, "area", null);
			String actorRoom = LoadUtils.singleTag(conditionElement, "room", null);
			boolean useRoom = actorArea == null;
			return new ConditionActorLocation(actorRef, (useRoom ? actorRoom : actorArea), useRoom);
		case "actorAvailableForScene":
			return new ConditionActorAvailableForScene(actorRef);
		case "actorDead":
			return new ConditionActorDead(actorRef);
		default:
			return null;
		}
	}
	
	private static List<Condition> loadSubConditions(Element conditionElement) throws ParserConfigurationException, SAXException, IOException {
		NodeList subConditionElements = conditionElement.getElementsByTagName("condition");
		List<Condition> subConditions = new ArrayList<>();
		for(int i = 0; i < subConditionElements.getLength(); i++) {
			if(subConditionElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element subConditionElement = (Element) subConditionElements.item(i);
				Condition subCondition = loadCondition(subConditionElement);
				subConditions.add(subCondition);
			}
		}
		return subConditions;
	}
	
	protected static List<Script> loadScripts(Element parentElement) {
		NodeList scriptElements = parentElement.getElementsByTagName("script");
		List<Script> scripts = new ArrayList<>();
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
		ActorReference actorRef = loadActorReference(scriptElement);
		switch(type) {
		case "money":
			int moneyValue = LoadUtils.singleTagInt(scriptElement, "value", 0);
			return new ScriptMoney(actorRef, moneyValue);
		case "add_item":
			String addItemID = LoadUtils.singleTag(scriptElement, "item", null);
			return new ScriptAddItem(actorRef, addItemID);
		case "knowledge":
			String knowledgeID = LoadUtils.singleTag(scriptElement, "knowledge", null);
			return new ScriptKnowledge(knowledgeID);
		case "trade":
			return new ScriptTrade();
		case "dialogue":
			String topic = LoadUtils.singleTag(scriptElement, "topic", null);
			return new ScriptDialogue(actorRef, topic);
		case "factionRelation":
			String targetFaction = LoadUtils.singleTag(scriptElement, "targetFaction", null);
			String relationFaction = LoadUtils.singleTag(scriptElement, "relationFaction", null);
			String relationString = LoadUtils.singleTag(scriptElement, "relation", null);
			FactionRelation relation;
			switch(relationString) {
			case "FRIEND":
				relation = FactionRelation.FRIEND;
				break;
			case "ENEMY":
				relation = FactionRelation.ENEMY;
				break;
			case "NEUTRAL":
			default:
				relation = FactionRelation.NEUTRAL;
				break;
			}
			return new ScriptFactionRelation(targetFaction, relationFaction, relation);
		default:
			return null;
		}
	}
	
	private static ActorReference loadActorReference(Element actorReferenceElement) {
		String targetTypeString = actorReferenceElement.getAttribute("target");
		ReferenceType targetType;
		switch(targetTypeString) {
		case "player":
			targetType = ReferenceType.PLAYER;
			break;
		case "reference":
			targetType = ReferenceType.REFERENCE;
			break;
		case "subject":
		default:
			targetType = ReferenceType.SUBJECT;
			break;
		}
		String targetRef = LoadUtils.singleTag(actorReferenceElement, "actor", null);
		return new ActorReference(targetType, targetRef);
	}
	
}
