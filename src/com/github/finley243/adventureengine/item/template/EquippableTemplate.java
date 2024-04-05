package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EquippableTemplate extends ItemTemplate {
	
	private final Set<Set<String>> slots;
	private final List<String> equippedEffects;
	private final List<ActionCustom.CustomActionHolder> equippedActions;
	
	public EquippableTemplate(Game game, String ID, String name, Scene description, Map<String, Script> scripts, List<ItemComponentTemplate> components, List<ActionCustom.CustomActionHolder> customActions, int price, Set<Set<String>> slots, List<String> equippedEffects, List<ActionCustom.CustomActionHolder> equippedActions) {
		super(game, ID, name, description, scripts, components, customActions, price);
		this.slots = slots;
		this.equippedEffects = equippedEffects;
		this.equippedActions = equippedActions;
	}
	
	public Set<Set<String>> getSlots() {
		return slots;
	}

	public List<String> getEquippedEffects() {
		return equippedEffects;
	}

	public List<ActionCustom.CustomActionHolder> getEquippedActions() {
		return equippedActions;
	}

	@Override
	public Set<String> getTags() {
		Set<String> tags = new HashSet<>();
		tags.add("apparel");
		return tags;
	}
	
}
