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
import com.github.finley243.adventureengine.world.template.StatsWeapon.WeaponType;
import com.github.finley243.adventureengine.world.template.StatsItem;
import com.github.finley243.adventureengine.world.template.StatsKey;
import com.github.finley243.adventureengine.world.template.StatsWeapon;

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
		String name = LoadUtils.singleTag(itemElement, "name", null);
		int price = LoadUtils.singleTagInt(itemElement, "price", 0);
		switch(type) {
		case "consumable":
			ConsumableType consumableType = ConsumableType.valueOf(LoadUtils.singleTag(itemElement, "type", "OTHER"));
			return new StatsConsumable(id, name, price, consumableType);
		case "key":
			return new StatsKey(id, name);
		case "weapon":
			WeaponType weaponType = WeaponType.valueOf(LoadUtils.singleTag(itemElement, "type", null));
			int weaponActionPoints = LoadUtils.singleTagInt(itemElement, "actionpoints", 1);
			int weaponDamage = LoadUtils.singleTagInt(itemElement, "damage", 0);
			float weaponHitChance = LoadUtils.singleTagFloat(itemElement, "chance", 1.0f);
			int weaponClipSize = LoadUtils.singleTagInt(itemElement, "clipsize", 0);
			return new StatsWeapon(id, name, price, weaponType, weaponActionPoints, weaponDamage, weaponHitChance, weaponClipSize);
		}
		return null;
	}
	
}
