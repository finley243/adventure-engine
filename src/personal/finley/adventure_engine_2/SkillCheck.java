package personal.finley.adventure_engine_2;

import personal.finley.adventure_engine_2.actor.Actor;

public class SkillCheck {

	// If true, success is guaranteed if skill is above value. If false, it is randomized.
	private boolean isHardCheck;
	
	private int value;
	
	public SkillCheck() {
		
	}
	
	public boolean check(Actor subject) {
		return true;
	}
	
}
