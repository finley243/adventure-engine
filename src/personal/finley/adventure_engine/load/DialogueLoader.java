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
import personal.finley.adventure_engine.dialogue.Choice;
import personal.finley.adventure_engine.dialogue.Condition;
import personal.finley.adventure_engine.dialogue.Line;
import personal.finley.adventure_engine.dialogue.Topic;

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
	
	private static Topic loadTopic(Element topic) throws ParserConfigurationException, SAXException, IOException {
		String topicID = topic.getAttribute("id");
		NodeList lineElements = topic.getElementsByTagName("line");
		List<Line> lines = new ArrayList<Line>();
		for(int i = 0; i < lineElements.getLength(); i++) {
			if(lineElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element lineElement = (Element) lineElements.item(i);
				Line line = loadLine(lineElement);
				lines.add(line);
			}
		}
		NodeList choiceElements = topic.getElementsByTagName("choice");
		List<Choice> choices = new ArrayList<Choice>();
		for(int i = 0; i < choiceElements.getLength(); i++) {
			if(choiceElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element choiceElement = (Element) choiceElements.item(i);
				Choice choice = loadChoice(choiceElement);
				choices.add(choice);
			}
		}
		return new Topic(topicID, lines, choices);
	}
	
	private static Line loadLine(Element line) throws ParserConfigurationException, SAXException, IOException {
		boolean once = boolAttribute(line, "once");
		boolean exit = boolAttribute(line, "exit");
		String redirect = line.getAttribute("redirect");
		if(redirect.isEmpty()) {
			redirect = null;
		}
		NodeList textElements = line.getElementsByTagName("text");
		List<String> texts = new ArrayList<String>();
		for(int i = 0; i < textElements.getLength(); i++) {
			if(textElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element lineElement = (Element) textElements.item(i);
				String text = lineElement.getTextContent();
				texts.add(text);
			}
		}
		Element conditionElement = (Element) line.getElementsByTagName("condition").item(0);
		Condition condition = loadCondition(conditionElement);
		return new Line(texts, condition, once, exit, redirect);
	}
	
	private static Choice loadChoice(Element choice) throws ParserConfigurationException, SAXException, IOException {
		boolean once = boolAttribute(choice, "once");
		String link = choice.getAttribute("link");
		System.out.println("Linked topic ID: " + link);
		String prompt = singleTag(choice, "prompt");
		Element conditionElement = (Element) choice.getElementsByTagName("condition").item(0);
		Condition condition = loadCondition(conditionElement);
		return new Choice(link, prompt, condition);
	}
	
	private static Condition loadCondition(Element condition) throws ParserConfigurationException, SAXException, IOException {
		
		return new Condition();
	}
	
	// ------------------------------------------------------------------------------
	
	private static boolean boolAttribute(Element element, String name) {
		return element.getAttribute(name).equalsIgnoreCase("true");
	}
	
	private static String singleTag(Element element, String name) {
		return element.getElementsByTagName(name).item(0).getTextContent();
	}
	
}
