package com.github.finley243.adventureengine.load;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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
import com.github.finley243.adventureengine.world.object.ObjectChair;
import com.github.finley243.adventureengine.world.object.ObjectElevator;
import com.github.finley243.adventureengine.world.object.ObjectExit;
import com.github.finley243.adventureengine.world.object.ObjectSign;
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
				NodeList rooms = rootElement.getElementsByTagName("room");
				for(int i = 0; i < rooms.getLength(); i++) {
					if(rooms.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Element roomElement = (Element) rooms.item(i);
						Room room = loadRoom(roomElement);
						Data.addRoom(room.getID(), room);
					}
				}
			}
		}
	}
	
	private static Room loadRoom(Element room) throws ParserConfigurationException, SAXException, IOException {
		String roomID = room.getAttribute("id");
		Element roomNameElement = (Element) room.getElementsByTagName("name").item(0);
		String roomName = roomNameElement.getTextContent();
		boolean roomNameIsProper = boolAttribute(roomNameElement, "proper");
		String roomDescription = singleTag(room, "description");
		
		NodeList areas = room.getElementsByTagName("area");
		Set<Area> areaSet = new HashSet<Area>();
		for(int i = 0; i < areas.getLength(); i++) {
			if(areas.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element areaElement = (Element) areas.item(i);
				Area area = loadArea(areaElement, roomID);
				areaSet.add(area);
				Data.addArea(area.getID(), area);
			}
		}
		return new Room(roomID, roomName, roomNameIsProper, roomDescription, areaSet);
	}
	
	private static Area loadArea(Element area, String roomID) {
		String areaID = area.getAttribute("id");
		Element nameElement = (Element) area.getElementsByTagName("name").item(0);
		String name = nameElement.getTextContent();
		boolean isProperName = boolAttribute(nameElement, "proper");
		boolean isProximateName = boolAttribute(nameElement, "prox");
		
		Element linksElement = (Element) area.getElementsByTagName("links").item(0);
		NodeList links = linksElement.getElementsByTagName("link");
		Set<String> linkSet = new HashSet<String>();
		for(int j = 0; j < links.getLength(); j++) {
			if(links.item(j).getNodeType() == Node.ELEMENT_NODE) {
				String linkText = links.item(j).getTextContent();
				linkSet.add(linkText);
			}
		}
		
		Element objectsElement = (Element) area.getElementsByTagName("objects").item(0);
		NodeList objects = objectsElement.getElementsByTagName("object");
		Set<WorldObject> objectSet = new HashSet<WorldObject>();
		for(int j = 0; j < objects.getLength(); j++) {
			if(objects.item(j).getNodeType() == Node.ELEMENT_NODE) {
				Element objectElement = (Element) objects.item(j);
				WorldObject object = loadObject(objectElement, areaID);
				objectSet.add(object);
				Data.addObject(object.getID(), object);
			}
		}
		
		return new Area(areaID, name, isProperName, isProximateName, roomID, linkSet, objectSet);
	}
	
	private static WorldObject loadObject(Element object, String areaID) {
		String objectID = object.getAttribute("id");
		String objectType = object.getAttribute("type");
		String objectName = singleTag(object, "name");
		switch(objectType) {
		case "exit":
			String exitLink = singleTag(object, "link");
			return new ObjectExit(objectID, areaID, objectName, exitLink);
		case "elevator":
			int floorNumber = singleTagInt(object, "floornumber");
			String floorName = singleTag(object, "floorname");
			Set<String> linkedElevatorIDs = setTags((Element) object.getElementsByTagName("links").item(0), "link");
			return new ObjectElevator(objectID, areaID, objectName, floorNumber, floorName, linkedElevatorIDs);
		case "sign":
			String signText = singleTag(object, "text");
			return new ObjectSign(objectID, areaID, objectName, signText);
		case "chair":
			return new ObjectChair(objectID, areaID, objectName);
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
	
	private static Set<String> setTags(Element element, String name) {
		Set<String> output = new HashSet<String>();
		NodeList nodes = element.getElementsByTagName(name);
		for(int i = 0; i < nodes.getLength(); i++) {
			if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				output.add(nodes.item(i).getTextContent());
			}
		}
		return output;
	}
	
}
