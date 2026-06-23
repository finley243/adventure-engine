package com.github.finley243.adventureengine.script.parse;

import com.github.finley243.adventureengine.script.parse.nodes.ASTFile;
import com.github.finley243.adventureengine.script.parse.nodes.ASTFunction;
import com.github.finley243.adventureengine.script.parse.nodes.ASTParameterDefinition;
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

    @Test
    public void testASTParser() {
        ScriptASTParser parser = new ScriptASTParser();
        List<ScriptToken> tokens = List.of(new ScriptToken(ScriptTokenType.FUNCTION, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "doStuff", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_CLOSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_CLOSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.FUNCTION, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "boolean", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "isConditionMet", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "condition", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_CLOSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_CLOSE, null, 1, null, 1, 1));
        ASTParseResult parseResult = parser.parse(tokens);
        Assertions.assertTrue(parseResult.errors().isEmpty());
        Assertions.assertInstanceOf(ASTFile.class, parseResult.node());
        ASTFile file = (ASTFile) parseResult.node();
        Assertions.assertEquals(2, file.functions().size());
        Assertions.assertInstanceOf(ASTFunction.class, file.functions().get(0));
        Assertions.assertInstanceOf(ASTFunction.class, file.functions().get(1));
        ASTFunction functionOne = (ASTFunction) file.functions().get(0);
        ASTFunction functionTwo = (ASTFunction) file.functions().get(1);
        Assertions.assertEquals("doStuff", functionOne.name());
        Assertions.assertEquals(0, functionOne.parameters().size());
        Assertions.assertEquals("isConditionMet", functionTwo.name());
        Assertions.assertEquals(1, functionTwo.parameters().size());
        Assertions.assertInstanceOf(ASTParameterDefinition.class, functionTwo.parameters().get(0));
        ASTParameterDefinition parameterCondition = (ASTParameterDefinition) functionTwo.parameters().get(0);
        Assertions.assertEquals("condition", parameterCondition.name());
        Assertions.assertNull(parameterCondition.defaultValue());
    }

}
