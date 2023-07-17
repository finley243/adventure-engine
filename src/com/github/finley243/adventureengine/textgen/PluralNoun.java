package com.github.finley243.adventureengine.textgen;

public class PluralNoun implements Noun {

    private final Noun noun;
    private final int count;

    public PluralNoun(Noun noun, int count) {
        if (count <= 1) throw new IllegalArgumentException("PluralNoun count must be greater than 1");
        this.noun = noun;
        this.count = count;
    }

    @Override
    public String getName() {
        return LangUtils.pluralizeNoun(noun.getName());
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
    public boolean isPlural() {
        return true;
    }

    @Override
    public TextContext.Pronoun getPronoun() {
        return TextContext.Pronoun.THEY;
    }

    @Override
    public boolean forcePronoun() {
        return false;
    }

}
