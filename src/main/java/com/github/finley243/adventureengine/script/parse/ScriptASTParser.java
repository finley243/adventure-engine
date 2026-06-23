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
        ScriptToken funcToken = stream.current();
        if (!stream.expect(ScriptTokenType.NAME)) {
            ScriptToken current = stream.current();
            errors.add(new CompileError("Expected function name", current.fileName(), current.line(), current.charStart(), current.charEnd()));
            stream.syncToEndOfBlock();
            return null;
        }
        String returnType = null;
        if (stream.hasNext() && stream.peek().type() == ScriptTokenType.NAME) { // Has return type
            returnType = stream.current().value();
            if (!stream.expect(ScriptTokenType.NAME)) {
                ScriptToken current = stream.current();
                errors.add(new CompileError("Expected function name", current.fileName(), current.line(), current.charStart(), current.charEnd()));
                stream.syncToEndOfBlock();
                return null;
            }
        }
        String functionName = stream.current().value();

        TokenStream parameterStream = stream.consumeBlock(ScriptTokenType.PARENTHESIS_OPEN, ScriptTokenType.PARENTHESIS_CLOSE);
        if (parameterStream == null) {
            errors.add(new CompileError("Expected function parameters", funcToken.fileName(), funcToken.line(), funcToken.charStart(), funcToken.charEnd()));
            stream.syncToEndOfBlock();
            return null;
        }
        List<ScriptASTNode> parameterNodes = parseParameterDefs(parameterStream, errors);

        TokenStream bodyStream = stream.consumeBlock(ScriptTokenType.BRACKET_OPEN, ScriptTokenType.BRACKET_CLOSE);
        if (bodyStream == null) {
            errors.add(new CompileError("Expected function body", funcToken.fileName(), funcToken.line(), funcToken.charStart(), funcToken.charEnd()));
            return null;
        }
        ScriptASTNode bodyNode = parseCompound(bodyStream, errors);

        int end = stream.current().charEnd();
        return new ASTFunction(functionName, returnType, parameterNodes, bodyNode, new SourceRange(funcToken.charStart(), end, funcToken.fileName()));
    }

    private List<ScriptASTNode> parseParameterDefs(TokenStream stream, List<CompileError> errors) {
        return null;
    }

    private ScriptASTNode parseCompound(TokenStream stream, List<CompileError> errors) {
        return null;
    }

}
