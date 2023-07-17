package com.github.finley243.adventureengine.textgen;

import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;

/**
 * Represents anything that can be referred to as a noun (used for text generation)
 */
public interface Noun {
	
	String getName();

	void setKnown();

	boolean isKnown();
	
	boolean isProperName();

	boolean isPlural();
	
	Pronoun getPronoun();

	boolean forcePronoun();

}
