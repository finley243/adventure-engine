package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.action.ActionTemplate;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.script.Script;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class LoadUtils {

	public static String attribute(Element element, String name, String defaultValue) {
		if (element == null || !element.hasAttribute(name)) return defaultValue;
		return element.getAttribute(name);
	}

	public static Boolean attributeBool(Element element, String name, Boolean defaultValue) {
		if (element == null || !element.hasAttribute(name)) return defaultValue;
		return element.getAttribute(name).equalsIgnoreCase("true") || element.getAttribute(name).equalsIgnoreCase("t");
	}

	public static Integer attributeInt(Element element, String name, Integer defaultValue) {
		if (element == null || !element.hasAttribute(name)) return defaultValue;
		return Integer.parseInt(element.getAttribute(name));
	}

	public static Float attributeFloat(Element element, String name, Float defaultValue) {
		if (element == null || !element.hasAttribute(name)) return defaultValue;
		return Float.parseFloat(element.getAttribute(name));
	}

	public static <T extends Enum<T>> T attributeEnum(Element element, String name, Class<T> enumClass, T defaultValue) {
		if (element == null || !element.hasAttribute(name)) return defaultValue;
		String stringValue = element.getAttribute(name);
		for (T current : EnumSet.allOf(enumClass)) {
			if(stringValue.equalsIgnoreCase(current.toString())) {
				return current;
			}
		}
		throw new IllegalArgumentException("No enum constant with name " + stringValue + " in " + enumClass.getName());
	}
	
	public static String singleTag(Element parent, String name, String defaultValue) {
		Element element = singleChildWithName(parent, name);
		if (element == null) return defaultValue;
		return element.getTextContent();
	}
	
	public static Integer singleTagInt(Element parent, String name, Integer defaultValue) {
		String stringValue = LoadUtils.singleTag(parent, name, null);
		if (stringValue == null) return defaultValue;
		return Integer.parseInt(stringValue);
	}
	
	public static Float singleTagFloat(Element parent, String name, Float defaultValue) {
		String stringValue = LoadUtils.singleTag(parent, name, null);
		if (stringValue == null) return defaultValue;
		return Float.parseFloat(stringValue);
	}
	
	public static Boolean singleTagBoolean(Element parent, String name, Boolean defaultValue) {
		String stringValue = LoadUtils.singleTag(parent, name, null);
		if (stringValue == null) return defaultValue;
		return stringValue.equalsIgnoreCase("true") || stringValue.equalsIgnoreCase("t");
	}

	public static <T extends Enum<T>> T singleTagEnum(Element parent, String name, Class<T> enumClass, T defaultValue) {
		String stringValue = LoadUtils.singleTag(parent, name, null);
		if (stringValue == null) return defaultValue;
		for (T current : EnumSet.allOf(enumClass)) {
			if (stringValue.equalsIgnoreCase(current.toString())) {
				return current;
			}
		}
		throw new IllegalArgumentException("No enum constant with name " + stringValue + " in " + enumClass.getName());
	}
	
	public static Set<String> setOfTags(Element parent, String name) {
		if (parent == null) return new HashSet<>();
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
		for (T current : EnumSet.allOf(enumClass)) {
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
		if (parent == null) return new ArrayList<>();
		List<Element> elements = directChildrenWithName(parent, name);
		List<String> output = new ArrayList<>();
		for (Element element : elements) {
			output.add(element.getTextContent());
		}
		return output;
	}

	public static List<Element> directChildrenWithName(Element parent, String name) {
		List<Element> matches = new ArrayList<>();
		if (parent == null) return matches;
		Node currentChild = parent.getFirstChild();
		while (currentChild != null) {
			if (currentChild.getNodeType() == Node.ELEMENT_NODE && currentChild.getNodeName().equals(name)) {
				matches.add((Element) currentChild);
			}
			currentChild = currentChild.getNextSibling();
		}
		return matches;
	}

	public static Element singleChildWithName(Element parent, String name) {
		if (parent == null) return null;
		Node currentChild = parent.getFirstChild();
		while (currentChild != null) {
			if (currentChild.getNodeType() == Node.ELEMENT_NODE && currentChild.getNodeName().equals(name)) {
				return (Element) currentChild;
			}
			currentChild = currentChild.getNextSibling();
		}
		return null;
	}

	public static boolean hasChildWithName(Element parent, String name) {
		return singleChildWithName(parent, name) != null;
	}

	public static <T extends Enum<T>> T stringToEnum(String value, Class<T> enumClass) {
		if (value == null) return null;
		for (T current : EnumSet.allOf(enumClass)) {
			if (value.equalsIgnoreCase(current.toString())) {
				return current;
			}
		}
		throw new IllegalArgumentException("No enum constant with name " + value + " in " + enumClass.getName());
	}

	public static boolean isValidInteger(String value) {
		if (value == null) return false;
		Pattern pattern = Pattern.compile("^-?\\d+$");
		return pattern.matcher(value).matches();
	}

	public static boolean isValidFloat(String value) {
		if (value == null) return false;
		Pattern pattern = Pattern.compile("^-?\\d+\\.\\d+$");
		return pattern.matcher(value).matches();
	}

	public static boolean isValidBoolean(String value) {
		if (value == null) return false;
		return value.equalsIgnoreCase("t") || value.equalsIgnoreCase("f") || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
	}

	public static Condition loadCondition(Element element, ScriptParser scriptParser, String sourceName) {
		if (element == null) return null;
		Script conditionScript = loadScriptExpression(element, scriptParser, sourceName);
		return new Condition(conditionScript);
	}

	public static Script loadScript(Element element, ScriptParser scriptParser, String sourceName) {
		if (element == null) return null;
		String scriptText = element.getTextContent().trim();
		return scriptParser.parseScript(scriptText, sourceName);
	}

	public static Script loadScriptExpression(Element element, ScriptParser scriptParser, String sourceName) {
		if (element == null) return null;
		String scriptText = element.getTextContent().trim();
		return scriptParser.parseExpression(scriptText, sourceName);
	}

	public static Map<String, List<Script>> loadScriptsWithTriggers(Element parentElement, ScriptParser scriptParser, String sourceName) {
		Map<String, List<Script>> scripts = new HashMap<>();
		for (Element scriptElement : directChildrenWithName(parentElement, "script")) {
			String trigger = scriptElement.getAttribute("trigger");
			Script script = loadScript(scriptElement, scriptParser, sourceName + " - script trigger: " + trigger);
			scripts.computeIfAbsent(trigger, _ -> new ArrayList<>()).add(script);
		}
		return scripts;
	}

	public static Expression loadScriptLiteral(Element parentElement, ScriptParser scriptParser, String traceString) {
		if (parentElement == null) return null;
		String expressionText = parentElement.getTextContent().trim();
		return scriptParser.parseLiteral(expressionText, traceString);
	}

	public static List<ActionCustom.CustomActionHolder> loadCustomActions(Element parentElement, String name, ScriptParser scriptParser, Registry<ActionTemplate> actionRegistry, String traceString) {
		List<ActionCustom.CustomActionHolder> customActions = new ArrayList<>();
		if (parentElement != null) {
			for (Element actionElement : LoadUtils.directChildrenWithName(parentElement, name)) {
				String actionID = LoadUtils.attribute(actionElement, "template", null);
				ActionTemplate action = actionRegistry.getFromID(actionID);
				if (action == null) throw new GameDataException("Action with ID " + actionID + " not found");
				Map<String, Script> parameters = new HashMap<>();
				for (Element variableElement : LoadUtils.directChildrenWithName(actionElement, "parameter")) {
					String parameterName = LoadUtils.attribute(variableElement, "name", null);
					Script parameterValue = loadScriptExpression(variableElement, scriptParser, traceString + " - custom action parameter: " + parameterName);
					parameters.put(parameterName, parameterValue);
				}
				customActions.add(new ActionCustom.CustomActionHolder(action, parameters));
			}
		}
		return customActions;
	}

	public static <T> Map<String, T> loadAll(Element parentElement, String name, Function<Element, T> parser, Function<T, String> idExtractor) {
		Map<String, T> resultMap = new HashMap<>();
		for (Element child : directChildrenWithName(parentElement, name)) {
			T obj = parser.apply(child);
			resultMap.put(idExtractor.apply(obj), obj);
		}
		return resultMap;
	}

	public static <T> List<T> topologicalSort(Collection<T> items, Function<T, String> idExtractor, Function<T, Collection<String>> dependenciesExtractor) {
		Set<String> allIds = new HashSet<>();
		for (T item : items) {
			allIds.add(idExtractor.apply(item));
		}
		for (T item : items) {
			for (String dep : dependenciesExtractor.apply(item)) {
				if (!allIds.contains(dep)) {
					throw new GameDataException("'" + idExtractor.apply(item) + "' references unknown dependency '" + dep + "'");
				}
			}
		}

		Map<String, T> remaining = new LinkedHashMap<>();
		for (T item : items) remaining.put(idExtractor.apply(item), item);
		List<T> sorted = new ArrayList<>();
		Set<String> resolved = new HashSet<>();

		while (!remaining.isEmpty()) {
			boolean progress = false;
			Iterator<T> iter = remaining.values().iterator();
			while (iter.hasNext()) {
				T item = iter.next();
				if (resolved.containsAll(dependenciesExtractor.apply(item))) {
					sorted.add(item);
					resolved.add(idExtractor.apply(item));
					iter.remove();
					progress = true;
				}
			}
			if (!progress) throw new GameDataException("Cycle detected in dependency hierarchy");
		}
		return sorted;
	}
	
}
