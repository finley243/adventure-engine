package com.github.finley243.adventureengine.script.parse;

import com.github.finley243.adventureengine.script.parse.nodes.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ScriptASTParser {

    private static final Set<ScriptTokenType> LITERAL_TYPES = EnumSet.of(ScriptTokenType.BOOLEAN_FALSE, ScriptTokenType.BOOLEAN_TRUE, ScriptTokenType.INTEGER, ScriptTokenType.FLOAT, ScriptTokenType.STRING, ScriptTokenType.NULL);

    public ASTParseResult parse(List<ScriptToken> tokens) {
        if (tokens.isEmpty()) return null;
        TokenStream stream = new TokenStream(tokens);
        List<CompileError> errors = new ArrayList<>();
        ASTNode fileNode = parseFile(stream, errors);
        return new ASTParseResult(fileNode, errors);
    }

    private ASTNode parseFile(TokenStream stream, List<CompileError> errors) {
        String fileName = stream.peek().fileName();
        int start = stream.peek().charStart();
        List<ASTNode> functions = new ArrayList<>();
        while (stream.hasNext()) {
            ScriptToken funcToken = stream.expect(ScriptTokenType.FUNCTION);
            if (funcToken == null) {
                ScriptToken current = stream.peek();
                errors.add(new CompileError("Only function definitions are allowed at top level", current.fileName(), current.line(), current.charStart(), current.charEnd()));
                stream.syncTo(ScriptTokenType.BRACKET_CLOSE);
                continue;
            }
            ASTNode functionNode = parseFunctionDef(stream, errors);
            if (functionNode != null) {
                functions.add(functionNode);
            }
        }
        int end = stream.current().charEnd();
        return new ASTFile(functions, new SourceRange(start, end, fileName));
    }

    private ASTNode parseFunctionDef(TokenStream stream, List<CompileError> errors) {
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
        List<ASTNode> parameterNodes = parseParameterDefs(parameterResult.contents(), errors);

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
        ASTNode bodyNode = parseCompound(bodyResult.contents(), errors);

        int end = stream.current().charEnd();
        return new ASTFunction(functionName, returnType, parameterNodes, bodyNode, new SourceRange(start, end, fileName));
    }

    private List<ASTNode> parseParameterDefs(TokenStream stream, List<CompileError> errors) {
        List<ASTNode> parameterNodes = new ArrayList<>();
        boolean hasOptional = false;
        while (stream.hasNext()) {
            StatementResult parameterResult = stream.consumeUntil(ScriptTokenType.COMMA);
            if (parameterResult.error() != StatementError.NONE) {
                errors.add(new CompileError("Invalid parameter definition", parameterResult.fileName(), parameterResult.line(), parameterResult.charStart(), parameterResult.charEnd()));
                continue;
            }
            TokenStream parameterStream = parameterResult.contents();
            int parameterStart = parameterResult.charStart();
            String parameterName = parameterStream.expectName();
            if (parameterName == null) {
                errors.add(new CompileError("Expected parameter name", parameterResult.fileName(), parameterResult.line(), parameterResult.charStart(), parameterResult.charEnd()));
                continue;
            }
            if (parameterStream.expect(ScriptTokenType.ASSIGNMENT) != null) { // Optional parameter with default value
                hasOptional = true;
                ScriptToken defaultValueToken = parameterStream.expectOneOf(LITERAL_TYPES);
                if (defaultValueToken == null) {
                    errors.add(new CompileError("Expected literal default value", parameterResult.fileName(), parameterResult.line(), parameterResult.charStart(), parameterResult.charEnd()));
                    continue;
                }
                ASTNode defaultValue = parseLiteral(defaultValueToken);
                if (parameterStream.hasNext()) {
                    errors.add(new CompileError("Unexpected tokens in parameter definition", parameterResult.fileName(), parameterResult.line(), parameterResult.charStart(), parameterResult.charEnd()));
                    continue;
                }
                int parameterEnd = parameterStream.current().charEnd();
                parameterNodes.add(new ASTParameterDefinition(parameterName, defaultValue, new SourceRange(parameterStart, parameterEnd, parameterResult.fileName())));
            } else { // Required parameter
                if (parameterStream.hasNext()) {
                    errors.add(new CompileError("Unexpected tokens in parameter definition", parameterResult.fileName(), parameterResult.line(), parameterResult.charStart(), parameterResult.charEnd()));
                    continue;
                }
                if (hasOptional) {
                    errors.add(new CompileError("Required parameter cannot follow optional parameter", parameterResult.fileName(), parameterResult.line(), parameterResult.charStart(), parameterResult.charEnd()));
                }
                int parameterEnd = parameterStream.current().charEnd();
                parameterNodes.add(new ASTParameterDefinition(parameterName, null, new SourceRange(parameterStart, parameterEnd, parameterResult.fileName())));
            }
        }
        return parameterNodes;
    }

    private ASTNode parseCompound(TokenStream stream, List<CompileError> errors) {
        return null;
    }

    private ASTNode parseLiteral(ScriptToken token) {
        ASTLiteral.Type type = switch (token.type()) {
            case BOOLEAN_TRUE, BOOLEAN_FALSE -> ASTLiteral.Type.BOOLEAN;
            case INTEGER -> ASTLiteral.Type.INTEGER;
            case FLOAT -> ASTLiteral.Type.FLOAT;
            case STRING -> ASTLiteral.Type.STRING;
            case NULL -> ASTLiteral.Type.NULL;
            default -> throw new IllegalArgumentException("Token is not a literal type");
        };
        String value = switch (token.type()) {
            case BOOLEAN_TRUE -> "true";
            case BOOLEAN_FALSE -> "false";
            case INTEGER, FLOAT, STRING -> token.value();
            case NULL -> null;
            default -> throw new IllegalArgumentException("Token is not a literal type");
        };
        return new ASTLiteral(type, value, new SourceRange(token.charStart(), token.charEnd(), token.fileName()));
    }

}
