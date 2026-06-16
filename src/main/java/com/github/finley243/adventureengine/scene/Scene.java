package com.github.finley243.adventureengine.scene;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantBoolean;
import com.github.finley243.adventureengine.expression.ExpressionConstantString;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.script.ScriptValueHolder;

import java.util.List;

public class Scene extends GameInstanced implements ScriptValueHolder {

	public enum SceneType {
		ALL, SELECT, RANDOM
	}

	private final Condition condition;
	private final boolean once;
	private final int priority;
	private final List<SceneLine> lines;
	private final List<SceneChoice> choices;

	private final SceneType type;

	private boolean hasTriggered;
	
	public Scene(String ID, Condition condition, boolean once, int priority, List<SceneLine> lines, List<SceneChoice> choices, SceneType type) {
		// TODO - Always assigned a non-null ID (for in-line scenes, assign an "automatic" ID if none is manually specified)
		super(ID);
		this.condition = condition;
		this.once = once;
		this.priority = priority;
		this.lines = lines;
		this.choices = choices;
		this.type = type;
		this.hasTriggered = false;
	}

	public void resolveLinkedScenes(Registry<Scene> sceneRegistry) {
		for (SceneLine line : lines) {
			line.resolveLinkedScenes(sceneRegistry);
		}
		for (SceneChoice choice : choices) {
			choice.resolveLinkedScene(sceneRegistry);
		}
	}

	public boolean canChoose(ScriptRuntime scriptRuntime, Context context) {
		return (condition == null || condition.isMet(scriptRuntime, context)) && !(once && hasTriggered);
	}
	
	public List<SceneLine> getLines() {
		return lines;
	}

	public List<SceneChoice> getChoices() {
		return choices;
	}

	public SceneType getType() {
		return type;
	}

	public int getPriority() {
		return priority;
	}

	public void setTriggered() {
		hasTriggered = true;
	}

	@Override
	public Expression getScriptValue(String name, Context context) {
		return switch (name) {
			case "triggered" -> new ExpressionConstantBoolean(hasTriggered);
			case "id" -> new ExpressionConstantString(getID());
			default -> null;
		};
	}

	@Override
	public boolean setScriptValue(String name, Expression value, Context context) {
		switch (name) {
			case "triggered" -> {
				this.hasTriggered = value.getValueBoolean();
				return true;
			}
		}
		return false;
	}

	@Override
	public ScriptValueHolder getSubHolder(String name, String ID) {
		return null;
	}

	public record SceneLineResult(boolean exit, Scene redirect) {}
	
}
