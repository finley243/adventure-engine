package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.condition.Condition.Equality;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoadUtils {

	public static String attribute(Element element, String name, String defaultValue) {
		if(!element.hasAttribute(name)) return defaultValue;
		return element.getAttribute(name);
	}

	public static boolean boolAttribute(Element element, String name, boolean defaultValue) {
		if(!element.hasAttribute(name)) return defaultValue;
		return element.getAttribute(name).equalsIgnoreCase("true");
	}
	
	public static String singleTag(Element parent, String name, String defaultValue) {
		Element element = singleChildWithName(parent, name);
		if(element == null) return defaultValue;
		return element.getTextContent();
	}
	
	public static int singleTagInt(Element parent, String name, int defaultValue) {
		String stringValue = LoadUtils.singleTag(parent, name, null);
		if(stringValue == null) return defaultValue;
		return Integer.parseInt(stringValue);
	}
	
	public static float singleTagFloat(Element parent, String name, float defaultValue) {
		String stringValue = LoadUtils.singleTag(parent, name, null);
		if(stringValue == null) return defaultValue;
		return Float.parseFloat(stringValue);
	}
	
	public static boolean singleTagBoolean(Element parent, String name, boolean defaultValue) {
		String stringValue = LoadUtils.singleTag(parent, name, null);
		if(stringValue == null) return defaultValue;
		return stringValue.equalsIgnoreCase("true");
	}
	
	public static Set<String> setOfTags(Element parent, String name) {
		if(parent == null) return new HashSet<>();
		List<Element> elements = directChildrenWithName(parent, name);
		Set<String> output = new HashSet<>();
		for (Element element : elements) {
			output.add(element.getTextContent());
		}
		return output;
	}
	
	public static List<String> listOfTags(Element parent, String name) {
		if(parent == null) return new ArrayList<>();
		List<Element> elements = directChildrenWithName(parent, name);
		List<String> output = new ArrayList<>();
		for (Element element : elements) {
			output.add(element.getTextContent());
		}
		return output;
	}

	public static List<Element> directChildrenWithName(Element parent, String name) {
		List<Element> matches = new ArrayList<>();
		if(parent == null) return matches;
		Node currentChild = parent.getFirstChild();
		while(currentChild != null) {
			if(currentChild.getNodeType() == Node.ELEMENT_NODE && ((Element) currentChild).getNodeName().equals(name)) {
				matches.add((Element) currentChild);
			}
			currentChild = currentChild.getNextSibling();
		}
		return matches;
	}

	public static Element singleChildWithName(Element parent, String name) {
		if(parent == null) return null;
		Node currentChild = parent.getFirstChild();
		while(currentChild != null) {
			if(currentChild.getNodeType() == Node.ELEMENT_NODE && ((Element) currentChild).getNodeName().equals(name)) {
				return (Element) currentChild;
			}
			currentChild = currentChild.getNextSibling();
		}
		return null;
	}
	
	public static Equality equalityTag(Element element, String name) {
		String logicString = LoadUtils.singleTag(element, name, null);
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
