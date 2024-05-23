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

	/**
	 * Begin execution of the script
	 *
	 * @param context The context with which this script will be executed
	 */
	public abstract ScriptReturnData execute(Context context);

	public static void loadNativeFunctions(Game game) {
		List<ScriptParser.ScriptData> functions = new ArrayList<>();
		functions.add(new ScriptParser.ScriptData("attributeMenu", null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("points", true, null)), false, new ScriptAttributeMenu()));
		functions.add(new ScriptParser.ScriptData("skillMenu", null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("points", true, null)), false, new ScriptSkillMenu()));
		functions.add(new ScriptParser.ScriptData("startTimer", null, List.of(new ScriptParser.ScriptParameter("timer", true, null), new ScriptParser.ScriptParameter("duration", true, null)), false, new ScriptTimerStart()));
		functions.add(new ScriptParser.ScriptData("transferItem", null, List.of(new ScriptParser.ScriptParameter("transferType", false, Expression.constant("count")), new ScriptParser.ScriptParameter("from", false, null), new ScriptParser.ScriptParameter("to", false, null), new ScriptParser.ScriptParameter("item", false, null), new ScriptParser.ScriptParameter("count", false, Expression.constant(1))), false, new ScriptTransferItem()));
		functions.add(new ScriptParser.ScriptData("sendSensoryEvent", null, List.of(new ScriptParser.ScriptParameter("phrase", false, null), new ScriptParser.ScriptParameter("phraseAudible", false, null), new ScriptParser.ScriptParameter("area", true, null), new ScriptParser.ScriptParameter("detectSelf", false, Expression.constant(true))), true, new ScriptSensoryEvent()));
		functions.add(new ScriptParser.ScriptData("sendBark", null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("bark", true, null)), true, new ScriptBark()));
		functions.add(new ScriptParser.ScriptData("startScene", null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("scene", true, null)), false, new ScriptScene()));
		functions.add(new ScriptParser.ScriptData("setFactionRelation", null, List.of(new ScriptParser.ScriptParameter("faction", true, null), new ScriptParser.ScriptParameter("relatedFaction", true, null), new ScriptParser.ScriptParameter("relation", true, null)), false, new ScriptFactionRelation()));
		functions.add(new ScriptParser.ScriptData("startCombat", null, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("target", true, null)), false, new ScriptCombat()));
		functions.add(new ScriptParser.ScriptData("isCombatant", Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("target", true, null)), false, new ScriptIsCombatant()));
		functions.add(new ScriptParser.ScriptData("isVisible", Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("target", true, null)), false, new ScriptIsVisible()));
		functions.add(new ScriptParser.ScriptData("randomBoolean", Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("chance", true, null)), false, new ScriptRandomBoolean()));
		functions.add(new ScriptParser.ScriptData("inventoryContains", Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("inventory", true, null), new ScriptParser.ScriptParameter("item", true, null), new ScriptParser.ScriptParameter("requireAll", false, Expression.constant(false))), false, new ScriptInventoryContains()));
		functions.add(new ScriptParser.ScriptData("round", Expression.DataType.INTEGER, List.of(new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptRound()));
		functions.add(new ScriptParser.ScriptData("scaleLinear", Expression.DataType.FLOAT, List.of(new ScriptParser.ScriptParameter("input", true, null), new ScriptParser.ScriptParameter("inputMin", true, null), new ScriptParser.ScriptParameter("inputMax", true, null), new ScriptParser.ScriptParameter("outputMin", true, null), new ScriptParser.ScriptParameter("outputMax", true, null)), false, new ScriptScaleLinear()));
		functions.add(new ScriptParser.ScriptData("scaleLog", Expression.DataType.FLOAT, List.of(new ScriptParser.ScriptParameter("input", true, null), new ScriptParser.ScriptParameter("inputMin", true, null), new ScriptParser.ScriptParameter("inputMax", true, null), new ScriptParser.ScriptParameter("outputMin", true, null), new ScriptParser.ScriptParameter("outputMax", true, null)), false, new ScriptScaleLog()));
		functions.add(new ScriptParser.ScriptData("setContains", Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("set", true, null), new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptSetContains()));
		functions.add(new ScriptParser.ScriptData("setSize", Expression.DataType.INTEGER, List.of(new ScriptParser.ScriptParameter("set", true, null)), false, new ScriptSetSize()));
		// TODO - Allow variable return type (return type depends on data type in set)
		functions.add(new ScriptParser.ScriptData("randomFromSet", null, List.of(new ScriptParser.ScriptParameter("set", true, null)), false, new ScriptRandomValueFromSet()));
		functions.add(new ScriptParser.ScriptData("statHolderType", Expression.DataType.STRING, List.of(new ScriptParser.ScriptParameter("holder", true, null)), false, new ScriptStatHolderType()));
		functions.add(new ScriptParser.ScriptData("isTimerActive", Expression.DataType.BOOLEAN, List.of(new ScriptParser.ScriptParameter("timer", true, null)), false, new ScriptTimerActive()));
		functions.add(new ScriptParser.ScriptData("toString", Expression.DataType.STRING, List.of(new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptToString()));
		functions.add(new ScriptParser.ScriptData("dataType", Expression.DataType.STRING, List.of(new ScriptParser.ScriptParameter("value", true, null)), false, new ScriptDataType()));
		for (ScriptParser.ScriptData scriptData : functions) {
			game.data().addScript(scriptData.name(), scriptData);
		}
	}

	public static Script constant(boolean value) {
		return new ScriptExpression(Expression.constant(value));
	}

	public static Script constant(int value) {
		return new ScriptExpression(Expression.constant(value));
	}

	public static Script constant(float value) {
		return new ScriptExpression(Expression.constant(value));
	}

	public static Script constant(String value) {
		return new ScriptExpression(Expression.constant(value));
	}

	public static Script constant(Set<String> value) {
		return new ScriptExpression(Expression.constant(value));
	}

	public static Script constant(Inventory value) {
		return new ScriptExpression(Expression.constant(value));
	}

	public static Script constant(Noun value) {
		return new ScriptExpression(Expression.constant(value));
	}

	public static Script constant(StatHolder value) {
		return new ScriptExpression(Expression.constant(value));
	}

	public record ScriptReturnData(Expression value, FlowStatementType flowStatement, String error) {}

}
