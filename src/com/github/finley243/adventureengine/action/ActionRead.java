package com.github.finley243.adventureengine.action;

import java.util.List;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataInventory;
import com.github.finley243.adventureengine.menu.data.MenuDataNested;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.Readable;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionRead extends Action {

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
	public boolean usesAction() {
		return false;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		if(isInInventory) {
			return new MenuDataNested("Read", "Read " + object.getFormattedName(false), canChoose(subject), new String[]{"inventory", object.getName()});
		} else {
			return new MenuDataNested("Read", "Read " + object.getFormattedName(false), canChoose(subject), new String[]{object.getName()});
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
