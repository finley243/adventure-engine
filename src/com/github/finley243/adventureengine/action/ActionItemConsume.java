package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.ItemConsumable;

public class ActionItemConsume extends Action {

	private final ItemConsumable item;
	
	public ActionItemConsume(ItemConsumable item) {
		super(ActionDetectionChance.NONE);
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.getInventory().removeItem(item);
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
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(phrase), context, this, null, subject, null));
		for(String effect : item.getEffects()) {
			subject.getEffectComponent().addEffect(subject.game().data().getEffect(effect));
		}
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
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
		return new MenuChoice(prompt, canChoose(subject), new String[]{"inventory", item.getName() + subject.getInventory().itemCountLabel(item)}, new String[]{"consume " + item.getName(), "eat " + item.getName(), "drink " + item.getName(), "use " + item.getName()});
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
