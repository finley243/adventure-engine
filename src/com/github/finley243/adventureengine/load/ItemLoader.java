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
import com.github.finley243.adventureengine.world.template.StatsConsumable.ConsumableType;
import com.github.finley243.adventureengine.world.template.StatsItem;
import com.github.finley243.adventureengine.world.template.StatsKey;

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
		String name = LoadUtils.singleTag(itemElement, "name");
		switch(type) {
		case "consumable":
			int consumablePrice = LoadUtils.singleTagInt(itemElement, "price");
			ConsumableType consumableType = ConsumableType.valueOf(LoadUtils.singleTag(itemElement, "type"));
			return new StatsConsumable(id, name, consumablePrice, consumableType);
		case "key":
			return new StatsKey(id, name);
		}
		return null;
	}
	
}
