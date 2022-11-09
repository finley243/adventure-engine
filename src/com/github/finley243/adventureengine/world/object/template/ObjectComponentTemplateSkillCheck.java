package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.List;

public class ObjectComponentTemplateSkillCheck extends ObjectComponentTemplate {

    private final List<SkillCheck> skillChecks;

    public ObjectComponentTemplateSkillCheck(Game game, String ID, boolean startEnabled, List<SkillCheck> skillChecks) {
        super(game, ID, startEnabled);
        this.skillChecks = skillChecks;
    }

    public List<SkillCheck> getSkillChecks() {
        return skillChecks;
    }

    public static class SkillCheck {
        public final Actor.Skill skill;
        public final int level;
        public final String prompt;
        public final String phrase;

        public SkillCheck(Actor.Skill skill, int level, String prompt, String phrase) {
            this.skill = skill;
            this.level = level;
            this.prompt = prompt;
            this.phrase = phrase;
        }
    }

}
