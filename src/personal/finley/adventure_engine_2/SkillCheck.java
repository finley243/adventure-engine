package personal.finley.adventure_engine_2;

import personal.finley.adventure_engine_2.actor.Actor;

public class SkillCheck {

	// If true, success is guaranteed if skill is above value. If false, it is randomized.
	private boolean isHardCheck;
	
	// If isHardCheck, this is the minimum value. If NOT isHardCheck, this is a somewhat abstract "difficulty number" (indicates a 95% chance at this skill level?).
	private int value;
	
	public SkillCheck() {
		
	}
	
	public boolean check(Actor subject) {
		return true;
	}
	
	public int getValue() {
		return value;
	}
	
}
