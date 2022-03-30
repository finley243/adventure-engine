package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

public class EffectSkill extends Effect {

	private final Actor.Skill skill;
	private final int amount;

	public EffectSkill(int duration, boolean manualRemoval, Actor.Skill skill, int amount) {
		super(duration, manualRemoval);
		this.skill = skill;
		this.amount = amount;
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
	public Effect generate() {
		return new EffectSkill(this.turnsRemaining, manualRemoval, skill, amount);
	}

}
