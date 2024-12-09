package com.github.finley243.adventureengine.quest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QuestManager {

    private final Map<Quest, Set<QuestObjective>> activeObjectives;

    public QuestManager() {
        this.activeObjectives = new HashMap<>();
    }

    protected void addActiveObjective(QuestObjective objective) {
        Quest quest = objective.getParentQuest();
        if (!activeObjectives.containsKey(quest)) {
            activeObjectives.put(quest, new HashSet<>());
        }
        activeObjectives.get(quest).add(objective);
    }

    protected void removeActiveObjective(QuestObjective objective) {
        Quest quest = objective.getParentQuest();
        if (activeObjectives.containsKey(quest)) {
            activeObjectives.get(quest).remove(objective);
        }
        if (activeObjectives.get(quest).isEmpty()) {
            activeObjectives.remove(quest);
        }
    }

    public void update() {
        for (Quest quest : activeObjectives.keySet()) {
            for (QuestObjective objective : activeObjectives.get(quest)) {
                objective.update();
            }
        }
    }

}
