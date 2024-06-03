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

	private final int line;

	public Script(int line) {
		this.line = line;
	}

	/**
	 * Begin execution of the script
	 *
	 * @param context The context with which this script will be executed
	 */
	public abstract ScriptReturnData execute(Context context);

	protected int getLine() {
		return line;
	}

	public static void loadNativeFunctions(Game game) {
		List<ScriptParser.ScriptData> functions = new ArrayList<>();
		functions.add(new ScriptParser.ScriptData("attributeMenu", false, null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("points", true, null)), false, new ScriptAttributeMenu(-1)));
		functions.add(new ScriptParser.ScriptData("skillMenu", false, null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("points", true, null)), false, new ScriptSkillMenu(-1)));
		functions.add(new ScriptParser.ScriptData("startTimer", false, null, List.of(new ScriptParser.ScriptParameter("timer", true, null), new ScriptParser.ScriptParameter("duration", true, null)), false, new ScriptTimerStart(-1)));
		functions.add(new ScriptParser.ScriptData("transferItem", false, null, List.of(new ScriptParser.ScriptParameter("transferType", false, Expression.constant("count")), new ScriptParser.ScriptParameter("from", false, null), new ScriptParser.ScriptParameter("to", false, null), new ScriptParser.ScriptParameter("item", false, null), new ScriptParser.ScriptParameter("count", false, Expression.constant(1))), false, new ScriptTransferItem(-1)));
		functions.add(new ScriptParser.ScriptData("sendSensoryEvent", false, null, List.of(new ScriptParser.ScriptParameter("area", true, null), new ScriptParser.ScriptParameter("phrase", false, null), new ScriptParser.ScriptParameter("phraseAudible", false, null), new ScriptParser.ScriptParameter("detectSelf", false, Expression.constant(true))), true, new ScriptSensoryEvent(-1)));
		functions.add(new ScriptParser.ScriptData("sendBark", false, null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("bark", true, null)), true, new ScriptBark(-1)));
		functions.add(new ScriptParser.ScriptData("startScene", false, null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("scene", true, null)), false, new ScriptScene(-1)));
		functions.add(new ScriptParser.ScriptData("setFactionRelation", false, null, List.of(new ScriptParser.ScriptParameter("faction", true, null), new ScriptParser.ScriptParameter("relatedFaction", true, null), new ScriptParser.ScriptParameter("relation", true, null)), false, new ScriptFactionRelation(-1)));
		functions.add(new ScriptParser.ScriptData("startCombat", false, null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("target", true, null)), false, new ScriptCombat(-1)));
		functions.add(new ScriptParser.ScriptData("isCombatant", true, Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("target", true, null)), false, new ScriptIsCombatant(-1)));
		functions.add(new ScriptParser.ScriptData("isVisible", true, Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("target", true, null)), false, new ScriptIsVisible(-1)));
		functions.add(new ScriptParser.ScriptData("randomBoolean", true, Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("chance", true, null)), false, new ScriptRandomBoolean(-1)));
		functions.add(new ScriptParser.ScriptData("inventoryContains", true, Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("inventory", true, null), new ScriptParser.ScriptParameter("item", true, null), new ScriptParser.ScriptParameter("requireAll", false, Expression.constant(false))), false, new ScriptInventoryContains(-1)));
		functions.add(new ScriptParser.ScriptData("round", true, Expression.DataType.INTEGER, List.of(new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptRound(-1)));
		functions.add(new ScriptParser.ScriptData("scaleLinear", true, Expression.DataType.FLOAT, List.of(new ScriptParser.ScriptParameter("input", true, null), new ScriptParser.ScriptParameter("inputMin", true, null), new ScriptParser.ScriptParameter("inputMax", true, null), new ScriptParser.ScriptParameter("outputMin", true, null), new ScriptParser.ScriptParameter("outputMax", true, null)), false, new ScriptScaleLinear(-1)));
		functions.add(new ScriptParser.ScriptData("scaleLog", true, Expression.DataType.FLOAT, List.of(new ScriptParser.ScriptParameter("input", true, null), new ScriptParser.ScriptParameter("inputMin", true, null), new ScriptParser.ScriptParameter("inputMax", true, null), new ScriptParser.ScriptParameter("outputMin", true, null), new ScriptParser.ScriptParameter("outputMax", true, null)), false, new ScriptScaleLog(-1)));
		functions.add(new ScriptParser.ScriptData("setContains", true, Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("set", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptSetContains(-1)));
		functions.add(new ScriptParser.ScriptData("setSize", true, Expression.DataType.INTEGER, List.of(new ScriptParser.ScriptParameter("set", true, null)), false, new ScriptSetSize(-1)));
		functions.add(new ScriptParser.ScriptData("setAdd", false, null, List.of(new ScriptParser.ScriptParameter("set", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptSetAdd(-1)));
		functions.add(new ScriptParser.ScriptData("setRemove", false, null, List.of(new ScriptParser.ScriptParameter("set", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptSetRemove(-1)));
		functions.add(new ScriptParser.ScriptData("setClear", false, null, List.of(new ScriptParser.ScriptParameter("set", true, null)), false, new ScriptSetClear(-1)));
		functions.add(new ScriptParser.ScriptData("setCopy", true, Expression.DataType.SET, List.of(new ScriptParser.ScriptParameter("set", true, null)), false, new ScriptSetCopy(-1)));
		functions.add(new ScriptParser.ScriptData("setUnion", true, Expression.DataType.SET, List.of(new ScriptParser.ScriptParameter("setOne", true, null), new ScriptParser.ScriptParameter("setTwo", true, null)), false, new ScriptSetUnion(-1)));
		functions.add(new ScriptParser.ScriptData("setIntersect", true, Expression.DataType.SET, List.of(new ScriptParser.ScriptParameter("setOne", true, null), new ScriptParser.ScriptParameter("setTwo", true, null)), false, new ScriptSetIntersect(-1)));
		functions.add(new ScriptParser.ScriptData("setDifference", true, Expression.DataType.SET, List.of(new ScriptParser.ScriptParameter("setOne", true, null), new ScriptParser.ScriptParameter("setTwo", true, null)), false, new ScriptSetDifference(-1)));
		functions.add(new ScriptParser.ScriptData("setSymmetricDifference", true, Expression.DataType.SET, List.of(new ScriptParser.ScriptParameter("setOne", true, null), new ScriptParser.ScriptParameter("setTwo", true, null)), false, new ScriptSetSymmetricDifference(-1)));
		functions.add(new ScriptParser.ScriptData("randomFromSet", true, null, List.of(new ScriptParser.ScriptParameter("set", true, null)), false, new ScriptRandomValueFromSet(-1)));
		functions.add(new ScriptParser.ScriptData("listContains", true, Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("list", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptListContains(-1)));
		functions.add(new ScriptParser.ScriptData("listSize", true, Expression.DataType.INTEGER, List.of(new ScriptParser.ScriptParameter("list", true, null)), false, new ScriptListSize(-1)));
		functions.add(new ScriptParser.ScriptData("listAdd", false, null, List.of(new ScriptParser.ScriptParameter("list", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptListAdd(-1)));
		functions.add(new ScriptParser.ScriptData("listAddIndex", false, null, List.of(new ScriptParser.ScriptParameter("list", true, null), new ScriptParser.ScriptParameter("index", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptListAddIndex(-1)));
		functions.add(new ScriptParser.ScriptData("listRemove", false, null, List.of(new ScriptParser.ScriptParameter("list", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptListRemove(-1)));
		functions.add(new ScriptParser.ScriptData("listRemoveIndex", false, null, List.of(new ScriptParser.ScriptParameter("list", true, null), new ScriptParser.ScriptParameter("index", true, null)), false, new ScriptListRemoveIndex(-1)));
		functions.add(new ScriptParser.ScriptData("listIndexOf", false, null, List.of(new ScriptParser.ScriptParameter("list", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptListIndexOf(-1)));
		functions.add(new ScriptParser.ScriptData("listSet", false, null, List.of(new ScriptParser.ScriptParameter("list", true, null), new ScriptParser.ScriptParameter("index", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptListIndexSet(-1)));
		functions.add(new ScriptParser.ScriptData("listGet", true, null, List.of(new ScriptParser.ScriptParameter("list", true, null), new ScriptParser.ScriptParameter("index", true, null)), false, new ScriptListIndexGet(-1)));
		functions.add(new ScriptParser.ScriptData("listClear", false, null, List.of(new ScriptParser.ScriptParameter("list", true, null)), false, new ScriptListClear(-1)));
		functions.add(new ScriptParser.ScriptData("listCopy", true, Expression.DataType.LIST, List.of(new ScriptParser.ScriptParameter("list", true, null)), false, new ScriptListCopy(-1)));
		functions.add(new ScriptParser.ScriptData("subList", true, Expression.DataType.LIST, List.of(new ScriptParser.ScriptParameter("list", true, null), new ScriptParser.ScriptParameter("start", true, null), new ScriptParser.ScriptParameter("end", true, null)), false, new ScriptSubList(-1)));
		functions.add(new ScriptParser.ScriptData("listConcat", true, Expression.DataType.LIST, List.of(new ScriptParser.ScriptParameter("listOne", true, null), new ScriptParser.ScriptParameter("listTwo", true, null)), false, new ScriptListConcat(-1)));
		functions.add(new ScriptParser.ScriptData("randomFromList", true, null, List.of(new ScriptParser.ScriptParameter("list", true, null)), false, new ScriptRandomValueFromList(-1)));
		functions.add(new ScriptParser.ScriptData("statHolderType", true, Expression.DataType.STRING, List.of(new ScriptParser.ScriptParameter("holder", true, null)), false, new ScriptStatHolderType(-1)));
		functions.add(new ScriptParser.ScriptData("isTimerActive", true, Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("timer", true, null)), false, new ScriptTimerActive(-1)));
		functions.add(new ScriptParser.ScriptData("toString", true, Expression.DataType.STRING, List.of(new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptToString(-1)));
		functions.add(new ScriptParser.ScriptData("dataType", true, Expression.DataType.STRING, List.of(new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptDataType(-1)));
		for (ScriptParser.ScriptData scriptData : functions) {
			game.data().addScript(scriptData.name(), scriptData);
		}
	}

	public static Script constant(boolean value) {
		return new ScriptExpression(-1, Expression.constant(value));
	}

	public static Script constant(int value) {
		return new ScriptExpression(-1, Expression.constant(value));
	}

	public static Script constant(float value) {
		return new ScriptExpression(-1, Expression.constant(value));
	}

	public static Script constant(String value) {
		return new ScriptExpression(-1, Expression.constant(value));
	}

	public static Script constant(Set<String> value) {
		return new ScriptExpression(-1, Expression.constant(value));
	}

	public static Script constant(Inventory value) {
		return new ScriptExpression(-1, Expression.constant(value));
	}

	public static Script constant(Noun value) {
		return new ScriptExpression(-1, Expression.constant(value));
	}

	public static Script constant(StatHolder value) {
		return new ScriptExpression(-1, Expression.constant(value));
	}

	public record ScriptReturnData(Expression value, FlowStatementType flowStatement, ScriptErrorData error) {}

	public record ScriptErrorData(String message, int line) {}

}
