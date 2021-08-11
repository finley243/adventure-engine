package com.github.finley243.adventureengine.load;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.LinkedObject;
import com.github.finley243.adventureengine.world.object.ObjectChair;
import com.github.finley243.adventureengine.world.object.ObjectCover;
import com.github.finley243.adventureengine.world.object.ObjectElevator;
import com.github.finley243.adventureengine.world.object.ObjectExit;
import com.github.finley243.adventureengine.world.object.ObjectSign;
import com.github.finley243.adventureengine.world.object.ObjectVendingMachine;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.github.finley243.adventureengine.world.template.ActorFactory;
import com.github.finley243.adventureengine.world.template.ItemFactory;

public class WorldLoader {

	public static void loadWorld(File dir) throws ParserConfigurationException, SAXException, IOException {
		if(dir.isDirectory()) {
			File[] files = dir.listFiles();
			for(File file : files) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(file);
				Element rootElement = document.getDocumentElement();
				NodeList roomElements = rootElement.getElementsByTagName("room");
				for(int i = 0; i < roomElements.getLength(); i++) {
					if(roomElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Element roomElement = (Element) roomElements.item(i);
						Room room = loadRoom(roomElement);
						Data.addRoom(room.getID(), room);
					}
				}
			}
		}
	}
	
	private static Room loadRoom(Element roomElement) throws ParserConfigurationException, SAXException, IOException {
		String roomID = roomElement.getAttribute("id");
		Element roomNameElement = (Element) roomElement.getElementsByTagName("name").item(0);
		String roomName = roomNameElement.getTextContent();
		boolean roomNameIsProper = LoadUtils.boolAttribute(roomNameElement, "proper", false);
		String roomDescription = LoadUtils.singleTag(roomElement, "roomDescription", null);
		
		NodeList areaElements = roomElement.getElementsByTagName("area");
		Set<Area> areas = new HashSet<Area>();
		for(int i = 0; i < areaElements.getLength(); i++) {
			if(areaElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element areaElement = (Element) areaElements.item(i);
				Area area = loadArea(areaElement, roomID);
				areas.add(area);
				Data.addArea(area.getID(), area);
			}
		}
		return new Room(roomID, roomName, roomNameIsProper, roomDescription, areas);
	}
	
	private static Area loadArea(Element areaElement, String roomID) {
		String areaID = areaElement.getAttribute("id");
		Element nameElement = (Element) areaElement.getElementsByTagName("name").item(0);
		String name = nameElement.getTextContent();
		boolean isProperName = LoadUtils.boolAttribute(nameElement, "proper", false);
		boolean isProximateName = LoadUtils.boolAttribute(nameElement, "prox", false);
		String description = LoadUtils.singleTag(areaElement, "areaDescription", null);
		
		Element linksElement = (Element) areaElement.getElementsByTagName("links").item(0);
		NodeList links = linksElement.getElementsByTagName("link");
		Set<String> linkSet = new HashSet<String>();
		for(int j = 0; j < links.getLength(); j++) {
			if(links.item(j).getNodeType() == Node.ELEMENT_NODE) {
				String linkText = links.item(j).getTextContent();
				linkSet.add(linkText);
			}
		}
		
		Element objectsElement = (Element) areaElement.getElementsByTagName("objects").item(0);
		NodeList objectElements = objectsElement.getElementsByTagName("object");
		Set<WorldObject> objectSet = new HashSet<WorldObject>();
		for(int j = 0; j < objectElements.getLength(); j++) {
			if(objectElements.item(j).getNodeType() == Node.ELEMENT_NODE) {
				Element objectElement = (Element) objectElements.item(j);
				WorldObject object = loadObject(objectElement, areaID);
				objectSet.add(object);
				if(object instanceof LinkedObject) {
					LinkedObject linkedObject = (LinkedObject) object;
					Data.addLinkedObject(linkedObject.getID(), linkedObject);
				}
			}
		}
		
		Area area = new Area(areaID, name, description, isProperName, isProximateName, roomID, linkSet, objectSet);
		for(WorldObject object : objectSet) {
			object.setArea(area);
		}
		
		Element actorsElement = (Element) areaElement.getElementsByTagName("actors").item(0);
		NodeList actorElements = actorsElement.getElementsByTagName("actor");
		for(int j = 0; j < actorElements.getLength(); j++) {
			if(actorElements.item(j).getNodeType() == Node.ELEMENT_NODE) {
				Element actorElement = (Element) actorElements.item(j);
				Actor actor = loadActor(actorElement, area);
				Data.addActor(actor.getID(), actor);
			}
		}
		
		return area;
	}
	
	private static WorldObject loadObject(Element objectElement, String areaID) {
		String objectType = objectElement.getAttribute("type");
		String objectName = LoadUtils.singleTag(objectElement, "name", null);
		String objectID = objectElement.getAttribute("id");
		String description = LoadUtils.singleTag(objectElement, "description", null);
		switch(objectType) {
		case "exit":
			String exitLink = LoadUtils.singleTag(objectElement, "link", null);
			Set<String> exitKeys = LoadUtils.setOfTags(objectElement, "key");
			return new ObjectExit(objectID, objectName, description, exitLink, exitKeys);
		case "elevator":
			int floorNumber = LoadUtils.singleTagInt(objectElement, "floornumber", 1);
			String floorName = LoadUtils.singleTag(objectElement, "floorname", null);
			Set<String> linkedElevatorIDs = LoadUtils.setOfTags((Element) objectElement.getElementsByTagName("links").item(0), "link");
			return new ObjectElevator(objectID, objectName, description, floorNumber, floorName, linkedElevatorIDs);
		case "sign":
			List<String> signText = LoadUtils.listOfTags((Element) objectElement.getElementsByTagName("lines").item(0), "text");
			return new ObjectSign(objectName, description, signText);
		case "chair":
			return new ObjectChair(objectName, description);
		case "cover":
			return new ObjectCover(objectName, description);
		case "vending_machine":
			List<String> vendingItems = LoadUtils.listOfTags((Element) objectElement.getElementsByTagName("items").item(0), "item");
			return new ObjectVendingMachine(objectName, description, vendingItems);
		case "item":
			String itemID = LoadUtils.singleTag(objectElement, "item", null);
			return ItemFactory.create(itemID);
		}
		return null;
	}
	
	private static Actor loadActor(Element actorElement, Area area) {
		String ID = actorElement.getAttribute("id");
		String stats = LoadUtils.singleTag(actorElement, "stats", null);
		String descriptor = LoadUtils.singleTag(actorElement, "descriptor", null);
		String topicID = LoadUtils.singleTag(actorElement, "topic", null);
		boolean startDead = LoadUtils.singleTagBoolean(actorElement, "startdead", false);
		return ActorFactory.create(ID, area, Data.getActorStats(stats), descriptor, topicID);
	}
	
}
