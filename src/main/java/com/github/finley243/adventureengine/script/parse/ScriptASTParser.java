package com.github.finley243.adventureengine.script.parse;

import com.github.finley243.adventureengine.script.parse.nodes.ASTFile;
import com.github.finley243.adventureengine.script.parse.nodes.ASTFunction;
import com.github.finley243.adventureengine.script.parse.nodes.ASTParameterDefinition;
import com.github.finley243.adventureengine.script.parse.nodes.ScriptASTNode;

import java.util.ArrayList;
import java.util.List;

public class ScriptASTParser {

    public ScriptASTNode parse(List<ScriptToken> tokens) {
        if (tokens.isEmpty()) return null;
        TokenStream stream = new TokenStream(tokens);
        List<ScriptASTNode> functions = new ArrayList<>();
        List<CompileError> errors = new ArrayList<>();
        while (stream.hasNext()) {
            ScriptASTNode functionNode = parseFunctionDef(stream, errors);
            functions.add(functionNode);
        }
        return new ASTFile(functions, new SourceRange(tokens.getFirst().charStart(), tokens.getLast().charEnd(), tokens.getFirst().fileName()));
    }

    private ScriptASTNode parseFunctionDef(TokenStream stream, List<CompileError> errors) {
        String fileName = stream.current().fileName();
        int start = stream.current().charStart();
        String firstName = stream.expectName();
        if (firstName == null) {
            ScriptToken current = stream.current();
            errors.add(new CompileError("Expected function name", current.fileName(), current.line(), current.charStart(), current.charEnd()));
            stream.syncTo(ScriptTokenType.BRACKET_CLOSE);
            return null;
        }
        String secondName = stream.expectName();
        String returnType;
        String functionName;
        if (secondName != null) { // Has return type
            returnType = firstName;
            functionName = secondName;
        } else {
            returnType = null;
            functionName = firstName;
        }

        BlockResult parameterResult = stream.consumeBlock(ScriptTokenType.PARENTHESIS_OPEN, ScriptTokenType.PARENTHESIS_CLOSE);
        if (parameterResult.error() != BlockError.NONE) {
            String message = switch (parameterResult.error()) {
                case MISSING_OPEN -> "Function is missing parameter block";
                case MISSING_CLOSE -> "Function parameter block is not closed";
                default -> null;
            };
            errors.add(new CompileError(message, parameterResult.fileName(), parameterResult.line(), parameterResult.charStart(), parameterResult.charEnd()));
            stream.syncTo(ScriptTokenType.BRACKET_CLOSE);
            return null;
        }
        List<ScriptASTNode> parameterNodes = parseParameterDefs(parameterResult.contents(), errors);

        BlockResult bodyResult = stream.consumeBlock(ScriptTokenType.BRACKET_OPEN, ScriptTokenType.BRACKET_CLOSE);
        if (bodyResult.error() != BlockError.NONE) {
            String message = switch (bodyResult.error()) {
                case MISSING_OPEN -> "Function is missing body";
                case MISSING_CLOSE -> "Function body is not closed";
                default -> null;
            };
            errors.add(new CompileError(message, bodyResult.fileName(), bodyResult.line(), bodyResult.charStart(), bodyResult.charEnd()));
            return null;
        }
        ScriptASTNode bodyNode = parseCompound(bodyResult.contents(), errors);

        int end = stream.current().charEnd();
        return new ASTFunction(functionName, returnType, parameterNodes, bodyNode, new SourceRange(start, end, fileName));
    }

    private List<ScriptASTNode> parseParameterDefs(TokenStream stream, List<CompileError> errors) {
        List<ScriptASTNode> parameterNodes = new ArrayList<>();
        boolean hasOptional = false;
        while (stream.hasNext()) {
            StatementResult parameterResult = stream.consumeUntil(ScriptTokenType.COMMA);
            if (parameterResult.error() != StatementError.NONE) {
                errors.add(new CompileError("Invalid parameter definition", parameterResult.fileName(), parameterResult.line(), parameterResult.charStart(), parameterResult.charEnd()));
                continue;
            }
            String parameterName = stream.expectName();

        }
        return parameterNodes;
    }

    private ScriptASTNode parseCompound(TokenStream stream, List<CompileError> errors) {
        return null;
    }

}
