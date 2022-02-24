package com.github.finley243.adventureengine.textgen;

import com.github.finley243.adventureengine.world.Noun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiNoun implements Noun {

    private final List<? extends Noun> nouns;

    public MultiNoun(List<? extends Noun> nouns) {
        if(nouns.isEmpty()) throw new IllegalArgumentException("MultiNoun cannot have empty noun list");
        this.nouns = nouns;
    }

    @Override
    public String getName() {
        return getFormattedName(false);
    }

    @Override
    public String getFormattedName(boolean indefinite) {
        Map<String, Integer> uniqueNames = new HashMap<>();
        for(Noun noun : nouns) {
            String name = noun.getName();
            if(uniqueNames.containsKey(name)) {
                uniqueNames.put(name, uniqueNames.get(name) + 1);
            } else {
                uniqueNames.put(name, 1);
            }
        }
        StringBuilder name = new StringBuilder();
        List<String> uniqueNamesList = new ArrayList<>(uniqueNames.keySet());
        for(int i = 0; i < uniqueNamesList.size(); i++) {
            if(i != 0 && i == uniqueNamesList.size() - 1) {
                if(uniqueNamesList.size() > 2) {
                    name.append(",");
                }
                name.append(" and ");
            } else if(i > 0) {
                name.append(", ");
            }
            int count = uniqueNames.get(uniqueNamesList.get(i));
            if(count > 1) {
                name.append(count).append(" ").append(LangUtils.pluralizeNoun(uniqueNamesList.get(i)));
            } else {
                name.append(LangUtils.addArticle(uniqueNamesList.get(i), indefinite));
            }
        }
        return name.toString();
    }

    @Override
    public boolean isProperName() {
        return false;
    }

    @Override
    public Context.Pronoun getPronoun() {
        if(nouns.size() == 1) {
            return Context.Pronoun.IT;
        }
        return Context.Pronoun.THEY;
    }

}
