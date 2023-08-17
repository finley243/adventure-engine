package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.ItemConsumable;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextContext;

public class ActionItemConsume extends Action {

	private final ItemConsumable item;
	
	public ActionItemConsume(ItemConsumable item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.getInventory().removeItem(item);
		TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", item).build());
		String phrase = item.getConsumePhrase();
		subject.game().eventQueue().addToEnd(new SensoryEvent(subject.getArea(), Phrases.get(phrase), context, this, null, subject, null));
		for (String effect : item.getEffects()) {
			subject.getEffectComponent().addEffect(effect);
		}
		subject.game().eventQueue().addToEnd(new CompleteActionEvent(subject, this, repeatActionCount));
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataInventory(item);
	}

	@Override
	public String getPrompt(Actor subject) {
		return item.getConsumePrompt();
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
