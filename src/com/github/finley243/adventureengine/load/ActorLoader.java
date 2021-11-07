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
import com.github.finley243.adventureengine.textgen.Context.Pronoun;
import com.github.finley243.adventureengine.world.template.StatsActor;

public class ActorLoader {

	public static void loadActors(File dir) throws ParserConfigurationException, SAXException, IOException {
		if(dir.isDirectory()) {
			File[] files = dir.listFiles();
			for(File file : files) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(file);
				Element rootElement = document.getDocumentElement();
				NodeList actors = rootElement.getElementsByTagName("actor");
				for(int i = 0; i < actors.getLength(); i++) {
					if(actors.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Element actorElement = (Element) actors.item(i);
						StatsActor actor = loadActor(actorElement);
						Data.addActorStats(actor.getID(), actor);
					}
				}
			}
		}
	}
	
	private static StatsActor loadActor(Element actorElement) {
		String id = actorElement.getAttribute("id");
		Element nameElement = (Element) actorElement.getElementsByTagName("name").item(0);
		String name = nameElement.getTextContent();
		boolean nameIsProper = LoadUtils.boolAttribute(nameElement, "proper", false);
		Pronoun pronoun = pronounTag(actorElement, "pronoun");
		String faction = LoadUtils.singleTag(actorElement, "faction", "default");
		List<String> idle;
		if(actorElement.getElementsByTagName("idle").getLength() > 0) {
			Element idleElement = (Element) actorElement.getElementsByTagName("idle").item(0);
			idle = LoadUtils.listOfTags(idleElement, "area");
		} else {
			idle = new ArrayList<>();
		}
		boolean preventMovement = LoadUtils.singleTagBoolean(actorElement, "preventMovement", false);
		int hp = LoadUtils.singleTagInt(actorElement, "hp", 0);
		String lootTable = LoadUtils.singleTag(actorElement, "loottable", null);
		String topic = LoadUtils.singleTag(actorElement, "topic", null);
		return new StatsActor(id, name, nameIsProper, pronoun, faction, idle, preventMovement, hp, lootTable, topic);
	}
	
	private static Pronoun pronounTag(Element element, String name) {
		String factionRelationString = LoadUtils.singleTag(element, name, null);
		switch(factionRelationString) {
		case "I":
			return Pronoun.I;
		case "YOU":
			return Pronoun.YOU;
		case "IT":
			return Pronoun.IT;
		case "HE":
			return Pronoun.HE;
		case "SHE":
			return Pronoun.SHE;
		case "THEY":
		default:
			return Pronoun.THEY;
		case "YOUALL":
			return Pronoun.YOUALL;
		case "WE":
			return Pronoun.WE;
		}
	}

}
