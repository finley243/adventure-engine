package com.github.finley243.adventureengine.script.parse;

import com.github.finley243.adventureengine.script.parse.nodes.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ScriptASTParser {

    private static final Set<ScriptTokenType> LITERAL_TYPES = EnumSet.of(ScriptTokenType.BOOLEAN_FALSE, ScriptTokenType.BOOLEAN_TRUE, ScriptTokenType.INTEGER, ScriptTokenType.FLOAT, ScriptTokenType.STRING, ScriptTokenType.NULL);
    private static final Set<ScriptTokenType> ASSIGNMENT_TYPES = EnumSet.of(ScriptTokenType.ASSIGNMENT, ScriptTokenType.MODIFIER_PLUS, ScriptTokenType.MODIFIER_MINUS, ScriptTokenType.MODIFIER_MULTIPLY, ScriptTokenType.MODIFIER_DIVIDE, ScriptTokenType.MODIFIER_MODULO);

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
        ASTNode bodyNode = parseCompound(bodyResult.contents(), bodyResult, errors);

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
                ASTLiteral defaultValue = parseLiteral(defaultValueToken);
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

    private ASTCompound parseCompound(TokenStream stream, BlockResult blockResult, List<CompileError> errors) {
        List<ASTNode> statements = new ArrayList<>();
        while (stream.hasNext()) {
            ASTNode statement;
            if (stream.expect(ScriptTokenType.IF) != null) {
                statement = parseIf(stream, errors);
            } else if (stream.expect(ScriptTokenType.FOR) != null) {
                statement = parseFor(stream, errors);
            } else {
                StatementResult result = stream.consumeUntil(ScriptTokenType.END_LINE);
                if (result.error() != StatementError.NONE) {
                    errors.add(new CompileError("Expected ';'", result.fileName(), result.line(), result.charStart(), result.charEnd()));
                    continue;
                }
                statement = parseSingleStatement(result.contents(), result, errors);
            }
            if (statement != null) {
                statements.add(statement);
            }
        }
        return new ASTCompound(statements, new SourceRange(blockResult.charStart(), blockResult.charEnd(), blockResult.fileName()));
    }

    private ASTIf parseIf(TokenStream stream, List<CompileError> errors) {
        ScriptToken ifToken = stream.current();
        List<ASTIfBranch> branches = new ArrayList<>();

        ASTIfBranch firstBranch = parseIfBranch(stream, "If statement", errors);
        if (firstBranch == null) return null;
        branches.add(firstBranch);

        ASTCompound elseBranch = null;
        while (stream.expect(ScriptTokenType.ELSE) != null) {
            if (stream.expect(ScriptTokenType.IF) != null) {
                ASTIfBranch elseIfBranch = parseIfBranch(stream, "Else-if statement", errors);
                if (elseIfBranch == null) break;
                branches.add(elseIfBranch);
            } else {
                BlockResult elseBodyResult = stream.consumeBlock(ScriptTokenType.BRACKET_OPEN, ScriptTokenType.BRACKET_CLOSE);
                if (elseBodyResult.error() != BlockError.NONE) {
                    String message = switch (elseBodyResult.error()) {
                        case MISSING_OPEN -> "Else statement is missing body";
                        case MISSING_CLOSE -> "Else statement body is not closed";
                        default -> null;
                    };
                    errors.add(new CompileError(message, elseBodyResult.fileName(), elseBodyResult.line(), elseBodyResult.charStart(), elseBodyResult.charEnd()));
                    break;
                }
                elseBranch = parseCompound(elseBodyResult.contents(), elseBodyResult, errors);
                break;
            }
        }

        int end = stream.current().charEnd();
        return new ASTIf(branches, elseBranch, new SourceRange(ifToken.charStart(), end, ifToken.fileName()));
    }

    private ASTIfBranch parseIfBranch(TokenStream stream, String context, List<CompileError> errors) {
        BlockResult conditionResult = stream.consumeBlock(ScriptTokenType.PARENTHESIS_OPEN, ScriptTokenType.PARENTHESIS_CLOSE);
        if (conditionResult.error() != BlockError.NONE) {
            String message = switch (conditionResult.error()) {
                case MISSING_OPEN -> context + " is missing condition block";
                case MISSING_CLOSE -> context + " condition block is not closed";
                default -> null;
            };
            errors.add(new CompileError(message, conditionResult.fileName(), conditionResult.line(), conditionResult.charStart(), conditionResult.charEnd()));
            stream.syncTo(ScriptTokenType.BRACKET_CLOSE);
            return null;
        }
        ASTNode condition = parseExpression(conditionResult.contents(), errors);

        BlockResult bodyResult = stream.consumeBlock(ScriptTokenType.BRACKET_OPEN, ScriptTokenType.BRACKET_CLOSE);
        if (bodyResult.error() != BlockError.NONE) {
            String message = switch (bodyResult.error()) {
                case MISSING_OPEN -> context + " is missing body";
                case MISSING_CLOSE -> context + " body is not closed";
                default -> null;
            };
            errors.add(new CompileError(message, bodyResult.fileName(), bodyResult.line(), bodyResult.charStart(), bodyResult.charEnd()));
            return null;
        }
        ASTNode body = parseCompound(bodyResult.contents(), bodyResult, errors);
        return new ASTIfBranch(condition, body, new SourceRange(conditionResult.charStart(), bodyResult.charEnd(), conditionResult.fileName()));
    }

    private ASTFor parseFor(TokenStream stream, List<CompileError> errors) {
        ScriptToken forToken = stream.current();

        BlockResult iteratorResult = stream.consumeBlock(ScriptTokenType.PARENTHESIS_OPEN, ScriptTokenType.PARENTHESIS_CLOSE);
        if (iteratorResult.error() != BlockError.NONE) {
            String message = switch (iteratorResult.error()) {
                case MISSING_OPEN -> "For loop is missing iterator block";
                case MISSING_CLOSE -> "For loop iterator block is not closed";
                default -> null;
            };
            errors.add(new CompileError(message, iteratorResult.fileName(), iteratorResult.line(), iteratorResult.charStart(), iteratorResult.charEnd()));
            stream.syncTo(ScriptTokenType.BRACKET_CLOSE);
            return null;
        }
        TokenStream iteratorStream = iteratorResult.contents();
        String iteratorName = iteratorStream.expectName();
        if (iteratorName == null) {
            ScriptToken current = iteratorStream.peek();
            errors.add(new CompileError("Expected iterator name", current.fileName(), current.line(), current.charStart(), current.charEnd()));
            stream.syncTo(ScriptTokenType.BRACKET_CLOSE);
            return null;
        }
        if (iteratorStream.expect(ScriptTokenType.COLON) == null) {
            ScriptToken current = iteratorStream.peek();
            errors.add(new CompileError("Expected ':' between iterator name and expression", current.fileName(), current.line(), current.charStart(), current.charEnd()));
            stream.syncTo(ScriptTokenType.BRACKET_CLOSE);
            return null;
        }
        ASTNode collection = parseExpression(iteratorStream, errors);

        BlockResult bodyResult = stream.consumeBlock(ScriptTokenType.BRACKET_OPEN, ScriptTokenType.BRACKET_CLOSE);
        if (bodyResult.error() != BlockError.NONE) {
            String message = switch (bodyResult.error()) {
                case MISSING_OPEN -> "For loop is missing body";
                case MISSING_CLOSE -> "For loop body is not closed";
                default -> null;
            };
            errors.add(new CompileError(message, bodyResult.fileName(), bodyResult.line(), bodyResult.charStart(), bodyResult.charEnd()));
            return null;
        }
        ASTNode body = parseCompound(bodyResult.contents(), bodyResult, errors);

        return new ASTFor(iteratorName, collection, body, new SourceRange(forToken.charStart(), stream.current().charEnd(), forToken.fileName()));
    }

    private ASTNode parseSingleStatement(TokenStream stream, StatementResult statementResult, List<CompileError> errors) {
        if (stream.expect(ScriptTokenType.RETURN) != null) {
            return parseReturn(stream, statementResult, errors);
        } else if (stream.expect(ScriptTokenType.BREAK) != null) {
            return parseBreak(stream, statementResult, errors);
        } else if (stream.expect(ScriptTokenType.CONTINUE) != null) {
            return parseContinue(stream, statementResult, errors);
        } else if (stream.expect(ScriptTokenType.VARIABLE) != null) {
            return parseVarDeclaration(stream, statementResult, errors);
        } else if (stream.expect(ScriptTokenType.ERROR) != null) {
            return parseError(stream, statementResult, errors);
        } else if (stream.expect(ScriptTokenType.LOG) != null) {
            return parseLog(stream, statementResult, errors);
        } else { // Statements that begin with a NAME token, or invalid statements beginning with other tokens
            return parseAssignmentOrCall(stream, statementResult, errors);
        }
    }

    private ASTReturn parseReturn(TokenStream stream, StatementResult statementResult, List<CompileError> errors) {
        ASTNode returnValue = parseExpression(stream, errors);
        return new ASTReturn(returnValue, new SourceRange(statementResult.charStart(), statementResult.charEnd(), statementResult.fileName()));
    }

    private ASTBreak parseBreak(TokenStream stream, StatementResult statementResult, List<CompileError> errors) {
        if (stream.hasNext()) {
            ScriptToken token = stream.peek();
            errors.add(new CompileError("Nothing is allowed after break", token.fileName(), token.line(), token.charStart(), token.charEnd()));
        }
        return new ASTBreak(new SourceRange(statementResult.charStart(), statementResult.charEnd(), statementResult.fileName()));
    }

    private ASTContinue parseContinue(TokenStream stream, StatementResult statementResult, List<CompileError> errors) {
        if (stream.hasNext()) {
            ScriptToken token = stream.peek();
            errors.add(new CompileError("Nothing is allowed after continue", token.fileName(), token.line(), token.charStart(), token.charEnd()));
        }
        return new ASTContinue(new SourceRange(statementResult.charStart(), statementResult.charEnd(), statementResult.fileName()));
    }

    private ASTVarDeclaration parseVarDeclaration(TokenStream stream, StatementResult statementResult, List<CompileError> errors) {
        String variableName = stream.expectName();
        if (variableName == null) {
            ScriptToken current = stream.peek();
            errors.add(new CompileError("Expected variable name", current.fileName(), current.line(), current.charStart(), current.charEnd()));
            return null;
        }

        ASTNode initialValue = null;
        if (stream.hasNext()) {
            if (stream.expect(ScriptTokenType.ASSIGNMENT) == null) {
                ScriptToken current = stream.peek();
                errors.add(new CompileError("Expected '='", current.fileName(), current.line(), current.charStart(), current.charEnd()));
                return null;
            }
            initialValue = parseExpression(stream, errors);
        }

        return new ASTVarDeclaration(variableName, initialValue, new SourceRange(statementResult.charStart(), statementResult.charEnd(), statementResult.fileName()));
    }

    private ASTError parseError(TokenStream stream, StatementResult statementResult, List<CompileError> errors) {
        BlockResult messageResult = stream.consumeBlock(ScriptTokenType.PARENTHESIS_OPEN, ScriptTokenType.PARENTHESIS_CLOSE);
        if (messageResult.error() != BlockError.NONE) {
            String message = switch (messageResult.error()) {
                case MISSING_OPEN -> "Error statement is missing opening parenthesis";
                case MISSING_CLOSE -> "Error statement is missing closing parenthesis";
                default -> null;
            };
            errors.add(new CompileError(message, messageResult.fileName(), messageResult.line(), messageResult.charStart(), messageResult.charEnd()));
            return null;
        }
        if (stream.hasNext()) {
            ScriptToken unexpected = stream.peek();
            errors.add(new CompileError("Unexpected tokens after error statement", unexpected.fileName(), unexpected.line(), unexpected.charStart(), unexpected.charEnd()));
        }
        ASTNode messageExpression = parseExpression(messageResult.contents(), errors);
        return new ASTError(messageExpression, new SourceRange(statementResult.charStart(), statementResult.charEnd(), statementResult.fileName()));
    }

    private ASTLog parseLog(TokenStream stream, StatementResult statementResult, List<CompileError> errors) {
        BlockResult messageResult = stream.consumeBlock(ScriptTokenType.PARENTHESIS_OPEN, ScriptTokenType.PARENTHESIS_CLOSE);
        if (messageResult.error() != BlockError.NONE) {
            String message = switch (messageResult.error()) {
                case MISSING_OPEN -> "Log statement is missing opening parenthesis";
                case MISSING_CLOSE -> "Log statement is missing closing parenthesis";
                default -> null;
            };
            errors.add(new CompileError(message, messageResult.fileName(), messageResult.line(), messageResult.charStart(), messageResult.charEnd()));
            return null;
        }
        if (stream.hasNext()) {
            ScriptToken unexpected = stream.peek();
            errors.add(new CompileError("Unexpected tokens after log statement", unexpected.fileName(), unexpected.line(), unexpected.charStart(), unexpected.charEnd()));
        }
        ASTNode messageExpression = parseExpression(messageResult.contents(), errors);
        return new ASTLog(messageExpression, new SourceRange(statementResult.charStart(), statementResult.charEnd(), statementResult.fileName()));
    }

    private ASTNode parseAssignmentOrCall(TokenStream stream, StatementResult statementResult, List<CompileError> errors) {
        ASTNode left = parseExpression(stream, errors);
        if (left == null) return null;

        ScriptToken assignmentOperator = stream.expectOneOf(ASSIGNMENT_TYPES);
        if (assignmentOperator == null) {
            if (!(left instanceof ASTFunctionCall)) {
                errors.add(new CompileError("Expression is not a valid statement", statementResult.fileName(), statementResult.line(), statementResult.charStart(), statementResult.charEnd()));
                return null;
            }
            return left;
        }

        ASTNode right = parseExpression(stream, errors);
        if (right == null) return null;

        SourceRange range = new SourceRange(statementResult.charStart(), statementResult.charEnd(), statementResult.fileName());
        ASTNode value = createValueFromAssignmentOperator(right, left, assignmentOperator.type(), range);

        if (left instanceof ASTVar v) {
            return new ASTVarAssignment(v, value, range);
        } else if (left instanceof ASTMemberAccess a) {
            return new ASTMemberAssignment(a, value, range);
        } else if (left instanceof ASTGlobalRef g) {
            return new ASTGlobalAssignment(g, value, range);
        } else if (left instanceof ASTListIndexRef l) {
            return new ASTListIndexAssignment(l, value, range);
        } else {
            errors.add(new CompileError("Invalid assignment target", statementResult.fileName(), statementResult.line(), statementResult.charStart(), statementResult.charEnd()));
            return null;
        }
    }

    private ASTNode createValueFromAssignmentOperator(ASTNode baseValue, ASTNode assignedReference, ScriptTokenType operator, SourceRange range) {
        return switch (operator) {
            case ASSIGNMENT -> baseValue;
            case MODIFIER_PLUS -> new ASTBinaryOp(ASTBinaryOp.Operator.ADD, assignedReference, baseValue, range);
            case MODIFIER_MINUS -> new ASTBinaryOp(ASTBinaryOp.Operator.SUBTRACT, assignedReference, baseValue, range);
            case MODIFIER_MULTIPLY -> new ASTBinaryOp(ASTBinaryOp.Operator.MULTIPLY, assignedReference, baseValue, range);
            case MODIFIER_DIVIDE -> new ASTBinaryOp(ASTBinaryOp.Operator.DIVIDE, assignedReference, baseValue, range);
            case MODIFIER_MODULO -> new ASTBinaryOp(ASTBinaryOp.Operator.MODULO, assignedReference, baseValue, range);
            default -> throw new IllegalArgumentException("Operator is not a valid assignment operator");
        };
    }

    private ASTNode parseExpression(TokenStream stream, List<CompileError> errors) {
        return null;
    }

    private ASTLiteral parseLiteral(ScriptToken token) {
        ASTLiteral.Type type = switch (token.type()) {
            case BOOLEAN_TRUE, BOOLEAN_FALSE -> ASTLiteral.Type.BOOLEAN;
            case INTEGER -> ASTLiteral.Type.INTEGER;
            case FLOAT -> ASTLiteral.Type.FLOAT;
            case STRING -> ASTLiteral.Type.STRING;
            case NULL -> ASTLiteral.Type.NULL;
            default -> throw new IllegalArgumentException("Token is not a literal type");
        };
        return new ASTLiteral(type, token.value(), new SourceRange(token.charStart(), token.charEnd(), token.fileName()));
    }

}
