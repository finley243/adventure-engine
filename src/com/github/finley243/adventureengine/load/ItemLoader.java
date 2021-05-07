package com.github.finley243.adventureengine.load;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.world.template.StatsConsumable;
import com.github.finley243.adventureengine.world.template.StatsItem;

public class ItemLoader {

	public static void loadItems(File dir) throws ParserConfigurationException, SAXException, IOException {
		if(dir.isDirectory()) {
			File[] files = dir.listFiles();
			for(File file : files) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(file);
				Element rootElement = document.getDocumentElement();
				NodeList items = rootElement.getElementsByTagName("item");
				for(int i = 0; i < items.getLength(); i++) {
					if(items.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Element itemElement = (Element) items.item(i);
						StatsItem item = loadItem(itemElement);
						Data.addItem(item.getID(), item);
					}
				}
			}
		}
	}
	
	private static StatsItem loadItem(Element itemElement) throws ParserConfigurationException, SAXException, IOException {
		String type = itemElement.getAttribute("type");
		String id = itemElement.getAttribute("id");
		String name = singleTag(itemElement, "name");
		switch(type) {
		case "consumable":
			return new StatsConsumable(id, name);
		}
		return null;
	}
	
	private static boolean boolAttribute(Element element, String name) {
		return element.getAttribute(name).equalsIgnoreCase("true");
	}
	
	private static String singleTag(Element element, String name) {
		return element.getElementsByTagName(name).item(0).getTextContent();
	}
	
	private static int singleTagInt(Element element, String name) {
		return Integer.parseInt(singleTag(element, name));
	}
	
}
