package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.load.ScriptParser;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * An action that can be executed at a given time
 */
public abstract class Script {

	public enum FlowStatementType {
		RETURN, BREAK, CONTINUE
	}

	private static final int NATIVE_FUNCTION_LINE = -1;
	private static final String NATIVE_FUNCTION_FILENAME = "NATIVE";

	private final ScriptTraceData traceData;

	public Script(ScriptTraceData traceData) {
		this.traceData = traceData;
	}

	/**
	 * Begin execution of the script
	 *
	 * @param context The context with which this script will be executed
	 */
	public abstract ScriptReturnData execute(Context context);

	protected ScriptTraceData getTraceData() {
		return traceData;
	}

	public static List<ScriptParser.ScriptData> getNativeFunctions() {
		List<ScriptParser.ScriptData> functions = new ArrayList<>();
		functions.add(new ScriptParser.ScriptData("attributeMenu", false, null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("points", true, null)), false, new ScriptAttributeMenu(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("skillMenu", false, null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("points", true, null)), false, new ScriptSkillMenu(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("startTimer", false, null, List.of(new ScriptParser.ScriptParameter("timer", true, null), new ScriptParser.ScriptParameter("duration", true, null)), false, new ScriptTimerStart(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("transferItem", false, null, List.of(new ScriptParser.ScriptParameter("transferType", false, Expression.constant("count")), new ScriptParser.ScriptParameter("from", false, null), new ScriptParser.ScriptParameter("to", false, null), new ScriptParser.ScriptParameter("item", false, null), new ScriptParser.ScriptParameter("count", false, Expression.constant(1))), false, new ScriptTransferItem(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("sendSensoryEvent", false, null, List.of(new ScriptParser.ScriptParameter("area", true, null), new ScriptParser.ScriptParameter("phrase", false, null), new ScriptParser.ScriptParameter("phraseAudible", false, null), new ScriptParser.ScriptParameter("detectSelf", false, Expression.constant(true))), true, new ScriptSensoryEvent(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("sendBark", false, null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("bark", true, null)), true, new ScriptBark(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("addEffect", false, null, List.of(new ScriptParser.ScriptParameter("target", true, null), new ScriptParser.ScriptParameter("effect", true, null)), false, new ScriptEffectAdd(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("removeEffect", false, null, List.of(new ScriptParser.ScriptParameter("target", true, null), new ScriptParser.ScriptParameter("effect", true, null)), false, new ScriptEffectRemove(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("startScene", false, null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("scene", true, null)), false, new ScriptScene(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("setFactionRelation", false, null, List.of(new ScriptParser.ScriptParameter("faction", true, null), new ScriptParser.ScriptParameter("relatedFaction", true, null), new ScriptParser.ScriptParameter("relation", true, null)), false, new ScriptFactionRelation(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("startCombat", false, null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("target", true, null)), false, new ScriptCombat(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("isCombatant", true, Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("target", true, null)), false, new ScriptIsCombatant(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("isVisible", true, Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("target", true, null)), false, new ScriptIsVisible(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("targetType", true, Expression.DataType.STRING, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("target", true, null)), false, new ScriptTargetType(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("randomBoolean", true, Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("chance", true, null)), false, new ScriptRandomBoolean(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("randomInteger", true, Expression.DataType.INTEGER, List.of(new ScriptParser.ScriptParameter("min", true, null), new ScriptParser.ScriptParameter("max", true, null)), false, new ScriptRandomInteger(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("inventoryContains", true, Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("inventory", true, null), new ScriptParser.ScriptParameter("item", true, null), new ScriptParser.ScriptParameter("requireAll", false, Expression.constant(false))), false, new ScriptInventoryContains(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("round", true, Expression.DataType.INTEGER, List.of(new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptRound(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("scaleLinear", true, Expression.DataType.FLOAT, List.of(new ScriptParser.ScriptParameter("input", true, null), new ScriptParser.ScriptParameter("inputMin", true, null), new ScriptParser.ScriptParameter("inputMax", true, null), new ScriptParser.ScriptParameter("outputMin", true, null), new ScriptParser.ScriptParameter("outputMax", true, null)), false, new ScriptScaleLinear(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("scaleLog", true, Expression.DataType.FLOAT, List.of(new ScriptParser.ScriptParameter("input", true, null), new ScriptParser.ScriptParameter("inputMin", true, null), new ScriptParser.ScriptParameter("inputMax", true, null), new ScriptParser.ScriptParameter("outputMin", true, null), new ScriptParser.ScriptParameter("outputMax", true, null)), false, new ScriptScaleLog(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("setUnion", true, Expression.DataType.SET, List.of(new ScriptParser.ScriptParameter("setOne", true, null), new ScriptParser.ScriptParameter("setTwo", true, null)), false, new ScriptSetUnion(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("setIntersect", true, Expression.DataType.SET, List.of(new ScriptParser.ScriptParameter("setOne", true, null), new ScriptParser.ScriptParameter("setTwo", true, null)), false, new ScriptSetIntersect(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("setDifference", true, Expression.DataType.SET, List.of(new ScriptParser.ScriptParameter("setOne", true, null), new ScriptParser.ScriptParameter("setTwo", true, null)), false, new ScriptSetDifference(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("setSymmetricDifference", true, Expression.DataType.SET, List.of(new ScriptParser.ScriptParameter("setOne", true, null), new ScriptParser.ScriptParameter("setTwo", true, null)), false, new ScriptSetSymmetricDifference(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("contains", true, Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("collection", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptCollectionContains(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("size", true, Expression.DataType.INTEGER, List.of(new ScriptParser.ScriptParameter("collection", true, null)), false, new ScriptCollectionSize(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("add", false, null, List.of(new ScriptParser.ScriptParameter("collection", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptCollectionAdd(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("listAddIndex", false, null, List.of(new ScriptParser.ScriptParameter("list", true, null), new ScriptParser.ScriptParameter("index", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptListAddIndex(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("remove", false, null, List.of(new ScriptParser.ScriptParameter("collection", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptCollectionRemove(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("listRemoveIndex", true, null, List.of(new ScriptParser.ScriptParameter("list", true, null), new ScriptParser.ScriptParameter("index", true, null)), false, new ScriptListRemoveIndex(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("listIndexOf", false, null, List.of(new ScriptParser.ScriptParameter("list", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptListIndexOf(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("listSet", false, null, List.of(new ScriptParser.ScriptParameter("list", true, null), new ScriptParser.ScriptParameter("index", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptListIndexSet(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("listGet", true, null, List.of(new ScriptParser.ScriptParameter("list", true, null), new ScriptParser.ScriptParameter("index", true, null)), false, new ScriptListIndexGet(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("clear", false, null, List.of(new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptCollectionClear(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("copy", true, Expression.DataType.LIST, List.of(new ScriptParser.ScriptParameter("collection", true, null)), false, new ScriptCopy(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("subList", true, Expression.DataType.LIST, List.of(new ScriptParser.ScriptParameter("list", true, null), new ScriptParser.ScriptParameter("start", true, null), new ScriptParser.ScriptParameter("end", true, null)), false, new ScriptSubList(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("listConcat", true, Expression.DataType.LIST, List.of(new ScriptParser.ScriptParameter("listOne", true, null), new ScriptParser.ScriptParameter("listTwo", true, null)), false, new ScriptListConcat(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("selectRandom", true, null, List.of(new ScriptParser.ScriptParameter("collection", true, null)), false, new ScriptRandomValueFromCollection(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("statHolderType", true, Expression.DataType.STRING, List.of(new ScriptParser.ScriptParameter("holder", true, null)), false, new ScriptStatHolderType(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("isTimerActive", true, Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("timer", true, null)), false, new ScriptTimerActive(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("toString", true, Expression.DataType.STRING, List.of(new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptToString(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("subString", true, Expression.DataType.STRING, List.of(new ScriptParser.ScriptParameter("string", true, null), new ScriptParser.ScriptParameter("start", true, null), new ScriptParser.ScriptParameter("end", true, null)), false, new ScriptSubString(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("toUpperCase", true, Expression.DataType.STRING, List.of(new ScriptParser.ScriptParameter("string", true, null)), false, new ScriptStringCase(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), ScriptStringCase.CaseType.UPPER)));
		functions.add(new ScriptParser.ScriptData("toLowerCase", true, Expression.DataType.STRING, List.of(new ScriptParser.ScriptParameter("string", true, null)), false, new ScriptStringCase(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), ScriptStringCase.CaseType.LOWER)));
		functions.add(new ScriptParser.ScriptData("toTitleCase", true, Expression.DataType.STRING, List.of(new ScriptParser.ScriptParameter("string", true, null)), false, new ScriptStringCase(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), ScriptStringCase.CaseType.TITLE)));
		functions.add(new ScriptParser.ScriptData("dataType", true, Expression.DataType.STRING, List.of(new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptDataType(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		functions.add(new ScriptParser.ScriptData("sleep", false, null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("duration", true, null)), false, new ScriptSleep(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME))));
		return functions;
	}

	public static Script constant(boolean value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.constant(value));
	}

	public static Script constant(int value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.constant(value));
	}

	public static Script constant(float value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.constant(value));
	}

	public static Script constant(String value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.constant(value));
	}

	public static Script constant(Set<String> value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.constant(value));
	}

	public static Script constant(Inventory value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.constant(value));
	}

	public static Script constant(Noun value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.constant(value));
	}

	public static Script constant(StatHolder value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.constant(value));
	}

	public record ScriptReturnData(Expression value, FlowStatementType flowStatement, ScriptErrorData error) {
		public String stackTrace() {
			if (error == null) {
				return null;
			}
			return error.message() + "\n - (" + error.traceData().line() + ") " + error.traceData().fileName();
		}
	}

	public record ScriptErrorData(String message, ScriptTraceData traceData) {}

	public record ScriptTraceData(int line, String fileName) {}

}
