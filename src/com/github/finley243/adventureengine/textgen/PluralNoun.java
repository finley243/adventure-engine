package com.github.finley243.adventureengine.textgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluralNoun implements Noun {

    private final Noun noun;
    private final int count;
    private boolean isKnown;

    public PluralNoun(Noun noun, int count) {
        if(count <= 1) throw new IllegalArgumentException("PluralNoun count must be greater than 1");
        this.noun = noun;
        this.count = count;
    }

    @Override
    public String getName() {
        return LangUtils.pluralizeNoun(noun.getName());
    }

    @Override
    public String getFormattedName() {
        if(isKnown()) {
            return "the " + getName();
        } else {
            return count + " " + getName();
        }
    }

    @Override
    public void setKnown() {
        noun.setKnown();
    }

    @Override
    public boolean isKnown() {
        return noun.isKnown();
    }

    @Override
    public boolean isProperName() {
        return false;
    }

    @Override
    public Context.Pronoun getPronoun() {
        return Context.Pronoun.THEY;
    }

    @Override
    public boolean forcePronoun() {
        return false;
    }

}