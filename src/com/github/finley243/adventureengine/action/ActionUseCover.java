package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectCover;

public class ActionUseCover implements Action {

	private ObjectCover cover;
	
	public ActionUseCover(ObjectCover cover) {
		this.cover = cover;
	}
	
	@Override
	public void choose(Actor subject) {
		cover.setUser(subject);
		subject.startUsingObject(cover);
		Context context = new Context(subject, false, cover, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("takeCover"), context));
	}

	@Override
	public String getPrompt() {
		return "Take cover behind " + cover.getFormattedName(false);
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int actionPoints() {
		return 1;
	}
	
	@Override
	public String[] getMenuStructure() {
		return new String[] {"World", LangUtils.titleCase(cover.getName())};
	}
	
	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}

}
