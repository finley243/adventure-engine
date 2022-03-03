package com.github.finley243.adventureengine.textgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiNoun implements Noun {

    private final List<? extends Noun> nouns;
    private boolean isKnown;

    public MultiNoun(List<? extends Noun> nouns) {
        if(nouns.isEmpty()) throw new IllegalArgumentException("MultiNoun cannot have empty noun list");
        this.nouns = nouns;
    }

    @Override
    public String getName() {
        return getFormattedName();
    }

    @Override
    public String getFormattedName() {
        return getFormattedName(false);
    }

    @Override
    public String getFormattedName(boolean indefinite) {
        Map<String, Integer> uniqueNamesCount = new HashMap<>();
        Map<String, Boolean> uniqueNamesKnown = new HashMap<>();
        for(Noun noun : nouns) {
            String name = noun.getName();
            if(uniqueNamesCount.containsKey(name)) {
                uniqueNamesCount.put(name, uniqueNamesCount.get(name) + 1);
                if(!noun.isKnown()) {
                    uniqueNamesKnown.put(name, false);
                }
            } else {
                uniqueNamesCount.put(name, 1);
                uniqueNamesKnown.put(name, noun.isKnown());
            }
        }
        StringBuilder name = new StringBuilder();
        List<String> uniqueNamesList = new ArrayList<>(uniqueNamesCount.keySet());
        for(int i = 0; i < uniqueNamesList.size(); i++) {
            if(i != 0 && i == uniqueNamesList.size() - 1) {
                if(uniqueNamesList.size() > 2) {
                    name.append(",");
                }
                name.append(" and ");
            } else if(i > 0) {
                name.append(", ");
            }
            int count = uniqueNamesCount.get(uniqueNamesList.get(i));
            if(count > 1) {
                if(uniqueNamesKnown.get(uniqueNamesList.get(i))) {
                    name.append("the ");
                }
                name.append(count).append(" ").append(LangUtils.pluralizeNoun(uniqueNamesList.get(i)));
            } else {
                name.append(LangUtils.addArticle(uniqueNamesList.get(i), !uniqueNamesKnown.get(uniqueNamesList.get(i))));
            }
        }
        return name.toString();
    }

    @Override
    public void setKnown() {
        isKnown = true;
        for(Noun noun : nouns) {
            noun.setKnown();
        }
    }

    @Override
    public boolean isKnown() {
        return isKnown;
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

    @Override
    public boolean forcePronoun() {
        return false;
    }

}
