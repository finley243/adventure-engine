package com.github.finley243.adventureengine.load;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.actor.Faction.FactionRelation;

public class FactionLoader {

	public static void loadFactions(File dir) throws ParserConfigurationException, SAXException, IOException {
		if(dir.isDirectory()) {
			File[] files = dir.listFiles();
			for(File file : files) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(file);
				Element rootElement = document.getDocumentElement();
				NodeList factions = rootElement.getElementsByTagName("faction");
				for(int i = 0; i < factions.getLength(); i++) {
					if(factions.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Element factionElement = (Element) factions.item(i);
						Faction faction = loadFaction(factionElement);
						Data.addFaction(faction.getID(), faction);
					}
				}
			}
		}
	}
	
	private static Faction loadFaction(Element factionElement) {
		String id = factionElement.getAttribute("id");
		FactionRelation defaultRelation = factionRelationTag(factionElement, "default");
		Map<String, FactionRelation> relations = loadRelations(factionElement);
		return new Faction(id, defaultRelation, relations);
	}
	
	private static Map<String, FactionRelation> loadRelations(Element factionElement) {
		Map<String, FactionRelation> relations = new HashMap<>();
		NodeList relationElements = factionElement.getElementsByTagName("relation");
		for(int i = 0; i < relationElements.getLength(); i++) {
			if(relationElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element relationElement = (Element) relationElements.item(i);
				String id = LoadUtils.singleTag(relationElement, "id", null);
				FactionRelation type = factionRelationTag(relationElement, "type");
				relations.put(id, type);
			}
		}
		return relations;
	}
	
	private static FactionRelation factionRelationTag(Element element, String name) {
		String factionRelationString = LoadUtils.singleTag(element, name, null);
		switch(factionRelationString) {
		case "FRIEND":
			return FactionRelation.FRIEND;
		case "ENEMY":
			return FactionRelation.ENEMY;
		case "NEUTRAL":
		default:
			return FactionRelation.NEUTRAL;
		}
	}

}
