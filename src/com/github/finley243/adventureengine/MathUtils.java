package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;

public class MathUtils {

    public static float chanceLinear(int value, int valueMin, int valueMax, float chanceMin, float chanceMax) {
        return chanceMin + ((chanceMax - chanceMin) / (valueMax - valueMin)) * (value - valueMin);
    }

    // Chance is higher if value1 is higher than value2
    public static float chanceLinearContest(int value1, int value1Min, int value1Max, int value2, int value2Min, int value2Max, float ratio, float chanceMin, float chanceMax) {
        int difference = (int) (value1*ratio - value2);
        int differenceMin = (int) (value1Min*ratio - value2Max);
        int differenceMax = (int) (value1Max*ratio - value2Min);
        return chanceLinear(difference, differenceMin, differenceMax, chanceMin, chanceMax);
    }

    public static float chanceLinearSkill(Actor subject, Actor.Skill skill, float chanceMin, float chanceMax) {
        return chanceLinear(subject.getSkill(skill), Actor.SKILL_MIN, Actor.SKILL_MAX, chanceMin, chanceMax);
    }

    public static float chanceLinearAttribute(Actor subject, Actor.Attribute attribute, float chanceMin, float chanceMax) {
        return chanceLinear(subject.getAttribute(attribute), Actor.ATTRIBUTE_MIN, Actor.ATTRIBUTE_MAX, chanceMin, chanceMax);
    }

    public static float chanceLinearSkillContest(Actor subject1, Actor.Skill skill1, Actor subject2, Actor.Skill skill2, float ratio, float chanceMin, float chanceMax) {
        return chanceLinearContest(subject1.getSkill(skill1), Actor.SKILL_MIN, Actor.SKILL_MAX, subject2.getSkill(skill2), Actor.SKILL_MIN, Actor.SKILL_MAX, ratio, chanceMin, chanceMax);
    }

    public static float chanceLinearAttributeContest(Actor subject1, Actor.Attribute attribute1, Actor subject2, Actor.Attribute attribute2, float ratio, float chanceMin, float chanceMax) {
        return chanceLinearContest(subject1.getAttribute(attribute1), Actor.ATTRIBUTE_MIN, Actor.ATTRIBUTE_MAX, subject2.getAttribute(attribute2), Actor.ATTRIBUTE_MIN, Actor.ATTRIBUTE_MAX, ratio, chanceMin, chanceMax);
    }

    public static float chanceLinearSkillAttributeContest(Actor subject1, Actor.Skill skill, Actor subject2, Actor.Attribute attribute, float ratio, float chanceMin, float chanceMax) {
        return chanceLinearContest(subject1.getSkill(skill), Actor.SKILL_MIN, Actor.SKILL_MAX, subject2.getAttribute(attribute), Actor.ATTRIBUTE_MIN, Actor.ATTRIBUTE_MAX, ratio, chanceMin, chanceMax);
    }

    public static float chanceLinearAttributeSkillContest(Actor subject1, Actor.Attribute attribute, Actor subject2, Actor.Skill skill, float ratio, float chanceMin, float chanceMax) {
        return chanceLinearContest(subject1.getAttribute(attribute), Actor.ATTRIBUTE_MIN, Actor.ATTRIBUTE_MAX, subject2.getSkill(skill), Actor.SKILL_MIN, Actor.SKILL_MAX, ratio, chanceMin, chanceMax);
    }

    public static int bound(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static float bound(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

}
