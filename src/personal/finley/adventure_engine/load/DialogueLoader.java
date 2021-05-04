package personal.finley.adventure_engine.load;

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

import personal.finley.adventure_engine.Data;
import personal.finley.adventure_engine.condition.Condition;
import personal.finley.adventure_engine.condition.Condition.Equality;
import personal.finley.adventure_engine.condition.ConditionMoney;
import personal.finley.adventure_engine.dialogue.Choice;
import personal.finley.adventure_engine.dialogue.Line;
import personal.finley.adventure_engine.dialogue.Topic;
import personal.finley.adventure_engine.dialogue.Topic.TopicType;

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
		boolean once = boolAttribute(lineElement, "once");
		boolean exit = boolAttribute(lineElement, "exit");
		String redirect = lineElement.getAttribute("redirect");
		if(redirect.isEmpty()) {
			redirect = null;
		}
		NodeList textElements = lineElement.getElementsByTagName("text");
		List<String> texts = new ArrayList<String>();
		for(int i = 0; i < textElements.getLength(); i++) {
			if(textElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element textElement = (Element) textElements.item(i);
				String text = textElement.getTextContent();
				texts.add(text);
			}
		}
		Element conditionElement = (Element) lineElement.getElementsByTagName("condition").item(0);
		Condition condition = loadCondition(conditionElement);
		return new Line(texts, condition, once, exit, redirect);
	}
	
	private static Choice loadChoice(Element choiceElement) throws ParserConfigurationException, SAXException, IOException {
		boolean once = boolAttribute(choiceElement, "once");
		String link = choiceElement.getAttribute("link");
		String prompt = singleTag(choiceElement, "prompt");
		Element conditionElement = (Element) choiceElement.getElementsByTagName("condition").item(0);
		Condition condition = loadCondition(conditionElement);
		return new Choice(link, prompt, condition, once);
	}
	
	private static Condition loadCondition(Element conditionElement) throws ParserConfigurationException, SAXException, IOException {
		if(conditionElement == null) return null;
		String type = conditionElement.getAttribute("type");
		Condition condition;
		switch(type) {
		case "compound":
			condition = null;
			break;
		case "money":
			int value = singleTagInt(conditionElement, "value");
			Equality equality = equalityTag(conditionElement, "equality");
			condition = new ConditionMoney(value, equality);
			break;
		default:
			condition = null;
		}
		return condition;
	}
	
	// ------------------------------------------------------------------------------
	
	private static boolean boolAttribute(Element element, String name) {
		return element.getAttribute(name).equalsIgnoreCase("true");
	}
	
	private static String singleTag(Element element, String name) {
		return element.getElementsByTagName(name).item(0).getTextContent();
	}
	
	private static int singleTagInt(Element element, String name) {
		return Integer.parseInt(singleTag(element, name));
	}
	
	private static Equality equalityTag(Element element, String name) {
		String logicString = singleTag(element, name);
		switch(logicString) {
		case "EQUAL":
			return Equality.EQUAL;
		case "NOT_EQUAL":
			return Equality.NOT_EQUAL;
		case "LESS":
			return Equality.LESS;
		case "GREATER":
			return Equality.GREATER;
		case "LESS_EQUAL":
			return Equality.LESS_EQUAL;
		case "GREATER_EQUAL":
		default:
			return Equality.GREATER_EQUAL;
		}
	}
	
}
