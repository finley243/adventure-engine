package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.script.nodes.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public void testFunctionDefinitions() {
        ScriptASTParser parser = new ScriptASTParser();
        // func doStuff() {}
        // func isConditionMet(condition) {}
        List<ScriptToken> tokens = List.of(new ScriptToken(ScriptTokenType.FUNCTION, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "doStuff", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_CLOSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_CLOSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.FUNCTION, null, 1, null, 1, 1),
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

    @Test
    public void testLiteralExpression() {
        ScriptASTParser parser = new ScriptASTParser();
        // Literal 5
        List<ScriptToken> tokens = List.of(
                new ScriptToken(ScriptTokenType.INTEGER, "5", 1, null, 1, 1)
        );
        ASTParseResult result = parser.parseSingleExpression(tokens);
        Assertions.assertTrue(result.errors().isEmpty());
        Assertions.assertInstanceOf(ASTLiteral.class, result.node());
        ASTLiteral literal = (ASTLiteral) result.node();
        Assertions.assertEquals(ASTLiteral.Type.INTEGER, literal.type());
        Assertions.assertEquals("5", literal.value());
    }

    @Test
    public void testBinaryOpPrecedence() {
        ScriptASTParser parser = new ScriptASTParser();
        // 1 + 2 * 3
        // Should parse as 1 + (2 * 3)
        List<ScriptToken> tokens = List.of(
                new ScriptToken(ScriptTokenType.INTEGER, "1", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PLUS, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.INTEGER, "2", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.MULTIPLY, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.INTEGER, "3", 1, null, 1, 1)
        );
        ASTParseResult result = parser.parseSingleExpression(tokens);
        Assertions.assertTrue(result.errors().isEmpty());
        Assertions.assertInstanceOf(ASTBinaryOp.class, result.node());
        ASTBinaryOp add = (ASTBinaryOp) result.node();
        Assertions.assertEquals(ASTBinaryOp.Operator.ADD, add.operator());
        Assertions.assertInstanceOf(ASTLiteral.class, add.left());
        Assertions.assertInstanceOf(ASTBinaryOp.class, add.right());
        ASTBinaryOp multiply = (ASTBinaryOp) add.right();
        Assertions.assertEquals(ASTBinaryOp.Operator.MULTIPLY, multiply.operator());
    }

    @Test
    public void testNegate() {
        ScriptASTParser parser = new ScriptASTParser();
        // -x
        List<ScriptToken> tokens = List.of(
                new ScriptToken(ScriptTokenType.MINUS, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "x", 1, null, 1, 1)
        );
        ASTParseResult result = parser.parseSingleExpression(tokens);
        Assertions.assertTrue(result.errors().isEmpty());
        Assertions.assertInstanceOf(ASTUnaryOp.class, result.node());
        ASTUnaryOp negate = (ASTUnaryOp) result.node();
        Assertions.assertEquals(ASTUnaryOp.Operator.NEGATE, negate.operator());
        Assertions.assertInstanceOf(ASTVar.class, negate.operand());
    }

    @Test
    public void testMemberAccess() {
        ScriptASTParser parser = new ScriptASTParser();
        // holder.health
        List<ScriptToken> tokens = List.of(
                new ScriptToken(ScriptTokenType.NAME, "holder", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.DOT, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "health", 1, null, 1, 1)
        );
        ASTParseResult result = parser.parseSingleExpression(tokens);
        Assertions.assertTrue(result.errors().isEmpty());
        Assertions.assertInstanceOf(ASTMemberAccess.class, result.node());
        ASTMemberAccess access = (ASTMemberAccess) result.node();
        Assertions.assertInstanceOf(ASTVar.class, access.object());
        Assertions.assertInstanceOf(ASTMemberNameStatic.class, access.name());
        ASTMemberNameStatic memberName = (ASTMemberNameStatic) access.name();
        Assertions.assertEquals("health", memberName.name());
    }

    @Test
    public void testChainedMemberAccess() {
        ScriptASTParser parser = new ScriptASTParser();
        // holder.stat1.(expr).stat2.stat3
        List<ScriptToken> tokens = List.of(
                new ScriptToken(ScriptTokenType.NAME, "holder", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.DOT, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "stat1", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.DOT, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "expr", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_CLOSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.DOT, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "stat2", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.DOT, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "stat3", 1, null, 1, 1)
        );
        ASTParseResult result = parser.parseSingleExpression(tokens);
        Assertions.assertTrue(result.errors().isEmpty());
        // (...).stat3
        Assertions.assertInstanceOf(ASTMemberAccess.class, result.node());
        ASTMemberAccess level4 = (ASTMemberAccess) result.node();
        Assertions.assertInstanceOf(ASTMemberNameStatic.class, level4.name());
        Assertions.assertEquals("stat3", ((ASTMemberNameStatic) level4.name()).name());
        // (...).stat2
        Assertions.assertInstanceOf(ASTMemberAccess.class, level4.object());
        ASTMemberAccess level3 = (ASTMemberAccess) level4.object();
        Assertions.assertInstanceOf(ASTMemberNameStatic.class, level3.name());
        Assertions.assertEquals("stat2", ((ASTMemberNameStatic) level3.name()).name());
        // (...).("expr")
        Assertions.assertInstanceOf(ASTMemberAccess.class, level3.object());
        ASTMemberAccess level2 = (ASTMemberAccess) level3.object();
        Assertions.assertInstanceOf(ASTMemberNameDynamic.class, level2.name());
        ASTMemberNameDynamic dynamicName = (ASTMemberNameDynamic) level2.name();
        Assertions.assertInstanceOf(ASTVar.class, dynamicName.expression());
        Assertions.assertEquals("expr", ((ASTVar) dynamicName.expression()).name());
        // holder.stat1
        Assertions.assertInstanceOf(ASTMemberAccess.class, level2.object());
        ASTMemberAccess level1 = (ASTMemberAccess) level2.object();
        Assertions.assertInstanceOf(ASTVar.class, level1.object());
        Assertions.assertEquals("holder", ((ASTVar) level1.object()).name());
        Assertions.assertInstanceOf(ASTMemberNameStatic.class, level1.name());
        Assertions.assertEquals("stat1", ((ASTMemberNameStatic) level1.name()).name());
    }

    @Test
    public void testFunctionCall() {
        ScriptASTParser parser = new ScriptASTParser();
        // myFunc(5, x)
        List<ScriptToken> tokens = List.of(
                new ScriptToken(ScriptTokenType.NAME, "myFunc", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.INTEGER, "5", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.COMMA, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "x", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_CLOSE, null, 1, null, 1, 1)
        );
        ASTParseResult result = parser.parseSingleExpression(tokens);
        Assertions.assertTrue(result.errors().isEmpty());
        Assertions.assertInstanceOf(ASTFunctionCall.class, result.node());
        ASTFunctionCall call = (ASTFunctionCall) result.node();
        Assertions.assertEquals("myFunc", call.name());
        Assertions.assertEquals(2, call.parameters().size());
        Assertions.assertInstanceOf(ASTParameter.class, call.parameters().get(0));
        Assertions.assertInstanceOf(ASTParameter.class, call.parameters().get(1));
        ASTParameter first = (ASTParameter) call.parameters().get(0);
        ASTParameter second = (ASTParameter) call.parameters().get(1);
        Assertions.assertNull(first.name());
        Assertions.assertNull(second.name());
        Assertions.assertInstanceOf(ASTLiteral.class, first.value());
        Assertions.assertInstanceOf(ASTVar.class, second.value());
    }

    @Test
    public void testFunctionCallNamedParameter() {
        ScriptASTParser parser = new ScriptASTParser();
        // myFunc(param = 5)
        List<ScriptToken> tokens = List.of(
                new ScriptToken(ScriptTokenType.NAME, "myFunc", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "param", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.ASSIGNMENT, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.INTEGER, "5", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_CLOSE, null, 1, null, 1, 1)
        );
        ASTParseResult result = parser.parseSingleExpression(tokens);
        Assertions.assertTrue(result.errors().isEmpty());
        Assertions.assertInstanceOf(ASTFunctionCall.class, result.node());
        ASTFunctionCall call = (ASTFunctionCall) result.node();
        Assertions.assertEquals(1, call.parameters().size());
        ASTParameter param = (ASTParameter) call.parameters().get(0);
        Assertions.assertEquals("param", param.name());
        Assertions.assertInstanceOf(ASTLiteral.class, param.value());
    }

    @Test
    public void testGlobalRef() {
        ScriptASTParser parser = new ScriptASTParser();
        // global["myVar"]
        List<ScriptToken> tokens = List.of(
                new ScriptToken(ScriptTokenType.GLOBAL, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_SQUARE_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.STRING, "myVar", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_SQUARE_CLOSE, null, 1, null, 1, 1)
        );
        ASTParseResult result = parser.parseSingleExpression(tokens);
        Assertions.assertTrue(result.errors().isEmpty());
        Assertions.assertInstanceOf(ASTGlobalRef.class, result.node());
        ASTGlobalRef globalRef = (ASTGlobalRef) result.node();
        Assertions.assertInstanceOf(ASTLiteral.class, globalRef.name());
    }

    @Test
    public void testTernaryOp() {
        ScriptASTParser parser = new ScriptASTParser();
        // a ? b : c
        List<ScriptToken> tokens = List.of(
                new ScriptToken(ScriptTokenType.NAME, "a", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.TERNARY_IF, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "b", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.COLON, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "c", 1, null, 1, 1)
        );
        ASTParseResult result = parser.parseSingleExpression(tokens);
        Assertions.assertTrue(result.errors().isEmpty());
        Assertions.assertInstanceOf(ASTTernaryOp.class, result.node());
        ASTTernaryOp conditional = (ASTTernaryOp) result.node();
        Assertions.assertInstanceOf(ASTVar.class, conditional.left());
        Assertions.assertInstanceOf(ASTVar.class, conditional.center());
        Assertions.assertInstanceOf(ASTVar.class, conditional.right());
    }

    @Test
    public void testPowerRightAssociativity() {
        ScriptASTParser parser = new ScriptASTParser();
        // a ^ b ^ c
        // Should parse as a ^ (b ^ c)
        List<ScriptToken> tokens = List.of(
                new ScriptToken(ScriptTokenType.NAME, "a", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.POWER, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "b", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.POWER, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "c", 1, null, 1, 1)
        );
        ASTParseResult result = parser.parseSingleExpression(tokens);
        Assertions.assertTrue(result.errors().isEmpty());
        Assertions.assertInstanceOf(ASTBinaryOp.class, result.node());
        ASTBinaryOp outer = (ASTBinaryOp) result.node();
        Assertions.assertEquals(ASTBinaryOp.Operator.POWER, outer.operator());
        Assertions.assertInstanceOf(ASTVar.class, outer.left());
        Assertions.assertEquals("a", ((ASTVar) outer.left()).name());
        Assertions.assertInstanceOf(ASTBinaryOp.class, outer.right());
        ASTBinaryOp inner = (ASTBinaryOp) outer.right();
        Assertions.assertEquals(ASTBinaryOp.Operator.POWER, inner.operator());
        Assertions.assertInstanceOf(ASTVar.class, inner.left());
        Assertions.assertEquals("b", ((ASTVar) inner.left()).name());
        Assertions.assertInstanceOf(ASTVar.class, inner.right());
        Assertions.assertEquals("c", ((ASTVar) inner.right()).name());
    }

    @Test
    public void testUnaryNegateBeforePower() {
        ScriptASTParser parser = new ScriptASTParser();
        // -a ^ b
        // Should parse as -(a ^ b)
        List<ScriptToken> tokens = List.of(
                new ScriptToken(ScriptTokenType.MINUS, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "a", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.POWER, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "b", 1, null, 1, 1)
        );
        ASTParseResult result = parser.parseSingleExpression(tokens);
        Assertions.assertTrue(result.errors().isEmpty());
        Assertions.assertInstanceOf(ASTUnaryOp.class, result.node());
        ASTUnaryOp negate = (ASTUnaryOp) result.node();
        Assertions.assertEquals(ASTUnaryOp.Operator.NEGATE, negate.operator());
        Assertions.assertInstanceOf(ASTBinaryOp.class, negate.operand());
        ASTBinaryOp power = (ASTBinaryOp) negate.operand();
        Assertions.assertEquals(ASTBinaryOp.Operator.POWER, power.operator());
        Assertions.assertInstanceOf(ASTVar.class, power.left());
        Assertions.assertEquals("a", ((ASTVar) power.left()).name());
        Assertions.assertInstanceOf(ASTVar.class, power.right());
        Assertions.assertEquals("b", ((ASTVar) power.right()).name());
    }

    @Test
    public void testPositionalAfterNamedParameterError() {
        ScriptASTParser parser = new ScriptASTParser();
        // myFunc(param = 5, 10)
        // Should produce an error since a positional parameter is used after a named parameter
        List<ScriptToken> tokens = List.of(
                new ScriptToken(ScriptTokenType.NAME, "myFunc", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "param", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.ASSIGNMENT, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.INTEGER, "5", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.COMMA, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.INTEGER, "10", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_CLOSE, null, 1, null, 1, 1)
        );
        ASTParseResult result = parser.parseSingleExpression(tokens);
        Assertions.assertFalse(result.errors().isEmpty());
    }

    @Test
    public void testFunctionWithIfElse() {
        ScriptASTParser parser = new ScriptASTParser();
        /*
        func myFunc(x) {
            if (x > 0) {
                return true;
            } else if (x < 0) {
                return false;
            } else {
                return false;
            }
        }
        */
        List<ScriptToken> tokens = List.of(
                new ScriptToken(ScriptTokenType.FUNCTION, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "myFunc", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "x", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_CLOSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.IF, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "x", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.GREATER, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.INTEGER, "0", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_CLOSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.RETURN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BOOLEAN_TRUE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.END_LINE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_CLOSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.ELSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.IF, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "x", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.LESS, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.INTEGER, "0", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_CLOSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.RETURN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BOOLEAN_FALSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.END_LINE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_CLOSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.ELSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.RETURN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BOOLEAN_FALSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.END_LINE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_CLOSE, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.BRACKET_CLOSE, null, 1, null, 1, 1)
        );
        ASTParseResult result = parser.parse(tokens);
        Assertions.assertTrue(result.errors().isEmpty());
        ASTFile file = (ASTFile) result.node();
        Assertions.assertEquals(1, file.functions().size());
        ASTFunction function = (ASTFunction) file.functions().getFirst();
        Assertions.assertEquals("myFunc", function.name());
        Assertions.assertEquals(1, function.parameters().size());
        ASTCompound body = (ASTCompound) function.body();
        Assertions.assertEquals(1, body.statements().size());
        Assertions.assertInstanceOf(ASTIf.class, body.statements().getFirst());
        ASTIf ifNode = (ASTIf) body.statements().getFirst();
        Assertions.assertEquals(2, ifNode.branches().size());
        Assertions.assertNotNull(ifNode.elseBranch());
        // First branch: x > 0
        ASTIfBranch firstBranch = ifNode.branches().getFirst();
        Assertions.assertInstanceOf(ASTBinaryOp.class, firstBranch.condition());
        ASTBinaryOp firstCondition = (ASTBinaryOp) firstBranch.condition();
        Assertions.assertEquals(ASTBinaryOp.Operator.GREATER, firstCondition.operator());
        // Second branch: x < 0
        ASTIfBranch secondBranch = ifNode.branches().get(1);
        Assertions.assertInstanceOf(ASTBinaryOp.class, secondBranch.condition());
        ASTBinaryOp secondCondition = (ASTBinaryOp) secondBranch.condition();
        Assertions.assertEquals(ASTBinaryOp.Operator.LESS, secondCondition.operator());
        // Each branch body has a single return statement
        ASTCompound firstBody = (ASTCompound) firstBranch.body();
        Assertions.assertEquals(1, firstBody.statements().size());
        Assertions.assertInstanceOf(ASTReturn.class, firstBody.statements().getFirst());
        ASTCompound secondBody = (ASTCompound) secondBranch.body();
        Assertions.assertEquals(1, secondBody.statements().size());
        Assertions.assertInstanceOf(ASTReturn.class, secondBody.statements().getFirst());
        ASTCompound elseBody = ifNode.elseBranch();
        Assertions.assertEquals(1, elseBody.statements().size());
        Assertions.assertInstanceOf(ASTReturn.class, elseBody.statements().getFirst());
    }

    @Test
    public void testScriptPipeline() {
        ScriptLexer lexer = new ScriptLexer();
        ScriptASTParser parser = new ScriptASTParser();
        ScriptValidator validator = new ScriptValidator();
        String scriptSource = "func testFunction(value) {" +
                "\nvar sum = value + 8;" +
                "\nreturn sum;" +
                "\n}";
        List<ScriptToken> tokens = lexer.parseToTokens(scriptSource, "testFile");
        ASTParseResult parseResult = parser.parse(tokens);
        Assertions.assertTrue(parseResult.errors().isEmpty());
        List<ASTFile> fileList = new ArrayList<>();
        fileList.add((ASTFile) parseResult.node());
        List<CompileError> validatorErrors = validator.validate(fileList, Set.of());
        Assertions.assertTrue(validatorErrors.isEmpty(), validatorErrors.toString());
    }

    @Test
    public void testChainedFunctionCall() {
        ScriptASTParser parser = new ScriptASTParser();
        // x::myFunc(5)
        // Should parse as myFunc(x, 5)
        List<ScriptToken> tokens = List.of(
                new ScriptToken(ScriptTokenType.NAME, "x", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.DOUBLE_COLON, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "myFunc", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.INTEGER, "5", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_CLOSE, null, 1, null, 1, 1)
        );
        ASTParseResult result = parser.parseSingleExpression(tokens);
        Assertions.assertTrue(result.errors().isEmpty());
        Assertions.assertInstanceOf(ASTFunctionCall.class, result.node());
        ASTFunctionCall call = (ASTFunctionCall) result.node();
        Assertions.assertEquals("myFunc", call.name());
        Assertions.assertEquals(2, call.parameters().size());
        ASTParameter first = (ASTParameter) call.parameters().get(0);
        ASTParameter second = (ASTParameter) call.parameters().get(1);
        Assertions.assertNull(first.name());
        Assertions.assertNull(second.name());
        Assertions.assertInstanceOf(ASTVar.class, first.value());
        Assertions.assertEquals("x", ((ASTVar) first.value()).name());
        Assertions.assertInstanceOf(ASTLiteral.class, second.value());
        Assertions.assertEquals("5", ((ASTLiteral) second.value()).value());
    }

    @Test
    public void testChainedFunctionCallWithMemberAccess() {
        ScriptASTParser parser = new ScriptASTParser();
        // a.b::myFunc()
        // Should parse as myFunc(a.b)
        List<ScriptToken> tokens = List.of(
                new ScriptToken(ScriptTokenType.NAME, "a", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.DOT, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "b", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.DOUBLE_COLON, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.NAME, "myFunc", 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_OPEN, null, 1, null, 1, 1),
                new ScriptToken(ScriptTokenType.PARENTHESIS_CLOSE, null, 1, null, 1, 1)
        );
        ASTParseResult result = parser.parseSingleExpression(tokens);
        Assertions.assertTrue(result.errors().isEmpty());
        Assertions.assertInstanceOf(ASTFunctionCall.class, result.node());
        ASTFunctionCall call = (ASTFunctionCall) result.node();
        Assertions.assertEquals("myFunc", call.name());
        Assertions.assertEquals(1, call.parameters().size());
        ASTParameter first = (ASTParameter) call.parameters().get(0);
        Assertions.assertInstanceOf(ASTMemberAccess.class, first.value());
        ASTMemberAccess memberAccess = (ASTMemberAccess) first.value();
        Assertions.assertInstanceOf(ASTVar.class, memberAccess.object());
        Assertions.assertEquals("a", ((ASTVar) memberAccess.object()).name());
        Assertions.assertEquals("b", ((ASTMemberNameStatic) memberAccess.name()).name());
    }

}
