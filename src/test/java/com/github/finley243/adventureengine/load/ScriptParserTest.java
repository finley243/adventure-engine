package com.github.finley243.adventureengine.load;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ScriptParserTest {

    @Test
    public void testTokenizer() {
        String scriptText = "\"test string\"";
        List<ScriptParser.ScriptToken> tokens = ScriptParser.parseToTokens(scriptText, "fileName");
        Assertions.assertEquals(1, tokens.size());
        Assertions.assertEquals(ScriptParser.ScriptTokenType.STRING, tokens.getFirst().type());
        scriptText = "1.2f + 2 == true";
        tokens = ScriptParser.parseToTokens(scriptText, "fileName");
        Assertions.assertEquals(5, tokens.size());
        Assertions.assertEquals(ScriptParser.ScriptTokenType.FLOAT, tokens.get(0).type());
        Assertions.assertEquals(ScriptParser.ScriptTokenType.PLUS, tokens.get(1).type());
        Assertions.assertEquals(ScriptParser.ScriptTokenType.INTEGER, tokens.get(2).type());
        Assertions.assertEquals(ScriptParser.ScriptTokenType.EQUAL, tokens.get(3).type());
        Assertions.assertEquals(ScriptParser.ScriptTokenType.BOOLEAN_TRUE, tokens.get(4).type());
    }

}
