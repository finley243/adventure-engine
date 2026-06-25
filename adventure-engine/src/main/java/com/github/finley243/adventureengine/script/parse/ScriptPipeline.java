package com.github.finley243.adventureengine.script.parse;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.load.GameDataException;
import com.github.finley243.adventureengine.script.*;
import com.github.finley243.adventureengine.script.nodes.ASTCompound;
import com.github.finley243.adventureengine.script.nodes.ASTFile;
import com.github.finley243.adventureengine.script.nodes.ASTLiteral;

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
        List<ScriptToken> scriptTokens = lexer.parseToTokens(source, fileName);
        ASTParseResult parseResult = parser.parseSingleExpression(scriptTokens);
        if (!parseResult.errors().isEmpty()) {
            StringBuilder sb = new StringBuilder("Script parsing failed:\n");
            for (CompileError error : parseResult.errors()) {
                sb.append(" [").append(error.range().fileName()).append(":").append(error.range().line()).append("] ").append(error.message()).append("\n");
            }
            throw new GameDataException(sb.toString());
        }
        List<CompileError> validatorErrors = validator.validateInlineExpression(parseResult.node(), knownFunctions, externalVariables);
        if (!validatorErrors.isEmpty()) {
            StringBuilder sb = new StringBuilder("Script validation failed:\n");
            for (CompileError error : validatorErrors) {
                sb.append(" [").append(error.range().fileName()).append(":").append(error.range().line()).append("] ").append(error.message()).append("\n");
            }
            throw new GameDataException(sb.toString());
        }
        return converter.convertInlineExpression(parseResult.node());
    }

    public Script compileBlock(String source, String fileName, Set<String> knownFunctions, Set<String> externalVariables) {
        List<ScriptToken> scriptTokens = lexer.parseToTokens(source, fileName);
        ASTParseResult parseResult = parser.parseInlineScript(scriptTokens);
        if (!parseResult.errors().isEmpty()) {
            StringBuilder sb = new StringBuilder("Script parsing failed:\n");
            for (CompileError error : parseResult.errors()) {
                sb.append(" [").append(error.range().fileName()).append(":").append(error.range().line()).append("] ").append(error.message()).append("\n");
            }
            throw new GameDataException(sb.toString());
        }
        ASTCompound compoundNode = (ASTCompound) parseResult.node();
        List<CompileError> validatorErrors = validator.validateInlineBlock(compoundNode, knownFunctions, externalVariables);
        if (!validatorErrors.isEmpty()) {
            StringBuilder sb = new StringBuilder("Script validation failed:\n");
            for (CompileError error : validatorErrors) {
                sb.append(" [").append(error.range().fileName()).append(":").append(error.range().line()).append("] ").append(error.message()).append("\n");
            }
            throw new GameDataException(sb.toString());
        }
        return converter.convertInlineBlock(compoundNode);
    }

    public List<ScriptFunction> compileFiles(Map<String, String> fileStrings, Set<String> reservedFunctionNames) {
        List<ASTFile> fileASTs = new ArrayList<>();
        for (Map.Entry<String, String> fileEntry : fileStrings.entrySet()) {
            String fileName = fileEntry.getKey();
            String fileContents = fileEntry.getValue();
            List<ScriptToken> tokenList = lexer.parseToTokens(fileContents, fileName);
            ASTParseResult parseResult = parser.parse(tokenList);
            if (!parseResult.errors().isEmpty()) {
                StringBuilder sb = new StringBuilder("Script parsing failed:\n");
                for (CompileError error : parseResult.errors()) {
                    sb.append(" [").append(error.range().fileName()).append(":").append(error.range().line()).append("] ").append(error.message()).append("\n");
                }
                throw new GameDataException(sb.toString());
            }
            ASTFile fileAST = (ASTFile) parseResult.node();
            fileASTs.add(fileAST);
        }
        validator.validateOrThrow(fileASTs, reservedFunctionNames);
        return converter.convert(fileASTs);
    }

    public Expression compileLiteralExpression(String source, String fileName) {
        List<ScriptToken> scriptTokens = lexer.parseToTokens(source, fileName);
        ASTParseResult parseResult = parser.parseLiteralExpression(scriptTokens);
        return converter.convertInlineLiteral((ASTLiteral) parseResult.node());
    }

}
