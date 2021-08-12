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
import com.github.finley243.adventureengine.actor.Actor.Attribute;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.effect.EffectAttribute;
import com.github.finley243.adventureengine.effect.EffectHeal;
import com.github.finley243.adventureengine.effect.EffectHealOverTime;
import com.github.finley243.adventureengine.world.template.StatsConsumable;
import com.github.finley243.adventureengine.world.template.StatsConsumable.ConsumableType;
import com.github.finley243.adventureengine.world.template.StatsItem;
import com.github.finley243.adventureengine.world.template.StatsKey;
import com.github.finley243.adventureengine.world.template.StatsWeapon;
import com.github.finley243.adventureengine.world.template.StatsWeapon.WeaponType;

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
		String description = LoadUtils.singleTag(itemElement, "description", null);
		int price = LoadUtils.singleTagInt(itemElement, "price", 0);
		switch(type) {
		case "consumable":
			ConsumableType consumableType = ConsumableType.valueOf(LoadUtils.singleTag(itemElement, "type", "OTHER"));
			List<Effect> consumableEffects = loadEffects((Element) itemElement.getElementsByTagName("effects").item(0));
			return new StatsConsumable(id, name, description, price, consumableType, consumableEffects);
		case "key":
			return new StatsKey(id, name, description);
		case "weapon":
			WeaponType weaponType = WeaponType.valueOf(LoadUtils.singleTag(itemElement, "type", null));
			int weaponDamage = LoadUtils.singleTagInt(itemElement, "damage", 0);
			int weaponRate = LoadUtils.singleTagInt(itemElement, "rate", 1);
			int critDamage = LoadUtils.singleTagInt(itemElement, "critDamage", 0);
			float weaponHitChance = LoadUtils.singleTagFloat(itemElement, "chance", 1.0f);
			int weaponClipSize = LoadUtils.singleTagInt(itemElement, "clipsize", 0);
			return new StatsWeapon(id, name, description, price, weaponType, weaponDamage, weaponRate, critDamage, weaponHitChance, weaponClipSize);
		}
		return null;
	}
	
	private static List<Effect> loadEffects(Element effectsElement) {
		NodeList effectElements = effectsElement.getElementsByTagName("effect");
		List<Effect> effects = new ArrayList<Effect>();
		for(int i = 0; i < effectElements.getLength(); i++) {
			if(effectElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element effectElement = (Element) effectElements.item(i);
				effects.add(loadEffect(effectElement));
			}
		}
		return effects;
	}
	
	private static Effect loadEffect(Element effectElement) {
		String effectType = effectElement.getAttribute("type");
		int duration = LoadUtils.singleTagInt(effectElement, "duration", 0);
		int amount = LoadUtils.singleTagInt(effectElement, "amount", 0);
		switch(effectType) {
		case "heal":
			return new EffectHeal(amount);
		case "heal_over_time":
			return new EffectHealOverTime(duration, amount);
		case "attribute":
			Attribute attribute = Attribute.valueOf(LoadUtils.singleTag(effectElement, "attribute", null).toUpperCase());
			return new EffectAttribute(duration, attribute, amount);
		default:
			return null;
		}
	}
	
}
