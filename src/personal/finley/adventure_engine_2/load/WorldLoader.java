package personal.finley.adventure_engine_2.load;

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

import personal.finley.adventure_engine_2.Data;
import personal.finley.adventure_engine_2.world.environment.Area;
import personal.finley.adventure_engine_2.world.environment.Room;
import personal.finley.adventure_engine_2.world.object.ObjectBase;
import personal.finley.adventure_engine_2.world.object.ObjectExit;

public class WorldLoader {

	public static void loadRooms(File dir) throws ParserConfigurationException, SAXException, IOException {
		if(dir.isDirectory()) {
			File[] files = dir.listFiles();
			for(File file : files) {
				loadRoom(file);
			}
		}
	}
	
	private static void loadRoom(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(file);
		
		Element room = document.getDocumentElement();
		String roomID = room.getAttribute("id");
		Element roomNameElement = (Element) room.getElementsByTagName("name").item(0);
		String roomName = roomNameElement.getTextContent();
		boolean roomIsProperName = roomNameElement.getAttribute("proper").equalsIgnoreCase("true");
		String roomDescription = room.getElementsByTagName("description").item(0).getTextContent();
		Room tempRoom = new Room(roomName, roomIsProperName, roomDescription);
		Data.addRoom(roomID, tempRoom);
		
		NodeList areas = room.getElementsByTagName("area");
		for(int i = 0; i < areas.getLength(); i++) {
			if(areas.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element area = (Element) areas.item(i);
				String areaID = area.getAttribute("id");
				Element nameElement = (Element) area.getElementsByTagName("name").item(0);
				String name = nameElement.getTextContent();
				boolean isProperName = nameElement.getAttribute("proper").equalsIgnoreCase("true");
				boolean isProximateName = nameElement.getAttribute("prox").equalsIgnoreCase("true");
				
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
				Set<ObjectBase> objectSet = new HashSet<ObjectBase>();
				for(int j = 0; j < objects.getLength(); j++) {
					if(objects.item(j).getNodeType() == Node.ELEMENT_NODE) {
						Element objectElement = (Element) objects.item(j);
						ObjectBase object = loadObject(objectElement, areaID);
						objectSet.add(object);
						Data.addObject(object.getID(), object);
					}
				}
				
				Area tempArea = new Area(areaID, name, isProperName, isProximateName, roomID, linkSet, objectSet);
				Data.addArea(areaID, tempArea);
				tempRoom.addArea(tempArea);
			}
		}
	}
	
	private static ObjectBase loadObject(Element object, String areaID) {
		String objectID = object.getAttribute("id");
		String objectType = object.getAttribute("type");
		String objectName = object.getElementsByTagName("name").item(0).getTextContent();
		switch(objectType) {
			case "exit":
				String exitLink = object.getElementsByTagName("link").item(0).getTextContent();
				System.out.println("ObjectID: " + objectID);
				System.out.println("ExitLink: " + exitLink);
				return new ObjectExit(objectID, areaID, objectName, exitLink);
		}
		return null;
	}
	
}
