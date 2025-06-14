package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;
import com.github.finley243.adventureengine.textgen.Phrases;

import java.util.List;

public class ActionItemConsume extends Action {

	private final Item item;
	private final String consumePrompt;
	private final String consumePhrase;
	private final List<String> effects;
	
	public ActionItemConsume(Item item, String consumePrompt, String consumePhrase, List<String> effects) {
		this.item = item;
		this.consumePrompt = consumePrompt;
		this.consumePhrase = consumePhrase;
		this.effects = effects;
	}

	@Override
	public String getID() {
		return "item_consume";
	}

	@Override
	public Context getContext(Actor subject) {
        return new Context(subject.game(), subject, null, item);
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.getInventory().removeItem(item);
		Context context = new Context(subject.game(), subject, null, item);
		SensoryEvent.execute(new SensoryEvent(subject.getArea(), Phrases.get(consumePhrase), context, true, this, null));
		for (String effect : effects) {
			subject.getEffectComponent().addEffect(effect);
		}
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataInventory(item, subject.getInventory());
	}

	@Override
	public String getPrompt(Actor subject) {
		return consumePrompt;
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionItemConsume other)) {
            return false;
        } else {
			return other.item == this.item;
        }
    }

}
