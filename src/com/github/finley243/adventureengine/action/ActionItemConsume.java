package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemConsumable;

public class ActionItemConsume extends Action {

	private final ItemConsumable item;
	
	public ActionItemConsume(ItemConsumable item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.inventory().removeItem(item);
		Context context = new Context(subject, false, item, true);
		switch(item.getConsumableType()) {
		case DRINK:
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("drink"), context, this, subject));
			break;
		case FOOD:
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("eat"), context, this, subject));
			break;
		case OTHER:
		default:
			Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("consume"), context, this, subject));
			break;
		}
		for(Effect effect : item.getEffects()) {
			subject.effectComponent().addEffect(effect.generate());
		}
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		String prompt;
		String fullPrompt;
		switch(item.getConsumableType()) {
		case DRINK:
			prompt = "Drink";
			fullPrompt = "Drink " + item.getFormattedName(true);
			break;
		case FOOD:
			prompt = "Eat";
			fullPrompt = "Eat " + item.getFormattedName(true);
			break;
		case OTHER:
		default:
			prompt = "Use";
			fullPrompt = "Use " + item.getFormattedName(true);
			break;
		}
		int count = subject.inventory().itemCountWithID(item.getStatsID());
		return new MenuData(prompt, fullPrompt, canChoose(subject), new String[]{"inventory", item.getName() + (count > 1 ? "(" + count + ")" : "")});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionItemConsume)) {
            return false;
        } else {
            ActionItemConsume other = (ActionItemConsume) o;
            return other.item == this.item;
        }
    }

}
