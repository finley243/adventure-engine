package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {

    public static float chanceLinear(int value, int valueMin, int valueMax, float chanceMin, float chanceMax) {
        return ((float) (value - valueMin) / (float) (valueMax - valueMin)) * (chanceMax - chanceMin) + chanceMin;
    }

    public static float chanceLog(int value, int valueMin, int valueMax, float chanceMin, float chanceMax) {
        return linearToLog(value, valueMin, valueMax, chanceMin, chanceMax);
    }

    public static float linearToLog(float linValue, float linMin, float linMax, float logMin, float logMax) {
        //return (float) Math.exp(((linValue - linMin)/(linMax - linMin)) * (Math.log(logMax) - Math.log(logMin)) + Math.log(logMin));
        return (float) (((Math.log(linValue) - Math.log(linMin))/(Math.log(linMax) - Math.log(linMin))) * (logMax - logMin) + logMin);
    }

    // Chance is higher if value1 is higher than value2
    // Ratio greater than 1.0 places greater weight on value1, lower than 1.0 places greater weight on value2
    public static float chanceLinearContest(int value1, int value1Min, int value1Max, int value2, int value2Min, int value2Max, float ratio, float chanceMin, float chanceMax) {
        int difference = (int) (value1*ratio - value2);
        int differenceMin = (int) (value1Min*ratio - value2Max);
        int differenceMax = (int) (value1Max*ratio - value2Min);
        return chanceLinear(difference, differenceMin, differenceMax, chanceMin, chanceMax);
    }

    public static float chanceLinearSkill(Actor subject, String skill, float chanceMin, float chanceMax, Context context) {
        return chanceLinear(subject.getSkill(skill, context), Actor.SKILL_MIN, Actor.SKILL_MAX, chanceMin, chanceMax);
    }

    public static float chanceLogSkill(Actor subject, String skill, float chanceMin, float chanceMax, Context context) {
        return chanceLog(subject.getSkill(skill, context), Actor.SKILL_MIN, Actor.SKILL_MAX, chanceMin, chanceMax);
    }

    public static float chanceLinearSkillInverted(Actor subject, String skill, float chanceMin, float chanceMax, Context context) {
        return chanceMax + chanceMin - chanceLinearSkill(subject, skill, chanceMin, chanceMax, context);
    }

    public static float chanceLinearAttribute(Actor subject, String attribute, float chanceMin, float chanceMax, Context context) {
        return chanceLinear(subject.getAttribute(attribute, context), Actor.ATTRIBUTE_MIN, Actor.ATTRIBUTE_MAX, chanceMin, chanceMax);
    }

    public static float chanceLinearSkillContest(Actor subject1, String skill1, Actor subject2, String skill2, float ratio, float chanceMin, float chanceMax, Context context) {
        return chanceLinearContest(subject1.getSkill(skill1, context), Actor.SKILL_MIN, Actor.SKILL_MAX, subject2.getSkill(skill2, context), Actor.SKILL_MIN, Actor.SKILL_MAX, ratio, chanceMin, chanceMax);
    }

    public static float chanceLinearAttributeContest(Actor subject1, String attribute1, Actor subject2, String attribute2, float ratio, float chanceMin, float chanceMax, Context context) {
        return chanceLinearContest(subject1.getAttribute(attribute1, context), Actor.ATTRIBUTE_MIN, Actor.ATTRIBUTE_MAX, subject2.getAttribute(attribute2, context), Actor.ATTRIBUTE_MIN, Actor.ATTRIBUTE_MAX, ratio, chanceMin, chanceMax);
    }

    public static float chanceLinearSkillAttributeContest(Actor subject1, String skill, Actor subject2, String attribute, float ratio, float chanceMin, float chanceMax, Context context) {
        return chanceLinearContest(subject1.getSkill(skill, context), Actor.SKILL_MIN, Actor.SKILL_MAX, subject2.getAttribute(attribute, context), Actor.ATTRIBUTE_MIN, Actor.ATTRIBUTE_MAX, ratio, chanceMin, chanceMax);
    }

    public static float chanceLinearAttributeSkillContest(Actor subject1, String attribute, Actor subject2, String skill, float ratio, float chanceMin, float chanceMax, Context context) {
        return chanceLinearContest(subject1.getAttribute(attribute, context), Actor.ATTRIBUTE_MIN, Actor.ATTRIBUTE_MAX, subject2.getSkill(skill, context), Actor.SKILL_MIN, Actor.SKILL_MAX, ratio, chanceMin, chanceMax);
    }

    public static boolean randomCheck(float chance) {
        return ThreadLocalRandom.current().nextFloat() < chance;
    }

    public static int bound(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static float bound(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Returns by how much a value extends beyond a range (in either direction)
     * @param value Value to compare
     * @param min Lower bound of range (inclusive)
     * @param max Upper bound of range (inclusive)
     * @return Amount above or below the range (0 if value is contained in range)
     */
    public static int differenceFromRange(int value, int min, int max) {
        if (value > max) {
            return value - max;
        } else if (value < min) {
            return min - value;
        } else {
            return 0;
        }
    }

    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static <T> T selectRandomFromSet(Set<T> set) {
        if (set.isEmpty()) return null;
        return selectRandomFromList(new ArrayList<>(set));
    }

    public static <T> T selectRandomFromList(List<T> list) {
        if (list.isEmpty()) return null;
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

}
