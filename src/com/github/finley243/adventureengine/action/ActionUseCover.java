package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectCover;

public class ActionUseCover extends Action {

	private final ObjectCover cover;
	
	public ActionUseCover(ObjectCover cover) {
		this.cover = cover;
	}
	
	@Override
	public void choose(Actor subject) {
		cover.setUser(subject);
		subject.startUsingObject(cover);
		Context context = new Context(subject, false, cover, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("takeCover"), context, this, subject));
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataWorldObject("Take cover", "Take cover behind " + cover.getFormattedName(false), canChoose(subject), cover);
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionUseCover)) {
            return false;
        } else {
            ActionUseCover other = (ActionUseCover) o;
            return other.cover == this.cover;
        }
    }

}
