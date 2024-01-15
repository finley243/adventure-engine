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
		functions.add(new ScriptParser.ScriptData("attributeMenu", null, Set.of(new ScriptParser.ScriptParameter("actor", null), new ScriptParser.ScriptParameter("points", null)), new ScriptAttributeMenu()));
		functions.add(new ScriptParser.ScriptData("skillMenu", null, Set.of(new ScriptParser.ScriptParameter("actor", null), new ScriptParser.ScriptParameter("points", null)), new ScriptSkillMenu()));
		functions.add(new ScriptParser.ScriptData("startTimer", null, Set.of(new ScriptParser.ScriptParameter("timer", null), new ScriptParser.ScriptParameter("duration", null)), new ScriptTimerStart()));
		functions.add(new ScriptParser.ScriptData("transferItem", null, Set.of(new ScriptParser.ScriptParameter("transferType", Expression.constant("count")), new ScriptParser.ScriptParameter("from", null), new ScriptParser.ScriptParameter("to", null), new ScriptParser.ScriptParameter("item", null), new ScriptParser.ScriptParameter("count", Expression.constant(1))), new ScriptTransferItem()));
		functions.add(new ScriptParser.ScriptData("sendSensoryEvent", null, Set.of(new ScriptParser.ScriptParameter("phrase", null), new ScriptParser.ScriptParameter("phraseAudible", null), new ScriptParser.ScriptParameter("area", null), new ScriptParser.ScriptParameter("detectSelf", Expression.constant(true))), new ScriptSensoryEvent()));
		functions.add(new ScriptParser.ScriptData("startScene", null, Set.of(new ScriptParser.ScriptParameter("actor", null), new ScriptParser.ScriptParameter("scene", null)), new ScriptScene()));
		functions.add(new ScriptParser.ScriptData("setFactionRelation", null, Set.of(new ScriptParser.ScriptParameter("faction", null), new ScriptParser.ScriptParameter("relatedFaction", null), new ScriptParser.ScriptParameter("relation", null)), new ScriptFactionRelation()));
		functions.add(new ScriptParser.ScriptData("startCombat", null, Set.of(new ScriptParser.ScriptParameter("actor", null), new ScriptParser.ScriptParameter("target", null)), new ScriptCombat()));
		functions.add(new ScriptParser.ScriptData("sendBark", null, Set.of(new ScriptParser.ScriptParameter("actor", null), new ScriptParser.ScriptParameter("bark", null)), new ScriptBark()));
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
