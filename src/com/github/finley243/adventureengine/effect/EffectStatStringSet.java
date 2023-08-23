package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.MutableStatController;
import com.github.finley243.adventureengine.stat.MutableStatHolder;
import com.github.finley243.adventureengine.stat.StatStringSet;

import java.util.Set;

public class EffectStatStringSet extends Effect {

    private final String stat;
    private final Set<String> valuesAdd;
    private final Set<String> valuesRemove;
    private final Condition statCondition;

    public EffectStatStringSet(Game game, String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String stat, Set<String> valuesAdd, Set<String> valuesRemove, Condition statCondition) {
        super(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.stat = stat;
        this.valuesAdd = valuesAdd;
        this.valuesRemove = valuesRemove;
        this.statCondition = statCondition;
    }

    @Override
    public void start(MutableStatController controller) {
        StatStringSet moddableSet = controller.getStatStringSet(stat);
        moddableSet.addMod(new StatStringSet.StatStringSetMod(statCondition, valuesAdd, valuesRemove));
    }

    @Override
    public void end(MutableStatController controller) {
        StatStringSet moddableSet = controller.getStatStringSet(stat);
        moddableSet.removeMod(new StatStringSet.StatStringSetMod(statCondition, valuesAdd, valuesRemove));
    }

}
