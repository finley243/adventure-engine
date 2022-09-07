package com.github.finley243.adventureengine.load;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;

public class LoadUtils {

	public static String attribute(Element element, String name, String defaultValue) {
		if(element == null || !element.hasAttribute(name)) return defaultValue;
		return element.getAttribute(name);
	}

	public static Boolean attributeBool(Element element, String name, Boolean defaultValue) {
		if(element == null || !element.hasAttribute(name)) return defaultValue;
		return element.getAttribute(name).equalsIgnoreCase("true") || element.getAttribute(name).equalsIgnoreCase("t");
	}

	public static Integer attributeInt(Element element, String name, Integer defaultValue) {
		if(element == null || !element.hasAttribute(name)) return defaultValue;
		return Integer.parseInt(element.getAttribute(name));
	}

	public static Float attributeFloat(Element element, String name, Float defaultValue) {
		if(element == null || !element.hasAttribute(name)) return defaultValue;
		return Float.parseFloat(element.getAttribute(name));
	}

	public static <T extends Enum<T>> T attributeEnum(Element element, String name, Class<T> enumClass, T defaultValue) {
		if(element == null || !element.hasAttribute(name)) return defaultValue;
		String stringValue = element.getAttribute(name);
		for(T current : EnumSet.allOf(enumClass)) {
			if(stringValue.equalsIgnoreCase(current.toString())) {
				return current;
			}
		}
		return defaultValue;
	}
	
	public static String singleTag(Element parent, String name, String defaultValue) {
		Element element = singleChildWithName(parent, name);
		if(element == null) return defaultValue;
		return element.getTextContent();
	}
	
	public static Integer singleTagInt(Element parent, String name, Integer defaultValue) {
		String stringValue = LoadUtils.singleTag(parent, name, null);
		if(stringValue == null) return defaultValue;
		return Integer.parseInt(stringValue);
	}
	
	public static Float singleTagFloat(Element parent, String name, Float defaultValue) {
		String stringValue = LoadUtils.singleTag(parent, name, null);
		if(stringValue == null) return defaultValue;
		return Float.parseFloat(stringValue);
	}
	
	public static Boolean singleTagBoolean(Element parent, String name, Boolean defaultValue) {
		String stringValue = LoadUtils.singleTag(parent, name, null);
		if(stringValue == null) return defaultValue;
		return stringValue.equalsIgnoreCase("true") || stringValue.equalsIgnoreCase("t");
	}

	public static <T extends Enum<T>> T singleTagEnum(Element parent, String name, Class<T> enumClass, T defaultValue) {
		String stringValue = LoadUtils.singleTag(parent, name, null);
		if(stringValue == null) return defaultValue;
		for(T current : EnumSet.allOf(enumClass)) {
			if(stringValue.equalsIgnoreCase(current.toString())) {
				return current;
			}
		}
		return defaultValue;
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

	public static <T extends Enum<T>> Set<T> setOfEnumTags(Element parent, String name, Class<T> enumClass) {
		Set<T> enumSet = new HashSet<>();
		Set<String> stringSet = setOfTags(parent, name);
		for(T current : EnumSet.allOf(enumClass)) {
			for (String tag : stringSet) {
				if (tag.equalsIgnoreCase(current.toString())) {
					enumSet.add(current);
					break;
				}
			}
		}
		return enumSet;
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
			if(currentChild.getNodeType() == Node.ELEMENT_NODE && currentChild.getNodeName().equals(name)) {
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
			if(currentChild.getNodeType() == Node.ELEMENT_NODE && currentChild.getNodeName().equals(name)) {
				return (Element) currentChild;
			}
			currentChild = currentChild.getNextSibling();
		}
		return null;
	}
	
}
