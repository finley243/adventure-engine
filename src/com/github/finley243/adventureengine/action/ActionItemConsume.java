package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
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
		Context context = new Context(new NounMapper().put("actor", subject).put("item", item).build());
		String phrase;
		switch(item.getConsumableType()) {
			case DRINK:
				phrase = "drink";
				break;
			case FOOD:
				phrase = "eat";
				break;
			case OTHER:
			default:
				phrase = "consume";
				break;
		}
		subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get(phrase), context, this, subject));
		for(Effect effect : item.getEffects()) {
			subject.effectComponent().addEffect(effect);
		}
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		String prompt;
		switch(item.getConsumableType()) {
		case DRINK:
			prompt = "Drink";
			break;
		case FOOD:
			prompt = "Eat";
			break;
		case OTHER:
		default:
			prompt = "Use";
			break;
		}
		return new MenuData(prompt, canChoose(subject), new String[]{"inventory", item.getName() + subject.inventory().itemCountLabel(item.getTemplate())});
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
