package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.AreaLink;

public class WeaponClass {

    private final String ID;
    private final String name;
    private final boolean isRanged;
    private final boolean isTwoHanded;
    private final Actor.Skill skill;
    private final AreaLink.DistanceCategory primaryRange;
    private final String hitPhrase;
    private final String hitPhraseRepeat;
    private final String missPhrase;
    private final String missPhraseRepeat;
    private final String limbHitPhrase;
    private final String limbHitPhraseRepeat;
    private final String limbMissPhrase;
    private final String limbMissPhraseRepeat;

    public WeaponClass(String ID, String name, boolean isRanged, boolean isTwoHanded, Actor.Skill skill, AreaLink.DistanceCategory primaryRange, String hitPhrase, String hitPhraseRepeat, String missPhrase, String missPhraseRepeat, String limbHitPhrase, String limbHitPhraseRepeat, String limbMissPhrase, String limbMissPhraseRepeat) {
        this.ID = ID;
        this.name = name;
        this.isRanged = isRanged;
        this.isTwoHanded = isTwoHanded;
        this.skill = skill;
        this.primaryRange = primaryRange;
        this.hitPhrase = hitPhrase;
        this.hitPhraseRepeat = hitPhraseRepeat;
        this.missPhrase = missPhrase;
        this.missPhraseRepeat = missPhraseRepeat;
        this.limbHitPhrase = limbHitPhrase;
        this.limbHitPhraseRepeat = limbHitPhraseRepeat;
        this.limbMissPhrase = limbMissPhrase;
        this.limbMissPhraseRepeat = limbMissPhraseRepeat;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public boolean isRanged() {
        return isRanged;
    }

    public boolean isTwoHanded() {
        return isTwoHanded;
    }

    public Actor.Skill getSkill() {
        return skill;
    }

    public AreaLink.DistanceCategory getPrimaryRange() {
        return primaryRange;
    }

    public String getHitPhrase() {
        return hitPhrase;
    }

    public String getHitPhraseRepeat() {
        return hitPhraseRepeat;
    }

    public String getMissPhrase() {
        return missPhrase;
    }

    public String getMissPhraseRepeat() {
        return missPhraseRepeat;
    }

    public String getLimbHitPhrase() {
        return limbHitPhrase;
    }

    public String getLimbHitPhraseRepeat() {
        return limbHitPhraseRepeat;
    }

    public String getLimbMissPhrase() {
        return limbMissPhrase;
    }

    public String getLimbMissPhraseRepeat() {
        return limbMissPhraseRepeat;
    }

}
