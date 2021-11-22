package com.github.finley243.adventureengine.action;

import java.util.List;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataInventory;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.Readable;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionRead implements Action {

	private final WorldObject object;
	private final boolean isInInventory;
	
	public ActionRead(WorldObject object, boolean isInInventory) {
		if(!(object instanceof Readable)) {
			throw new IllegalArgumentException("WorldObject must implement Readable");
		}
		this.object = object;
		this.isInInventory = isInInventory;
	}
	
	@Override
	public void choose(Actor subject) {
		Context context = new Context(subject, false, object, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("read"), context, this, subject));
		if(subject instanceof ActorPlayer) {
			List<String> text = ((Readable) object).getText();
			Game.EVENT_BUS.post(new RenderTextEvent("-----------"));
			for(String line : text) {
				Game.EVENT_BUS.post(new RenderTextEvent(line));
			}
			Game.EVENT_BUS.post(new RenderTextEvent("-----------"));
		}
	}

	@Override
	public String getPrompt() {
		return "Read " + object.getFormattedName(false);
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
		return 0;
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
	public MenuData getMenuData() {
		if(isInInventory) {
			return new MenuDataInventory("Read", (Item) object);
		} else {
			return new MenuDataWorldObject("Read", object);
		}
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionRead)) {
            return false;
        } else {
            ActionRead other = (ActionRead) o;
            return other.object == this.object;
        }
    }

}
