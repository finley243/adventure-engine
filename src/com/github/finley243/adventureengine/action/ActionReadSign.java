package com.github.finley243.adventureengine.action;

import java.util.List;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.event.RenderTextEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectSign;

public class ActionReadSign implements Action {

	private ObjectSign sign;
	
	public ActionReadSign(ObjectSign sign) {
		this.sign = sign;
	}
	
	@Override
	public void choose(Actor subject) {
		Context context = new Context(subject, false, sign, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("read"), context));
		if(subject instanceof ActorPlayer) {
			List<String> text = sign.getText();
			Game.EVENT_BUS.post(new RenderTextEvent("-----------"));
			for(String line : text) {
				Game.EVENT_BUS.post(new RenderTextEvent(line));
			}
			Game.EVENT_BUS.post(new RenderTextEvent("-----------"));
		}
	}

	@Override
	public String getPrompt() {
		return "Read " + sign.getFormattedName(false);
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
	public int actionCount() {
		return 1;
	}
	
	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}
	
	@Override
	public MenuData getMenuData() {
		return new MenuDataWorldObject("Read", sign);
	}

}
