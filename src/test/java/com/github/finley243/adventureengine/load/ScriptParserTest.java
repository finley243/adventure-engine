package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.script.parse.ScriptLexer;
import com.github.finley243.adventureengine.script.parse.ScriptToken;
import com.github.finley243.adventureengine.script.parse.ScriptTokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ScriptParserTest {

    @Test
    public void testTokenizer() {
        ScriptLexer lexer = new ScriptLexer();
        String scriptText = "\"test string\"";
        List<ScriptToken> tokens = lexer.parseToTokens(scriptText, "fileName");
        Assertions.assertEquals(1, tokens.size());
        Assertions.assertEquals(ScriptTokenType.STRING, tokens.getFirst().type());
        scriptText = "1.2f + 2 == true";
        tokens = lexer.parseToTokens(scriptText, "fileName");
        Assertions.assertEquals(5, tokens.size());
        Assertions.assertEquals(ScriptTokenType.FLOAT, tokens.get(0).type());
        Assertions.assertEquals(ScriptTokenType.PLUS, tokens.get(1).type());
        Assertions.assertEquals(ScriptTokenType.INTEGER, tokens.get(2).type());
        Assertions.assertEquals(ScriptTokenType.EQUAL, tokens.get(3).type());
        Assertions.assertEquals(ScriptTokenType.BOOLEAN_TRUE, tokens.get(4).type());
    }

}
