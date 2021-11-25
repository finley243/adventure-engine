package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.Item;

public class ActionItemTake implements Action {

	private final Item item;
	
	public ActionItemTake(Item item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.getArea().removeObject(item);
		subject.inventory().addItem(item);
		Context context = new Context(subject, false, item, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("pickUp"), context, this, subject));
	}

	@Override
	public boolean canChoose(Actor subject) {
		return true;
	}
	
	@Override
	public String getPrompt() {
		return "Take " + item.getFormattedName(false);
	}
	
	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}
	
	@Override
	public boolean usesAction() {
		return false;
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
		return new MenuDataWorldObject("Take", canChoose(subject), item);
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionItemTake)) {
            return false;
        } else {
            ActionItemTake other = (ActionItemTake) o;
            return other.item == this.item;
        }
    }
	
}
