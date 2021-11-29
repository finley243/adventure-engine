package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;

public class ConditionSkill implements Condition {

	private final ActorReference actor;
	private final Actor.Skill skill;
	private final int value;

	public ConditionSkill(ActorReference actor, Actor.Skill skill, int value) {
		this.actor = actor;
		this.skill = skill;
		this.value = value;
	}

	@Override
	public boolean isMet(Actor subject) {
		return actor.getActor(subject).getSkill(skill) >= value;
	}

	@Override
	public String getChoiceTag() {
		return value + " " + skill.toString();
	}

}
