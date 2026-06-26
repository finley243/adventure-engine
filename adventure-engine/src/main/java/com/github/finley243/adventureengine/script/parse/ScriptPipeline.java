package com.github.finley243.adventureengine.script.parse;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.load.GameDataException;
import com.github.finley243.adventureengine.script.*;
import com.github.finley243.adventureengine.script.nodes.ASTCompound;
import com.github.finley243.adventureengine.script.nodes.ASTFile;
import com.github.finley243.adventureengine.script.nodes.ASTLiteral;
import com.github.finley243.adventureengine.script.nodes.ASTNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScriptPipeline {

    private final ScriptLexer lexer;
    private final ScriptASTParser parser;
    private final ScriptValidator validator;
    private final ScriptConverter converter;

    public ScriptPipeline(ScriptLexer lexer, ScriptASTParser parser, ScriptValidator validator, ScriptConverter converter) {
        this.lexer = lexer;
        this.parser = parser;
        this.validator = validator;
        this.converter = converter;
    }

    public Script compileExpression(String source, String fileName, Set<String> knownFunctions, Set<String> externalVariables) {
        List<CompileError> errors = new ArrayList<>();
        List<ScriptToken> scriptTokens = lexer.parseToTokens(source, fileName, errors);
        ASTNode node = parser.parseSingleExpression(scriptTokens, errors);
        validator.validateInlineExpression(node, errors, knownFunctions, externalVariables);
        throwExceptionIfHasErrors(errors);
        return converter.convertInlineExpression(node);
    }

    public Script compileBlock(String source, String fileName, Set<String> knownFunctions, Set<String> externalVariables) {
        List<CompileError> errors = new ArrayList<>();
        List<ScriptToken> scriptTokens = lexer.parseToTokens(source, fileName, errors);
        ASTNode node = parser.parseInlineScript(scriptTokens, errors);
        ASTCompound compoundNode = (ASTCompound) node;
        validator.validateInlineBlock(compoundNode, errors, knownFunctions, externalVariables);
        throwExceptionIfHasErrors(errors);
        return converter.convertInlineBlock(compoundNode);
    }

    public List<ScriptFunction> compileFiles(Map<String, String> fileStrings, Set<String> reservedFunctionNames) {
        List<CompileError> errors = new ArrayList<>();
        List<ASTFile> fileASTs = new ArrayList<>();
        for (Map.Entry<String, String> fileEntry : fileStrings.entrySet()) {
            String fileName = fileEntry.getKey();
            String fileContents = fileEntry.getValue();
            List<ScriptToken> tokenList = lexer.parseToTokens(fileContents, fileName, errors);
            ASTNode node = parser.parse(tokenList, errors);
            ASTFile fileAST = (ASTFile) node;
            fileASTs.add(fileAST);
        }
        validator.validate(fileASTs, errors, reservedFunctionNames);
        throwExceptionIfHasErrors(errors);
        return converter.convert(fileASTs);
    }

    public Expression compileLiteralExpression(String source, String fileName) {
        List<CompileError> errors = new ArrayList<>();
        List<ScriptToken> scriptTokens = lexer.parseToTokens(source, fileName, errors);
        ASTNode node = parser.parseLiteralExpression(scriptTokens);
        return converter.convertInlineLiteral((ASTLiteral) node);
    }

    private void throwExceptionIfHasErrors(List<CompileError> errors) {
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder("Script parsing failed:\n");
            for (CompileError error : errors) {
                sb.append(" [").append(error.range().fileName()).append(":").append(error.range().line()).append("] ").append(error.message()).append("\n");
            }
            throw new GameDataException(sb.toString());
        }
    }

}
