package com.github.finley243.adventureengine.textgen;

import com.github.finley243.adventureengine.Context;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextGenTest {

    @Test
    void testTextGen() {
        Noun nounCat = createNoun("cat", true, false, 1, TextContext.Pronoun.IT);
        Noun nounBag = createNoun("bag", false, false, 1, TextContext.Pronoun.IT);
        String phraseTemplate = "$nounCat $is in $nounBag.";
        Context context = null;
        TextContext textContext = new TextContext(Map.of(), Map.of("nounCat", nounCat, "nounBag", nounBag));
        String generatedResult = TextGen.generate(phraseTemplate, context, textContext);
        assertEquals("The cat is in a bag.", generatedResult, "Generated text does not match expected result: " + generatedResult + " (expected: The cat is in a bag.)");
        String generatedResultRepeat = TextGen.generate(phraseTemplate, context, textContext);
        assertEquals("It is in it.", generatedResultRepeat, "Repeated generated text does not match expected result: " + generatedResultRepeat + " (expected: It is in it.)");
    }

    private Noun createNoun(String name, boolean isKnown, boolean isProper, int pluralCount, TextContext.Pronoun pronoun) {
        return new Noun() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void setKnown() {}

            @Override
            public boolean isKnown() {
                return isKnown;
            }

            @Override
            public boolean isProperName() {
                return isProper;
            }

            @Override
            public int pluralCount() {
                return pluralCount;
            }

            @Override
            public TextContext.Pronoun getPronoun() {
                return pronoun;
            }
        };
    }

}
