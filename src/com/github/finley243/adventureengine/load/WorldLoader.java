package com.github.finley243.adventureengine.load;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.object.LinkedObject;
import com.github.finley243.adventureengine.world.object.ObjectChair;
import com.github.finley243.adventureengine.world.object.ObjectElevator;
import com.github.finley243.adventureengine.world.object.ObjectExit;
import com.github.finley243.adventureengine.world.object.ObjectSign;
import com.github.finley243.adventureengine.world.object.ObjectVendingMachine;
import com.github.finley243.adventureengine.world.object.WorldObject;

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
		boolean roomNameIsProper = boolAttribute(roomNameElement, "proper");
		String roomDescription = singleTag(roomElement, "description");
		
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
		boolean isProperName = boolAttribute(nameElement, "proper");
		boolean isProximateName = boolAttribute(nameElement, "prox");
		
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
		
		Area area = new Area(areaID, name, isProperName, isProximateName, roomID, linkSet, objectSet);
		for(WorldObject object : objectSet) {
			object.setArea(area);
		}
		return area;
	}
	
	private static WorldObject loadObject(Element objectElement, String areaID) {
		String objectType = objectElement.getAttribute("type");
		String objectName = singleTag(objectElement, "name");
		switch(objectType) {
		case "exit":
			String exitID = objectElement.getAttribute("id");
			String exitLink = singleTag(objectElement, "link");
			return new ObjectExit(exitID, objectName, exitLink);
		case "elevator":
			String elevatorID = objectElement.getAttribute("id");
			int floorNumber = singleTagInt(objectElement, "floornumber");
			String floorName = singleTag(objectElement, "floorname");
			Set<String> linkedElevatorIDs = setOfTags((Element) objectElement.getElementsByTagName("links").item(0), "link");
			return new ObjectElevator(elevatorID, objectName, floorNumber, floorName, linkedElevatorIDs);
		case "sign":
			String signText = singleTag(objectElement, "text");
			return new ObjectSign(objectName, signText);
		case "chair":
			return new ObjectChair(objectName);
		case "vending_machine":
			List<String> vendingItems = listOfTags((Element) objectElement.getElementsByTagName("items").item(0), "item");
			return new ObjectVendingMachine(objectName, vendingItems);
		}
		return null;
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
	
	private static Set<String> setOfTags(Element element, String name) {
		Set<String> output = new HashSet<String>();
		NodeList nodes = element.getElementsByTagName(name);
		for(int i = 0; i < nodes.getLength(); i++) {
			if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				output.add(nodes.item(i).getTextContent());
			}
		}
		return output;
	}
	
	private static List<String> listOfTags(Element element, String name) {
		List<String> output = new ArrayList<String>();
		NodeList nodes = element.getElementsByTagName(name);
		for(int i = 0; i < nodes.getLength(); i++) {
			if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				output.add(nodes.item(i).getTextContent());
			}
		}
		return output;
	}
	
}
