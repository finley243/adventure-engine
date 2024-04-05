package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.script.Script;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConsumableTemplate extends ItemTemplate {
	
	private final String consumePrompt;
	private final String consumePhrase;
	private final List<String> effects;
	
	public ConsumableTemplate(Game game, String ID, String name, Scene description, Map<String, Script> scripts, List<ItemComponentTemplate> components, List<ActionCustom.CustomActionHolder> customActions, int price, String consumePrompt, String consumePhrase, List<String> effects) {
		super(game, ID, name, description, scripts, components, customActions, price);
		this.consumePrompt = consumePrompt;
		this.consumePhrase = consumePhrase;
		this.effects = effects;
	}
	
	public String getConsumePrompt() {
		return consumePrompt;
	}

	public String getConsumePhrase() {
		return consumePhrase;
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
