package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.script.nodes.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ScriptASTParser {

    private static final int UNARY_OP_MIN_PRECEDENCE = 8;

    private static final Set<ScriptTokenType> LITERAL_TYPES = EnumSet.of(ScriptTokenType.BOOLEAN_FALSE, ScriptTokenType.BOOLEAN_TRUE, ScriptTokenType.INTEGER, ScriptTokenType.FLOAT, ScriptTokenType.STRING, ScriptTokenType.NULL);
    private static final Set<ScriptTokenType> ASSIGNMENT_TYPES = EnumSet.of(ScriptTokenType.ASSIGNMENT, ScriptTokenType.MODIFIER_PLUS, ScriptTokenType.MODIFIER_MINUS, ScriptTokenType.MODIFIER_MULTIPLY, ScriptTokenType.MODIFIER_DIVIDE, ScriptTokenType.MODIFIER_MODULO);

    public ASTNode parse(List<ScriptToken> tokens, List<CompileError> errors) {
        if (tokens.isEmpty()) return null;
        TokenStream stream = new TokenStream(tokens);
        return parseFile(stream, errors);
    }

    public ASTNode parseSingleExpression(List<ScriptToken> tokens, List<CompileError> errors) {
        if (tokens.isEmpty()) return null;
        TokenStream stream = new TokenStream(tokens);
        return parseExpression(stream, errors);
    }

    public ASTNode parseInlineScript(List<ScriptToken> tokens, List<CompileError> errors) {
        if (tokens.isEmpty()) return null;
        TokenStream stream = new TokenStream(tokens);
        BlockResult placeholderBlockResult = new BlockResult(stream, new SourceRange(tokens.getFirst(), tokens.getLast()), BlockError.NONE);
        return parseCompound(stream, placeholderBlockResult, errors);
    }

    public ASTNode parseLiteralExpression(List<ScriptToken> tokens) {
        if (tokens.size() != 1) return null;
        return parseLiteral(tokens.getFirst());
    }

    private ASTNode parseFile(TokenStream stream, List<CompileError> errors) {
        ScriptToken startToken = stream.peek();
        List<ASTFunction> functions = new ArrayList<>();
        while (stream.hasNext()) {
            ScriptToken funcToken = stream.expect(ScriptTokenType.FUNCTION);
            if (funcToken == null) {
                ScriptToken current = stream.peek();
                errors.add(new CompileError("Only function definitions are allowed at top level", new SourceRange(current)));
                stream.syncTo(ScriptTokenType.BRACKET_CLOSE);
                continue;
            }
            ASTFunction functionNode = parseFunctionDef(stream, errors);
            if (functionNode != null) {
                functions.add(functionNode);
            }
        }
        return new ASTFile(functions, new SourceRange(startToken, stream.current()));
    }

    private ASTFunction parseFunctionDef(TokenStream stream, List<CompileError> errors) {
        ScriptToken startToken = stream.current();
        String functionName = stream.expectName();
        ScriptToken nameToken = stream.current();
        if (functionName == null) {
            errors.add(new CompileError("Expected function name", new SourceRange(nameToken)));
            stream.syncTo(ScriptTokenType.BRACKET_CLOSE);
            return null;
        }

        BlockResult parameterResult = stream.consumeBlock(ScriptTokenType.PARENTHESIS_OPEN, ScriptTokenType.PARENTHESIS_CLOSE);
        if (parameterResult.error() != BlockError.NONE) {
            String message = switch (parameterResult.error()) {
                case MISSING_OPEN -> "Function is missing parameter block";
                case MISSING_CLOSE -> "Function parameter block is not closed";
                default -> null;
            };
            errors.add(new CompileError(message, parameterResult.range()));
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
            errors.add(new CompileError(message, bodyResult.range()));
            return null;
        }
        ASTNode bodyNode = parseCompound(bodyResult.contents(), bodyResult, errors);

        return new ASTFunction(new SourceRange(startToken), functionName, new SourceRange(nameToken), parameterNodes, bodyNode, new SourceRange(startToken, stream.current()));
    }

    private List<ASTNode> parseParameterDefs(TokenStream stream, List<CompileError> errors) {
        List<ASTNode> parameterNodes = new ArrayList<>();
        boolean hasOptional = false;
        while (stream.hasNext()) {
            StatementResult parameterResult = stream.consumeUntil(ScriptTokenType.COMMA);
            if (parameterResult.error() != StatementError.NONE) {
                errors.add(new CompileError("Invalid parameter definition", parameterResult.range()));
                continue;
            }
            TokenStream parameterStream = parameterResult.contents();
            ScriptToken parameterStartToken = parameterResult.contents().peek();
            ScriptToken parameterNameToken = parameterStream.expect(ScriptTokenType.NAME);
            if (parameterNameToken == null) {
                errors.add(new CompileError("Expected parameter name", parameterResult.range()));
                continue;
            }
            String parameterName = parameterNameToken.value();
            if (parameterStream.expect(ScriptTokenType.ASSIGNMENT) != null) { // Optional parameter with default value
                SourceRange assignmentRange = new SourceRange(parameterStream.current());
                hasOptional = true;
                ScriptToken defaultValueToken = parameterStream.expectOneOf(LITERAL_TYPES);
                if (defaultValueToken == null) {
                    errors.add(new CompileError("Expected literal default value", parameterResult.range()));
                    continue;
                }
                ASTLiteral defaultValue = parseLiteral(defaultValueToken);
                if (parameterStream.hasNext()) {
                    errors.add(new CompileError("Unexpected tokens in parameter definition", parameterResult.range()));
                    continue;
                }
                parameterNodes.add(new ASTParameterDefinition(new SourceRange(parameterNameToken), parameterName, assignmentRange, defaultValue, new SourceRange(parameterStartToken, parameterStream.current())));
            } else { // Required parameter
                if (parameterStream.hasNext()) {
                    errors.add(new CompileError("Unexpected tokens in parameter definition", parameterResult.range()));
                    continue;
                }
                if (hasOptional) {
                    errors.add(new CompileError("Required parameter cannot follow optional parameter", parameterResult.range()));
                }
                parameterNodes.add(new ASTParameterDefinition(new SourceRange(parameterNameToken), parameterName, null, null, new SourceRange(parameterStartToken, parameterStream.current())));
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
                    errors.add(new CompileError("Expected ';'", result.range()));
                    continue;
                }
                statement = parseSingleStatement(result.contents(), result, errors);
            }
            if (statement != null) {
                statements.add(statement);
            }
        }
        return new ASTCompound(statements, blockResult.range());
    }

    private ASTIf parseIf(TokenStream stream, List<CompileError> errors) {
        ScriptToken ifToken = stream.current();
        List<ASTIfBranch> branches = new ArrayList<>();

        ASTIfBranch firstBranch = parseIfBranch(new SourceRange(ifToken), stream, "If statement", errors);
        if (firstBranch == null) return null;
        branches.add(firstBranch);

        SourceRange elseTokenRange = null;
        ASTCompound elseBranch = null;
        while (stream.expect(ScriptTokenType.ELSE) != null) {
            ScriptToken branchElseToken = stream.current();
            if (stream.expect(ScriptTokenType.IF) != null) {
                ScriptToken branchIfToken = stream.current();
                ASTIfBranch elseIfBranch = parseIfBranch(new SourceRange(branchElseToken, branchIfToken), stream, "Else-if statement", errors);
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
                    errors.add(new CompileError(message, elseBodyResult.range()));
                    break;
                }
                elseTokenRange = new SourceRange(branchElseToken);
                elseBranch = parseCompound(elseBodyResult.contents(), elseBodyResult, errors);
                break;
            }
        }

        return new ASTIf(branches, elseTokenRange, elseBranch, new SourceRange(ifToken, stream.current()));
    }

    private ASTIfBranch parseIfBranch(SourceRange keywordRange, TokenStream stream, String context, List<CompileError> errors) {
        BlockResult conditionResult = stream.consumeBlock(ScriptTokenType.PARENTHESIS_OPEN, ScriptTokenType.PARENTHESIS_CLOSE);
        if (conditionResult.error() != BlockError.NONE) {
            String message = switch (conditionResult.error()) {
                case MISSING_OPEN -> context + " is missing condition block";
                case MISSING_CLOSE -> context + " condition block is not closed";
                default -> null;
            };
            errors.add(new CompileError(message, conditionResult.range()));
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
            errors.add(new CompileError(message, bodyResult.range()));
            return null;
        }
        ASTNode body = parseCompound(bodyResult.contents(), bodyResult, errors);
        return new ASTIfBranch(keywordRange, condition, body, conditionResult.range());
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
            errors.add(new CompileError(message, iteratorResult.range()));
            stream.syncTo(ScriptTokenType.BRACKET_CLOSE);
            return null;
        }
        TokenStream iteratorStream = iteratorResult.contents();
        ScriptToken iteratorNameToken = iteratorStream.expect(ScriptTokenType.NAME);
        if (iteratorNameToken == null) {
            ScriptToken current = iteratorStream.peek();
            errors.add(new CompileError("Expected iterator name", new SourceRange(current)));
            stream.syncTo(ScriptTokenType.BRACKET_CLOSE);
            return null;
        }
        String iteratorName = iteratorNameToken.value();
        ScriptToken colonToken = iteratorStream.expect(ScriptTokenType.COLON);
        if (colonToken == null) {
            ScriptToken current = iteratorStream.peek();
            errors.add(new CompileError("Expected ':' between iterator name and expression", new SourceRange(current)));
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
            errors.add(new CompileError(message, bodyResult.range()));
            return null;
        }
        ASTNode body = parseCompound(bodyResult.contents(), bodyResult, errors);

        return new ASTFor(new SourceRange(forToken), new SourceRange(iteratorNameToken), iteratorName, new SourceRange(colonToken), collection, body, new SourceRange(forToken, stream.current()));
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
        ScriptToken keywordToken = stream.current();
        ASTNode returnValue = parseExpression(stream, errors);
        return new ASTReturn(new SourceRange(keywordToken), returnValue, statementResult.range());
    }

    private ASTBreak parseBreak(TokenStream stream, StatementResult statementResult, List<CompileError> errors) {
        if (stream.hasNext()) {
            ScriptToken token = stream.peek();
            errors.add(new CompileError("Nothing is allowed after break", new SourceRange(token)));
        }
        return new ASTBreak(statementResult.range());
    }

    private ASTContinue parseContinue(TokenStream stream, StatementResult statementResult, List<CompileError> errors) {
        if (stream.hasNext()) {
            ScriptToken token = stream.peek();
            errors.add(new CompileError("Nothing is allowed after continue", new SourceRange(token)));
        }
        return new ASTContinue(statementResult.range());
    }

    private ASTVarDeclaration parseVarDeclaration(TokenStream stream, StatementResult statementResult, List<CompileError> errors) {
        ScriptToken keywordToken = stream.current();
        ScriptToken variableNameToken = stream.expect(ScriptTokenType.NAME);
        if (variableNameToken == null) {
            ScriptToken current = stream.hasNext() ? stream.peek() : stream.current();
            errors.add(new CompileError("Expected variable name", new SourceRange(current)));
            return null;
        }
        String variableName = variableNameToken.value();

        ASTNode initialValue = null;
        ScriptToken operatorToken = null;
        if (stream.hasNext()) {
            operatorToken = stream.expect(ScriptTokenType.ASSIGNMENT);
            if (operatorToken == null) {
                ScriptToken current = stream.hasNext() ? stream.peek() : stream.current();
                errors.add(new CompileError("Expected '='", new SourceRange(current)));
                return null;
            }
            initialValue = parseExpression(stream, errors);
        }

        return new ASTVarDeclaration(new SourceRange(keywordToken), new SourceRange(variableNameToken), variableName, operatorToken == null ? null : new SourceRange(operatorToken), initialValue, statementResult.range());
    }

    private ASTError parseError(TokenStream stream, StatementResult statementResult, List<CompileError> errors) {
        ScriptToken keywordToken = stream.current();
        BlockResult messageResult = stream.consumeBlock(ScriptTokenType.PARENTHESIS_OPEN, ScriptTokenType.PARENTHESIS_CLOSE);
        if (messageResult.error() != BlockError.NONE) {
            String message = switch (messageResult.error()) {
                case MISSING_OPEN -> "Error statement is missing opening parenthesis";
                case MISSING_CLOSE -> "Error statement is missing closing parenthesis";
                default -> null;
            };
            errors.add(new CompileError(message, messageResult.range()));
            return null;
        }
        if (stream.hasNext()) {
            ScriptToken unexpected = stream.peek();
            errors.add(new CompileError("Unexpected tokens after error statement", new SourceRange(unexpected)));
        }
        ASTNode messageExpression = parseExpression(messageResult.contents(), errors);
        return new ASTError(new SourceRange(keywordToken), messageExpression, statementResult.range());
    }

    private ASTLog parseLog(TokenStream stream, StatementResult statementResult, List<CompileError> errors) {
        ScriptToken keywordToken = stream.current();
        BlockResult messageResult = stream.consumeBlock(ScriptTokenType.PARENTHESIS_OPEN, ScriptTokenType.PARENTHESIS_CLOSE);
        if (messageResult.error() != BlockError.NONE) {
            String message = switch (messageResult.error()) {
                case MISSING_OPEN -> "Log statement is missing opening parenthesis";
                case MISSING_CLOSE -> "Log statement is missing closing parenthesis";
                default -> null;
            };
            errors.add(new CompileError(message, messageResult.range()));
            return null;
        }
        if (stream.hasNext()) {
            ScriptToken unexpected = stream.peek();
            errors.add(new CompileError("Unexpected tokens after log statement", new SourceRange(unexpected)));
        }
        ASTNode messageExpression = parseExpression(messageResult.contents(), errors);
        return new ASTLog(new SourceRange(keywordToken), messageExpression, statementResult.range());
    }

    private ASTNode parseAssignmentOrCall(TokenStream stream, StatementResult statementResult, List<CompileError> errors) {
        ASTNode left = parseExpression(stream, errors);
        if (left == null) return null;

        ScriptToken assignmentOperator = stream.expectOneOf(ASSIGNMENT_TYPES);
        if (assignmentOperator == null) {
            if (!(left instanceof ASTFunctionCall)) {
                errors.add(new CompileError("Expression is not a valid statement", statementResult.range()));
                return null;
            }
            return left;
        }

        ASTNode right = parseExpression(stream, errors);
        if (right == null) return null;

        SourceRange range = statementResult.range();
        ASTNode value = createValueFromAssignmentOperator(right, left, assignmentOperator.type(), range);
        SourceRange operatorRange = new SourceRange(assignmentOperator);

        if (left instanceof ASTVar v) {
            return new ASTVarAssignment(v, operatorRange, value, range);
        } else if (left instanceof ASTMemberAccess a) {
            return new ASTMemberAssignment(a, operatorRange, value, range);
        } else if (left instanceof ASTGlobalRef g) {
            return new ASTGlobalAssignment(g, operatorRange, value, range);
        } else if (left instanceof ASTListIndexRef l) {
            return new ASTListIndexAssignment(l, operatorRange, value, range);
        } else {
            errors.add(new CompileError("Invalid assignment target", range));
            return null;
        }
    }

    private ASTNode createValueFromAssignmentOperator(ASTNode baseValue, ASTNode assignedReference, ScriptTokenType operator, SourceRange range) {
        return switch (operator) {
            case ASSIGNMENT -> baseValue;
            case MODIFIER_PLUS -> new ASTBinaryOp(ASTBinaryOp.Operator.ADD, null, assignedReference, baseValue, range);
            case MODIFIER_MINUS -> new ASTBinaryOp(ASTBinaryOp.Operator.SUBTRACT, null, assignedReference, baseValue, range);
            case MODIFIER_MULTIPLY -> new ASTBinaryOp(ASTBinaryOp.Operator.MULTIPLY, null, assignedReference, baseValue, range);
            case MODIFIER_DIVIDE -> new ASTBinaryOp(ASTBinaryOp.Operator.DIVIDE, null, assignedReference, baseValue, range);
            case MODIFIER_MODULO -> new ASTBinaryOp(ASTBinaryOp.Operator.MODULO, null, assignedReference, baseValue, range);
            default -> throw new IllegalArgumentException("Operator is not a valid assignment operator");
        };
    }

    private ASTNode parseExpression(TokenStream stream, List<CompileError> errors) {
        return parseExpression(stream, 0, errors);
    }

    private ASTNode parseExpression(TokenStream stream, int minPrecedence, List<CompileError> errors) {
        if (!stream.hasNext()) {
            return null;
        }
        ScriptToken prefixToken = stream.consume();
        ASTNode left = parsePrefix(prefixToken, stream, errors);
        if (left == null) return null;
        while (stream.hasNext() && infixPrecedence(stream.peek().type()) > minPrecedence) {
            ScriptToken infixToken = stream.consume();
            left = parseInfix(infixToken, left, stream, errors);
            if (left == null) return null;
        }
        return left;
    }

    private ASTNode parsePrefix(ScriptToken token, TokenStream stream, List<CompileError> errors) {
        return switch (token.type()) {
            case INTEGER, FLOAT, STRING, BOOLEAN_TRUE, BOOLEAN_FALSE, NULL -> parseLiteral(token);
            case NAME -> parseName(token, stream, errors);
            case MINUS -> parseUnaryOp(ASTUnaryOp.Operator.NEGATE, token, stream, errors);
            case NOT -> parseUnaryOp(ASTUnaryOp.Operator.NOT, token, stream, errors);
            case PARENTHESIS_OPEN -> parseGroup(token, stream, errors);
            case PLAYER -> parsePlayerRef(token);
            case GLOBAL -> parseGlobalRef(token, stream, errors);
            case GAME_DATA -> parseGameDataRef(token, stream, errors);
            case CONTEXT -> parseContextRef(token, stream, errors);
            case WORLD -> parseWorldRef(token, stream, errors);
            case SET -> parseCollectionConstructor(ASTCollection.Type.SET, ScriptTokenType.BRACKET_OPEN, ScriptTokenType.BRACKET_CLOSE, token, stream, errors);
            case LIST -> parseCollectionConstructor(ASTCollection.Type.LIST, ScriptTokenType.BRACKET_SQUARE_OPEN, ScriptTokenType.BRACKET_SQUARE_CLOSE, token, stream, errors);
            default -> {
                errors.add(new CompileError("Unexpected token in expression", new SourceRange(token)));
                yield null;
            }
        };
    }

    private ASTNode parseInfix(ScriptToken token, ASTNode left, TokenStream stream, List<CompileError> errors) {
        return switch (token.type()) {
            case PLUS -> parseBinaryOp(ASTBinaryOp.Operator.ADD, left, token, stream, errors);
            case MINUS -> parseBinaryOp(ASTBinaryOp.Operator.SUBTRACT, left, token, stream, errors);
            case MULTIPLY -> parseBinaryOp(ASTBinaryOp.Operator.MULTIPLY, left, token, stream, errors);
            case DIVIDE -> parseBinaryOp(ASTBinaryOp.Operator.DIVIDE, left, token, stream, errors);
            case MODULO -> parseBinaryOp(ASTBinaryOp.Operator.MODULO, left, token, stream, errors);
            case POWER -> parseBinaryOp(ASTBinaryOp.Operator.POWER, left, token, stream, errors);
            case AND -> parseBinaryOp(ASTBinaryOp.Operator.AND, left, token, stream, errors);
            case OR -> parseBinaryOp(ASTBinaryOp.Operator.OR, left, token, stream, errors);
            case NULL_COALESCING -> parseBinaryOp(ASTBinaryOp.Operator.NULL_COALESCING, left, token, stream, errors);
            case EQUAL -> parseBinaryOp(ASTBinaryOp.Operator.EQUAL, left, token, stream, errors);
            case NOT_EQUAL -> parseBinaryOp(ASTBinaryOp.Operator.NOT_EQUAL, left, token, stream, errors);
            case GREATER -> parseBinaryOp(ASTBinaryOp.Operator.GREATER, left, token, stream, errors);
            case GREATER_EQUAL -> parseBinaryOp(ASTBinaryOp.Operator.GREATER_EQUAL, left, token, stream, errors);
            case LESS -> parseBinaryOp(ASTBinaryOp.Operator.LESS, left, token, stream, errors);
            case LESS_EQUAL -> parseBinaryOp(ASTBinaryOp.Operator.LESS_EQUAL, left, token, stream, errors);
            case DOT -> parseMemberAccess(left, token, stream, errors);
            case BRACKET_SQUARE_OPEN -> parseListIndex(left, token, stream, errors);
            case TERNARY_IF -> parseTernary(left, token, stream, errors);
            case DOUBLE_COLON -> parseChainedFunctionCall(left, token, stream, errors);
            default -> {
                errors.add(new CompileError("Unexpected token in expression", new SourceRange(token)));
                yield null;
            }
        };
    }

    private int infixPrecedence(ScriptTokenType type) {
        return switch (type) {
            case TERNARY_IF -> 1;
            case NULL_COALESCING -> 2;
            case OR -> 3;
            case AND -> 4;
            case EQUAL, NOT_EQUAL -> 5;
            case LESS, GREATER, LESS_EQUAL, GREATER_EQUAL -> 6;
            case PLUS, MINUS -> 7;
            case MULTIPLY, DIVIDE -> 8;
            case MODULO -> 9;
            case POWER -> 10;
            case DOT, DOUBLE_COLON, BRACKET_SQUARE_OPEN -> 11;
            default -> 0;
        };
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
        return new ASTLiteral(type, token.value(), new SourceRange(token));
    }

    private ASTNode parseName(ScriptToken token, TokenStream stream, List<CompileError> errors) {
        if (stream.expect(ScriptTokenType.PARENTHESIS_OPEN) != null) {
            return parseFunctionCall(token, stream, errors);
        }
        return new ASTVar(token.value(), new SourceRange(token));
    }

    private ASTFunctionCall parseFunctionCall(ScriptToken nameToken, TokenStream stream, List<CompileError> errors) {
        List<ASTNode> arguments = new ArrayList<>();
        boolean hasNamedParameter = false;
        while (stream.hasNext() && stream.peek().type() != ScriptTokenType.PARENTHESIS_CLOSE) {
            if (!arguments.isEmpty()) {
                if (stream.expect(ScriptTokenType.COMMA) == null) {
                    ScriptToken current = stream.peek();
                    errors.add(new CompileError("Expected ',' or ')' in function call", new SourceRange(current)));
                    return null;
                }
            }
            boolean isNamed = stream.peek().type() == ScriptTokenType.NAME && stream.peekNext() != null && stream.peekNext().type() == ScriptTokenType.ASSIGNMENT;
            if (isNamed) {
                hasNamedParameter = true;
            } else if (hasNamedParameter) {
                ScriptToken current = stream.hasNext() ? stream.peek() : stream.current();
                errors.add(new CompileError("Positional parameter cannot follow named parameter", new SourceRange(current)));
                return null;
            }
            ASTNode argument = parseFunctionCallArgument(isNamed, stream, errors);
            if (argument == null) return null;
            arguments.add(argument);
        }
        if (stream.expect(ScriptTokenType.PARENTHESIS_CLOSE) == null) {
            errors.add(new CompileError("Function call is missing closing parenthesis", new SourceRange(nameToken, stream.current())));
            return null;
        }
        return new ASTFunctionCall(new SourceRange(nameToken), nameToken.value(), arguments, new SourceRange(nameToken, stream.current()));
    }

    private ASTNode parseFunctionCallArgument(boolean isNamed, TokenStream stream, List<CompileError> errors) {
        if (isNamed) {
            ScriptToken paramName = stream.consume();
            ScriptToken assignmentOperator = stream.consume();
            ASTNode value = parseExpression(stream, 0, errors);
            if (value == null) return null;
            return new ASTParameter(new SourceRange(paramName), paramName.value(), new SourceRange(assignmentOperator), value, new SourceRange(paramName, stream.current()));
        } else {
            ASTNode value = parseExpression(stream, 0, errors);
            if (value == null) return null;
            return new ASTParameter(null, null, null, value, new SourceRange(value, stream.current()));
        }
    }

    private ASTNode parseUnaryOp(ASTUnaryOp.Operator operator, ScriptToken token, TokenStream stream, List<CompileError> errors) {
        ASTNode operand = parseExpression(stream, UNARY_OP_MIN_PRECEDENCE, errors);
        if (operand == null) return null;
        return new ASTUnaryOp(operator, new SourceRange(token), operand, new SourceRange(token, stream.current()));
    }

    private ASTNode parseGroup(ScriptToken token, TokenStream stream, List<CompileError> errors) {
        ASTNode expression = parseExpression(stream, 0, errors);
        if (expression == null) return null;
        if (stream.expect(ScriptTokenType.PARENTHESIS_CLOSE) == null) {
            errors.add(new CompileError("Expected ')'", new SourceRange(token, stream.current())));
            return null;
        }
        return expression;
    }

    private ASTNode parsePlayerRef(ScriptToken token) {
        return new ASTPlayerRef(new SourceRange(token));
    }

    private ASTNode parseGlobalRef(ScriptToken token, TokenStream stream, List<CompileError> errors) {
        if (stream.expect(ScriptTokenType.BRACKET_SQUARE_OPEN) == null) {
            errors.add(new CompileError("Expected '[' after 'global'", new SourceRange(token)));
            return null;
        }
        ASTNode key = parseExpression(stream, 0, errors);
        if (key == null) return null;
        if (stream.expect(ScriptTokenType.BRACKET_SQUARE_CLOSE) == null) {
            errors.add(new CompileError("Expected ']' in global reference", new SourceRange(token, stream.current())));
            return null;
        }
        return new ASTGlobalRef(new SourceRange(token), key, new SourceRange(token, stream.current()));
    }

    private ASTNode parseGameDataRef(ScriptToken token, TokenStream stream, List<CompileError> errors) {
        if (stream.expect(ScriptTokenType.DOT) == null) {
            errors.add(new CompileError("Expected '.' after 'gameData'", new SourceRange(token)));
            return null;
        }
        ScriptToken dataTypeToken = stream.expect(ScriptTokenType.NAME);
        if (dataTypeToken == null) {
            ScriptToken current = stream.hasNext() ? stream.peek() : stream.current();
            errors.add(new CompileError("Expected data type after 'gameData.'", new SourceRange(current)));
            return null;
        }
        String dataType = dataTypeToken.value();
        if (stream.expect(ScriptTokenType.PARENTHESIS_OPEN) == null) {
            ScriptToken current = stream.hasNext() ? stream.peek() : stream.current();
            errors.add(new CompileError("Expected '(' after data type in gameData reference", new SourceRange(current)));
            return null;
        }
        ASTNode id = parseExpression(stream, 0, errors);
        if (id == null) return null;
        if (stream.expect(ScriptTokenType.PARENTHESIS_CLOSE) == null) {
            errors.add(new CompileError("Expected ')' in gameData reference", new SourceRange(token, stream.current())));
            return null;
        }
        return new ASTGameDataRef(new SourceRange(token), new SourceRange(dataTypeToken), dataType, id, new SourceRange(token, stream.current()));
    }

    private ASTNode parseContextRef(ScriptToken token, TokenStream stream, List<CompileError> errors) {
        if (stream.expect(ScriptTokenType.DOT) == null) {
            errors.add(new CompileError("Expected '.' after 'context'", new SourceRange(token)));
            return null;
        }
        ScriptToken nameToken = stream.expect(ScriptTokenType.NAME);
        if (nameToken == null) {
            ScriptToken current = stream.hasNext() ? stream.peek() : stream.current();
            errors.add(new CompileError("Expected name after 'context.'", new SourceRange(current)));
            return null;
        }
        String name = nameToken.value();
        return new ASTContextRef(new SourceRange(token), new SourceRange(nameToken), name, new SourceRange(token, stream.current()));
    }

    private ASTNode parseWorldRef(ScriptToken token, TokenStream stream, List<CompileError> errors) {
        if (stream.expect(ScriptTokenType.DOT) == null) {
            errors.add(new CompileError("Expected '.' after 'world'", new SourceRange(token)));
            return null;
        }
        ScriptToken nameToken = stream.expect(ScriptTokenType.NAME);
        if (nameToken == null) {
            ScriptToken current = stream.hasNext() ? stream.peek() : stream.current();
            errors.add(new CompileError("Expected name after 'world.'", new SourceRange(current)));
            return null;
        }
        String name = nameToken.value();
        return new ASTWorldRef(new SourceRange(token), new SourceRange(nameToken), name, new SourceRange(token, stream.current()));
    }

    private ASTNode parseCollectionConstructor(ASTCollection.Type type, ScriptTokenType openToken, ScriptTokenType closeToken, ScriptToken token, TokenStream stream, List<CompileError> errors) {
        if (stream.expect(openToken) == null) {
            errors.add(new CompileError("Expected opening bracket in collection", new SourceRange(token)));
            return null;
        }
        List<ASTNode> elements = new ArrayList<>();
        while (stream.hasNext() && stream.peek().type() != closeToken) {
            if (!elements.isEmpty()) {
                if (stream.expect(ScriptTokenType.COMMA) == null) {
                    ScriptToken current = stream.hasNext() ? stream.peek() : stream.current();
                    errors.add(new CompileError("Expected ',' or closing bracket in collection", new SourceRange(current)));
                    return null;
                }
            }
            ASTNode element = parseExpression(stream, 0, errors);
            if (element == null) return null;
            elements.add(element);
        }
        if (stream.expect(closeToken) == null) {
            errors.add(new CompileError("Collection is missing closing bracket", new SourceRange(token, stream.current())));
            return null;
        }
        return new ASTCollection(type, new SourceRange(token), elements, new SourceRange(token, stream.current()));
    }

    private ASTNode parseBinaryOp(ASTBinaryOp.Operator operator, ASTNode left, ScriptToken token, TokenStream stream, List<CompileError> errors) {
        int precedence = infixPrecedence(token.type());
        boolean isRightAssociative = binaryOpIsRightAssociative(operator);
        ASTNode right = parseExpression(stream, isRightAssociative ? precedence - 1 : precedence, errors);
        if (right == null) return null;
        return new ASTBinaryOp(operator, new SourceRange(token), left, right, new SourceRange(left, stream.current()));
    }

    private boolean binaryOpIsRightAssociative(ASTBinaryOp.Operator operator) {
        return operator == ASTBinaryOp.Operator.POWER || operator == ASTBinaryOp.Operator.NULL_COALESCING;
    }

    private ASTNode parseMemberAccess(ASTNode left, ScriptToken token, TokenStream stream, List<CompileError> errors) {
        if (stream.expect(ScriptTokenType.PARENTHESIS_OPEN) != null) {
            ASTNode nameExpression = parseExpression(stream, 0, errors);
            if (nameExpression == null) return null;
            if (stream.expect(ScriptTokenType.PARENTHESIS_CLOSE) == null) {
                errors.add(new CompileError("Expected ')' in dynamic member access", new SourceRange(token, stream.current())));
                return null;
            }
            return new ASTMemberAccess(left, new ASTMemberNameDynamic(nameExpression), new SourceRange(left, stream.current()));
        } else {
            ScriptToken nameToken = stream.expect(ScriptTokenType.NAME);
            if (nameToken == null) {
                errors.add(new CompileError("Expected member name after '.'", new SourceRange(token)));
                return null;
            }
            String name = nameToken.value();
            return new ASTMemberAccess(left, new ASTMemberNameStatic(name, new SourceRange(nameToken)), new SourceRange(left, stream.current()));
        }
    }

    private ASTNode parseListIndex(ASTNode left, ScriptToken token, TokenStream stream, List<CompileError> errors) {
        ASTNode index = parseExpression(stream, 0, errors);
        if (index == null) return null;
        if (stream.expect(ScriptTokenType.BRACKET_SQUARE_CLOSE) == null) {
            errors.add(new CompileError("Expected ']'", new SourceRange(token, stream.current())));
            return null;
        }
        return new ASTListIndexRef(left, index, new SourceRange(left, stream.current()));
    }

    private ASTNode parseTernary(ASTNode condition, ScriptToken token, TokenStream stream, List<CompileError> errors) {
        ScriptToken questionToken = stream.current();
        ASTNode trueValue = parseExpression(stream, 0, errors);
        if (trueValue == null) return null;
        ScriptToken colonToken = stream.expect(ScriptTokenType.COLON);
        if (colonToken == null) {
            errors.add(new CompileError("Expected ':' in ternary expression", new SourceRange(token, stream.current())));
            return null;
        }
        ASTNode falseValue = parseExpression(stream, 0, errors);
        if (falseValue == null) return null;
        return new ASTTernaryOp(condition, new SourceRange(questionToken), trueValue, new SourceRange(colonToken), falseValue, new SourceRange(condition, stream.current()));
    }

    private ASTNode parseChainedFunctionCall(ASTNode left, ScriptToken token, TokenStream stream, List<CompileError> errors) {
        ScriptToken nameToken = stream.consume();
        if (nameToken == null || nameToken.type() != ScriptTokenType.NAME) {
            errors.add(new CompileError("Expected function name after '::'", new SourceRange(token)));
            return null;
        }
        if (stream.expect(ScriptTokenType.PARENTHESIS_OPEN) == null) {
            errors.add(new CompileError("Expected '(' after function name in chained call", new SourceRange(nameToken)));
            return null;
        }
        ASTFunctionCall call = parseFunctionCall(nameToken, stream, errors);
        if (call == null) return null;
        List<ASTNode> parameters = new ArrayList<>();
        parameters.add(new ASTParameter(null, null, null, left, new SourceRange(left, token)));
        parameters.addAll(call.parameters());
        return new ASTFunctionCall(new SourceRange(token, nameToken), call.name(), parameters, new SourceRange(token, stream.current()));
    }

}
