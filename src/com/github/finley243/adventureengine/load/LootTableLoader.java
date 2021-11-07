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
import com.github.finley243.adventureengine.world.item.LootTable;
import com.github.finley243.adventureengine.world.item.LootTableEntry;

public class LootTableLoader {

	public static void loadTables(File dir) throws ParserConfigurationException, SAXException, IOException {
		if(dir.isDirectory()) {
			File[] files = dir.listFiles();
			for(File file : files) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(file);
				Element rootElement = document.getDocumentElement();
				NodeList tables = rootElement.getElementsByTagName("loottable");
				for(int i = 0; i < tables.getLength(); i++) {
					if(tables.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Element tableElement = (Element) tables.item(i);
						LootTable table = loadTable(tableElement);
						Data.addLootTable(table.getID(), table);
					}
				}
			}
		}
	}
	
	private static LootTable loadTable(Element tableElement) {
		String tableID = tableElement.getAttribute("id");
		boolean useAll = LoadUtils.boolAttribute(tableElement, "useAll", false);
		NodeList entryElements = tableElement.getElementsByTagName("entry");
		List<LootTableEntry> entries = new ArrayList<>();
		for(int i = 0; i < entryElements.getLength(); i++) {
			if(entryElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element entryElement = (Element) entryElements.item(i);
				LootTableEntry entry = loadEntry(entryElement);
				entries.add(entry);
			}
		}
		return new LootTable(tableID, useAll, entries);
	}
	
	private static LootTableEntry loadEntry(Element entryElement) {
		String referenceID = LoadUtils.singleTag(entryElement, "reference", null);
		boolean isLootTable = false;
		Node referenceNode = entryElement.getElementsByTagName("reference").item(0);
		if(referenceNode.getNodeType() == Node.ELEMENT_NODE) {
			Element referenceElement = (Element) referenceNode;
			isLootTable = LoadUtils.boolAttribute(referenceElement, "table", false);
		}
		float chance = LoadUtils.singleTagFloat(entryElement, "chance", 1.0f);
		int count = LoadUtils.singleTagInt(entryElement, "count", 1);
		return new LootTableEntry(referenceID, isLootTable, chance, count);
	}

}
