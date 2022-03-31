package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

public class EffectSkill extends Effect {

	private final Actor.Skill skill;

	public EffectSkill(int duration, boolean manualRemoval, Actor.Skill skill, int amount) {
		super(duration, manualRemoval, amount);
		this.skill = skill;
	}
	
	@Override
	public void start(Actor target) {
		target.getSkill(skill).addMod(amount);
	}
	
	@Override
	public void end(Actor target) {
		target.getSkill(skill).addMod(-amount);
	}

	@Override
	public void eachTurn(Actor target){}

	@Override
	public boolean equals(Object o) {
		return super.equals(o) && skill == ((EffectSkill) o).skill;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + skill.hashCode();
	}

}
