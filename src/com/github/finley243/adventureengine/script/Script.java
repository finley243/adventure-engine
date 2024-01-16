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

	/**
	 * Begin execution of the script
	 *
	 * @param context
	 */
	public abstract ScriptReturnData execute(Context context);

	public static void loadBuiltInFunctions(Game game) {
		List<ScriptParser.ScriptData> functions = new ArrayList<>();
		functions.add(new ScriptParser.ScriptData("attributeMenu", null, Set.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("points", true, null)), new ScriptAttributeMenu()));
		functions.add(new ScriptParser.ScriptData("skillMenu", null, Set.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("points", true, null)), new ScriptSkillMenu()));
		functions.add(new ScriptParser.ScriptData("startTimer", null, Set.of(new ScriptParser.ScriptParameter("timer", true, null), new ScriptParser.ScriptParameter("duration", true, null)), new ScriptTimerStart()));
		functions.add(new ScriptParser.ScriptData("transferItem", null, Set.of(new ScriptParser.ScriptParameter("transferType", false, Expression.constant("count")), new ScriptParser.ScriptParameter("from", false, null), new ScriptParser.ScriptParameter("to", false, null), new ScriptParser.ScriptParameter("item", false, null), new ScriptParser.ScriptParameter("count", false, Expression.constant(1))), new ScriptTransferItem()));
		functions.add(new ScriptParser.ScriptData("sendSensoryEvent", null, Set.of(new ScriptParser.ScriptParameter("phrase", false, null), new ScriptParser.ScriptParameter("phraseAudible", false, null), new ScriptParser.ScriptParameter("area", true, null), new ScriptParser.ScriptParameter("detectSelf", false, Expression.constant(true))), new ScriptSensoryEvent()));
		functions.add(new ScriptParser.ScriptData("sendBark", null, Set.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("bark", true, null)), new ScriptBark()));
		functions.add(new ScriptParser.ScriptData("startScene", null, Set.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("scene", true, null)), new ScriptScene()));
		functions.add(new ScriptParser.ScriptData("setFactionRelation", null, Set.of(new ScriptParser.ScriptParameter("faction", true, null), new ScriptParser.ScriptParameter("relatedFaction", true, null), new ScriptParser.ScriptParameter("relation", true, null)), new ScriptFactionRelation()));
		functions.add(new ScriptParser.ScriptData("startCombat", null, Set.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("target", true, null)), new ScriptCombat()));
		functions.add(new ScriptParser.ScriptData("isCombatant", null, Set.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("target", true, null)), new ScriptIsCombatant()));
		functions.add(new ScriptParser.ScriptData("isVisible", null, Set.of(new ScriptParser.ScriptParameter("actor", true, null), new ScriptParser.ScriptParameter("target", true, null)), new ScriptIsVisible()));
		functions.add(new ScriptParser.ScriptData("randomBoolean", null, Set.of(new ScriptParser.ScriptParameter("chance", true, null)), new ScriptRandomBoolean()));
		functions.add(new ScriptParser.ScriptData("inventoryContains", null, Set.of(new ScriptParser.ScriptParameter("inventory", true, null), new ScriptParser.ScriptParameter("item", true, null), new ScriptParser.ScriptParameter("requireAll", false, Expression.constant(false))), new ScriptInventoryContains()));
		functions.add(new ScriptParser.ScriptData("round", null, Set.of(new ScriptParser.ScriptParameter("value", true, null)), new ScriptRound()));
		functions.add(new ScriptParser.ScriptData("scaleLinear", null, Set.of(new ScriptParser.ScriptParameter("input", true, null), new ScriptParser.ScriptParameter("inputMin", true, null), new ScriptParser.ScriptParameter("inputMax", true, null), new ScriptParser.ScriptParameter("outputMin", true, null), new ScriptParser.ScriptParameter("outputMax", true, null)), new ScriptScaleLinear()));
		functions.add(new ScriptParser.ScriptData("scaleLog", null, Set.of(new ScriptParser.ScriptParameter("input", true, null), new ScriptParser.ScriptParameter("inputMin", true, null), new ScriptParser.ScriptParameter("inputMax", true, null), new ScriptParser.ScriptParameter("outputMin", true, null), new ScriptParser.ScriptParameter("outputMax", true, null)), new ScriptScaleLog()));
		functions.add(new ScriptParser.ScriptData("setContains", null, Set.of(new ScriptParser.ScriptParameter("set", true, null), new ScriptParser.ScriptParameter("value", true, null)), new ScriptSetContains()));
		functions.add(new ScriptParser.ScriptData("setSize", null, Set.of(new ScriptParser.ScriptParameter("set", true, null)), new ScriptSetSize()));
		functions.add(new ScriptParser.ScriptData("randomFromSet", null, Set.of(new ScriptParser.ScriptParameter("set", true, null)), new ScriptRandomValueFromSet()));
		functions.add(new ScriptParser.ScriptData("statHolderType", null, Set.of(new ScriptParser.ScriptParameter("holder", true, null)), new ScriptStatHolderType()));
		functions.add(new ScriptParser.ScriptData("isTimerActive", null, Set.of(new ScriptParser.ScriptParameter("timer", true, null)), new ScriptTimerActive()));
		functions.add(new ScriptParser.ScriptData("toString", null, Set.of(new ScriptParser.ScriptParameter("value", true, null)), new ScriptToString()));
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

	public record ScriptReturnData(Expression value, boolean isReturn, boolean isBreak, String error) {}

}
