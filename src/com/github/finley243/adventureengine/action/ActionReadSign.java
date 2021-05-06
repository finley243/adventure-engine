package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.object.ObjectSign;

public class ActionReadSign implements Action {

	private ObjectSign sign;
	
	public ActionReadSign(ObjectSign sign) {
		this.sign = sign;
	}
	
	@Override
	public void choose(Actor subject) {
		
	}

	@Override
	public String getChoiceName() {
		return "Read " + sign.getFormattedName();
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
		return 0;
	}

}
