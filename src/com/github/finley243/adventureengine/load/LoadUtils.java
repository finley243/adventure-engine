package com.github.finley243.adventureengine.load;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.finley243.adventureengine.condition.Condition.Equality;

public class LoadUtils {

	public static boolean boolAttribute(Element element, String name) {
		if(!element.hasAttribute(name)) return false;
		return element.getAttribute(name).equalsIgnoreCase("true");
	}
	
	public static String singleTag(Element element, String name) {
		if(element.getElementsByTagName(name).getLength() == 0) return null;
		return element.getElementsByTagName(name).item(0).getTextContent();
	}
	
	public static int singleTagInt(Element element, String name) {
		String stringValue = LoadUtils.singleTag(element, name);
		if(stringValue == null) return 0;
		return Integer.parseInt(stringValue);
	}
	
	public static Set<String> setOfTags(Element element, String name) {
		NodeList nodes = element.getElementsByTagName(name);
		Set<String> output = new HashSet<String>();
		for(int i = 0; i < nodes.getLength(); i++) {
			if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				output.add(nodes.item(i).getTextContent());
			}
		}
		return output;
	}
	
	public static List<String> listOfTags(Element element, String name) {
		NodeList nodes = element.getElementsByTagName(name);
		List<String> output = new ArrayList<String>();
		for(int i = 0; i < nodes.getLength(); i++) {
			if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				output.add(nodes.item(i).getTextContent());
			}
		}
		return output;
	}
	
	public static Equality equalityTag(Element element, String name) {
		String logicString = LoadUtils.singleTag(element, name);
		switch(logicString) {
		case "EQUAL":
			return Equality.EQUAL;
		case "NOT_EQUAL":
			return Equality.NOT_EQUAL;
		case "LESS":
			return Equality.LESS;
		case "GREATER":
			return Equality.GREATER;
		case "LESS_EQUAL":
			return Equality.LESS_EQUAL;
		case "GREATER_EQUAL":
		default:
			return Equality.GREATER_EQUAL;
		}
	}
	
}
