package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataInventory;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemConsumable;

public class ActionConsume implements Action {

	private final ItemConsumable item;
	
	public ActionConsume(ItemConsumable item) {
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
			subject.addEffect(effect.generate());
		}
	}

	@Override
	public boolean canChoose(Actor subject) {
		return true;
	}

	@Override
	public String getPrompt() {
		switch(item.getConsumableType()) {
		case DRINK:
			return "Drink " + item.getFormattedName(true);
		case FOOD:
			return "Eat " + item.getFormattedName(true);
		case OTHER:
		default:
			return "Use " + item.getFormattedName(true);
		}
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean usesAction() {
		return true;
	}
	
	@Override
	public boolean canRepeat() {
		return true;
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		return false;
	}
	
	@Override
	public int actionCount() {
		return 1;
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
		return new MenuDataInventory(prompt, canChoose(subject), item);
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionConsume)) {
            return false;
        } else {
            ActionConsume other = (ActionConsume) o;
            return other.item == this.item;
        }
    }

}
