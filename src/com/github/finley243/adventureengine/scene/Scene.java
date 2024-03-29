package com.github.finley243.adventureengine.scene;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantBoolean;
import com.github.finley243.adventureengine.expression.ExpressionConstantString;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.stat.StatHolder;

import java.util.ArrayList;
import java.util.List;

public class Scene extends GameInstanced implements StatHolder {

	public enum SceneType {
		SEQUENTIAL, SELECTOR, RANDOM
	}

	private final Condition condition;
	private final boolean once;
	private final int priority;
	private final List<SceneLine> lines;
	private final List<SceneChoice> choices;

	private final SceneType type;

	private boolean hasTriggered;
	
	public Scene(Game game, String ID, Condition condition, boolean once, int priority, List<SceneLine> lines, List<SceneChoice> choices, SceneType type) {
		// TODO - Always assigned a non-null ID (for in-line scenes, assign an "automatic" ID if none is manually specified)
		super(game, ID);
		this.condition = condition;
		this.once = once;
		this.priority = priority;
		this.lines = lines;
		this.choices = choices;
		this.type = type;
		this.hasTriggered = false;
	}

	public boolean canChoose(Context context) {
		return (condition == null || condition.isMet(context)) && !(once && hasTriggered);
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
	public Expression getStatValue(String name, Context context) {
		return switch (name) {
			case "triggered" -> new ExpressionConstantBoolean(hasTriggered);
			case "id" -> new ExpressionConstantString(getID());
			default -> null;
		};
	}

	@Override
	public boolean setStatValue(String name, Expression value, Context context) {
		switch (name) {
			case "triggered" -> {
				this.hasTriggered = value.getValueBoolean();
				return true;
			}
		}
		return false;
	}

	@Override
	public StatHolder getSubHolder(String name, String ID) {
		return null;
	}

	public void loadState(SaveData saveData) {
		if ("has_triggered".equals(saveData.getParameter())) {
			this.hasTriggered = saveData.getValueBoolean();
		}
	}

	public List<SaveData> saveState() {
		List<SaveData> state = new ArrayList<>();
		if (hasTriggered) {
			state.add(new SaveData(SaveData.DataType.SCENE, this.getID(), "has_triggered", hasTriggered));
		}
		return state;
	}

	public record SceneLineResult(boolean exit, String redirect) {}
	
}
