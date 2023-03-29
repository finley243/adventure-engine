package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConsumableTemplate extends ItemTemplate {

	public enum ConsumableType{
		FOOD, DRINK, OTHER
	}
	
	private final ConsumableType type;
	private final List<String> effects;
	
	public ConsumableTemplate(Game game, String ID, String name, Scene description, Map<String, Script> scripts, int price, String attackType, ConsumableType type, List<String> effects) {
		super(game, ID, name, description, scripts, price, attackType);
		this.type = type;
		this.effects = effects;
	}

	@Override
	public boolean hasState() {
		return false;
	}
	
	public ConsumableType getType() {
		return type;
	}
	
	public List<String> getEffects() {
		return effects;
	}

	@Override
	public Set<String> getTags() {
		Set<String> tags = new HashSet<>();
		tags.add("consumable");
		return tags;
	}
	
}
