package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.expression.*;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptCompound;
import com.github.finley243.adventureengine.script.ScriptSetVariable;
import com.github.finley243.adventureengine.stat.StatHolderReference;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptParser {

    private enum ScriptTokenType {
        END_LINE, STRING, FLOAT, INTEGER, NAME, EQUALS, COMMA, DOT, PLUS, MINUS, DIVIDE, MULTIPLY, MODULO, PARENTHESIS_OPEN, PARENTHESIS_CLOSE, BRACKET_OPEN, BRACKET_CLOSE, BOOLEAN_TRUE, BOOLEAN_FALSE
    }

    private static final String REGEX_PATTERN = "/\\*[.*]+\\*/|//.*\n|\"(\\\\\"|[^\"])*\"|'(\\\\'|[^'])*'|_?[a-zA-Z][a-zA-Z0-9_]*|([0-9]*\\.[0-9]+|[0-9]+\\.?[0-9]*)f|[0-9]+|;|=|,|\\.|\\+|-|/|\\*|%|\\(|\\)|\\{|\\}";

    public static List<ScriptData> parseScripts(String scriptText) {
        List<ScriptData> scripts = new ArrayList<>();
        List<ScriptToken> tokens = parseToTokens(scriptText);
        List<ScriptTokenFunction> functions = groupTokensToFunctions(tokens);
        for (ScriptTokenFunction function : functions) {
            ScriptData script = parseFunction(function);
            scripts.add(script);
        }
        return scripts;
    }

    private static List<ScriptToken> parseToTokens(String scriptText) {
        List<ScriptToken> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile(REGEX_PATTERN);
        Matcher matcher = pattern.matcher(scriptText);
        while (matcher.find()) {
            String currentToken = matcher.group();
            if (currentToken.startsWith("\"") && currentToken.endsWith("\"") && currentToken.length() > 1) {
                String value = currentToken.substring(1, currentToken.length() - 1);
                value = value.replaceAll("\\\\\"", "\"");
                tokens.add(new ScriptToken(ScriptTokenType.STRING, value));
            } else if (currentToken.startsWith("'") && currentToken.endsWith("'") && currentToken.length() > 1) {
                String value = currentToken.substring(1, currentToken.length() - 1);
                value = value.replaceAll("\\\\'", "'");
                tokens.add(new ScriptToken(ScriptTokenType.STRING, value));
            } else if (currentToken.matches("([0-9]*\\.[0-9]+|[0-9]+\\.?[0-9]*)f")) {
                String value = currentToken.substring(0, currentToken.length() - 1);
                tokens.add(new ScriptToken(ScriptTokenType.FLOAT, value));
            } else if (currentToken.matches("[0-9]+")) {
                tokens.add(new ScriptToken(ScriptTokenType.INTEGER, currentToken));
            } else if (currentToken.equals(";")) {
                tokens.add(new ScriptToken(ScriptTokenType.END_LINE));
            } else if (currentToken.equals("=")) {
                tokens.add(new ScriptToken(ScriptTokenType.EQUALS));
            } else if (currentToken.equals(",")) {
                tokens.add(new ScriptToken(ScriptTokenType.COMMA));
            } else if (currentToken.equals(".")) {
                tokens.add(new ScriptToken(ScriptTokenType.DOT));
            } else if (currentToken.equals("+")) {
                tokens.add(new ScriptToken(ScriptTokenType.PLUS));
            } else if (currentToken.equals("-")) {
                tokens.add(new ScriptToken(ScriptTokenType.MINUS));
            } else if (currentToken.equals("/")) {
                tokens.add(new ScriptToken(ScriptTokenType.DIVIDE));
            } else if (currentToken.equals("*")) {
                tokens.add(new ScriptToken(ScriptTokenType.MULTIPLY));
            } else if (currentToken.equals("%")) {
                tokens.add(new ScriptToken(ScriptTokenType.MODULO));
            } else if (currentToken.equals("(")) {
                tokens.add(new ScriptToken(ScriptTokenType.PARENTHESIS_OPEN));
            } else if (currentToken.equals(")")) {
                tokens.add(new ScriptToken(ScriptTokenType.PARENTHESIS_CLOSE));
            } else if (currentToken.equals("{")) {
                tokens.add(new ScriptToken(ScriptTokenType.BRACKET_OPEN));
            } else if (currentToken.equals("}")) {
                tokens.add(new ScriptToken(ScriptTokenType.BRACKET_CLOSE));
            } else if (currentToken.equals("true")) {
                tokens.add(new ScriptToken(ScriptTokenType.BOOLEAN_TRUE));
            } else if (currentToken.equals("false")) {
                tokens.add(new ScriptToken(ScriptTokenType.BOOLEAN_FALSE));
            } else if (currentToken.matches("_?[a-zA-Z][a-zA-Z0-9_]*")) {
                tokens.add(new ScriptToken(ScriptTokenType.NAME, currentToken));
            }
        }
        return tokens;
    }

    private static List<ScriptTokenFunction> groupTokensToFunctions(List<ScriptToken> tokens) {
        List<ScriptTokenFunction> tokenFunctions = new ArrayList<>();
        int index = 0;
        while (index < tokens.size()) {
            int parameterStartIndex = findFirstTokenIndex(tokens, ScriptTokenType.PARENTHESIS_OPEN, index);
            int parameterEndIndex = findPairedClosingBracket(tokens, parameterStartIndex);
            int bodyStartIndex = parameterEndIndex + 1;
            if (tokens.get(bodyStartIndex).type != ScriptTokenType.BRACKET_OPEN) throw new IllegalArgumentException("Script has invalid header");
            int bodyEndIndex = findPairedClosingBracket(tokens, bodyStartIndex);
            List<ScriptToken> header = tokens.subList(index, parameterStartIndex);
            List<ScriptToken> parameters = tokens.subList(parameterStartIndex, parameterEndIndex + 1);
            List<ScriptToken> body = tokens.subList(bodyStartIndex, bodyEndIndex + 1);
            tokenFunctions.add(new ScriptTokenFunction(header, parameters, body));
            index = bodyEndIndex + 1;
        }
        return tokenFunctions;
    }

    private static ScriptData parseFunction(ScriptTokenFunction functionTokens) {
        if (functionTokens.header().getFirst().type != ScriptTokenType.NAME || !functionTokens.header().getFirst().value.equals("func")) {
            throw new IllegalArgumentException("Function header is missing func keyword");
        }
        if (functionTokens.header().getLast().type != ScriptTokenType.NAME) {
            throw new IllegalArgumentException("Function header is missing valid name");
        }
        String functionName = functionTokens.header().getLast().value;
        Expression.DataType functionReturnType = null;
        if (functionTokens.header().size() == 3) {
            if (functionTokens.header().get(1).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function header has invalid return type");
            functionReturnType = stringToDataType(functionTokens.header().get(1).value);
        } else if (functionTokens.header().size() != 2) {
            throw new IllegalArgumentException("Function header contains unexpected tokens");
        }
        Set<ScriptParameter> functionParameters = parseFunctionParameters(functionTokens.parameters());
        Script functionScript = generateScript(functionTokens.body());
        return new ScriptData(functionName, functionReturnType, functionParameters, functionScript);
    }

    private static Set<ScriptParameter> parseFunctionParameters(List<ScriptToken> parameterTokens) {
        Set<ScriptParameter> functionParameters = new HashSet<>();
        List<List<ScriptToken>> parameterGroups = new ArrayList<>();
        int index = 0;
        while (index < parameterTokens.size()) {
            int nextCommaIndex = findFirstTokenIndex(parameterTokens, ScriptTokenType.COMMA, index);
            List<ScriptToken> currentGroup;
            if (nextCommaIndex == -1) {
                currentGroup = parameterTokens.subList(index, parameterTokens.size());
            } else {
                currentGroup = parameterTokens.subList(index, nextCommaIndex);
            }
            if (currentGroup.isEmpty() || currentGroup.size() == 2) throw new IllegalArgumentException("Function contains invalid parameter definition");
            if (currentGroup.getFirst().type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function contains invalid parameter definition");
            if (currentGroup.size() >= 3 && currentGroup.get(1).type != ScriptTokenType.EQUALS) throw new IllegalArgumentException("Function contains invalid parameter definition");
            parameterGroups.add(currentGroup);
        }
        for (List<ScriptToken> parameterGroup : parameterGroups) {
            String parameterName = parameterGroup.getFirst().value;
            Expression parameterDefaultValue = null;
            if (parameterGroup.size() >= 3) {
                parameterDefaultValue = parseLiteral(parameterGroup.subList(2, parameterGroup.size()));
            }
            functionParameters.add(new ScriptParameter(parameterName, parameterDefaultValue));
        }
        return functionParameters;
    }

    private static Script generateScript(List<ScriptToken> tokens) {
        if (tokens.isEmpty()) return null;
        if (tokens.get(0).type == ScriptTokenType.BRACKET_OPEN && tokens.getLast().type == ScriptTokenType.BRACKET_CLOSE) {
            // Script block
            List<Script> scripts = new ArrayList<>();
            List<ScriptToken> currentScriptTokens = new ArrayList<>();
            int bracketDepth = 0;
            for (int i = 1; i < tokens.size() - 1; i++) {
                currentScriptTokens.add(tokens.get(i));
                if (bracketDepth == 0 && tokens.get(i).type == ScriptTokenType.END_LINE) {
                    Script currentScript = generateScript(currentScriptTokens);
                    scripts.add(currentScript);
                    currentScriptTokens.clear();
                } else if (tokens.get(i).type == ScriptTokenType.BRACKET_OPEN) {
                    bracketDepth += 1;
                } else if (tokens.get(i).type == ScriptTokenType.BRACKET_CLOSE) {
                    bracketDepth -= 1;
                    if (bracketDepth == 0 && !(i + 1 < tokens.size() - 1 && tokens.get(i + 1).type == ScriptTokenType.NAME && tokens.get(i + 1).value.equals("else"))) {
                        Script bracketScript = generateScript(currentScriptTokens);
                        scripts.add(bracketScript);
                        currentScriptTokens.clear();
                    }
                }
            }
            // TODO - Allow for sequential, separated (non-chained) if-statements (requires checking for if/else keywords explicitly)
            if (!currentScriptTokens.isEmpty()) {
                throw new IllegalArgumentException("Script is improperly terminated");
            }
            return new ScriptCompound(null, scripts, false);
        } else if (tokens.get(0).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            // Expression block

        } else if (tokens.get(0).type == ScriptTokenType.NAME && tokens.get(0).value.equals("if")) {
            // If statement
            if (tokens.get(1).type != ScriptTokenType.PARENTHESIS_OPEN) {
                throw new IllegalArgumentException("If statement condition is malformed");
            }
            List<ScriptToken> conditionTokens = new ArrayList<>();
            int parenthesisDepth = 0;
            int i = 1;
            while (i < tokens.size()) {
                conditionTokens.add(tokens.get(i));
                if (tokens.get(i).type == ScriptTokenType.PARENTHESIS_OPEN) {
                    parenthesisDepth += 1;
                } else if (tokens.get(i).type == ScriptTokenType.PARENTHESIS_CLOSE) {
                    parenthesisDepth -= 1;
                    if (parenthesisDepth == 0) {
                        break;
                    }
                }
                i += 1;
            }
            if (conditionTokens.size() <= 2) {
                throw new IllegalArgumentException("If statement condition is malformed");
            }
            Script condition = generateScript(conditionTokens);

        } else if (tokens.get(0).type == ScriptTokenType.NAME && tokens.get(0).value.equals("var")) {
            // Local variable definition
            if (tokens.get(1).type != ScriptTokenType.NAME) {
                throw new IllegalArgumentException("Variable declaration has no specified name");
            }
            if (tokens.getLast().type != ScriptTokenType.END_LINE) {
                throw new IllegalArgumentException("Variable declaration has invalid line end");
            }
            String variableName = tokens.get(1).value;
            Expression variableValue = null;
            if (tokens.size() > 2 && tokens.get(2).type == ScriptTokenType.EQUALS) {
                variableValue = generateExpression(tokens.subList(3, tokens.size() - 1));
            }
            return new ScriptSetVariable(null, Expression.constant(variableName), variableValue);
        } else if (tokens.get(0).type == ScriptTokenType.NAME && tokens.get(0).value.equals("stat")) {
            // Stat reference

        } else if (tokens.get(0).type == ScriptTokenType.NAME && tokens.get(1).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.get(tokens.size() - 2).type == ScriptTokenType.PARENTHESIS_CLOSE) {
            // Named function call

        } else if (tokens.get(0).type == ScriptTokenType.NAME && tokens.get(1).type == ScriptTokenType.EQUALS) {
            // Local variable reference
            if (tokens.getLast().type != ScriptTokenType.END_LINE) {
                throw new IllegalArgumentException("Variable assignment has invalid line end");
            }
            if (tokens.size() <= 3) {
                throw new IllegalArgumentException("Variable assignment has no specified value");
            }
            String variableName = tokens.getFirst().value;
            Expression variableValue = generateExpression(tokens.subList(2, tokens.size() - 1));
            return new ScriptSetVariable(null, Expression.constant(variableName), variableValue);
        }
        return null;
    }

    private static Expression generateExpression(List<ScriptToken> tokens) {
        if (tokens.isEmpty()) return null;
        if (tokens.size() == 1) {
            ScriptToken token = tokens.getFirst();
            if (token.type == ScriptTokenType.STRING) {
                return Expression.constant(token.value);
            } else if (token.type == ScriptTokenType.INTEGER) {
                int value = Integer.parseInt(token.value);
                return Expression.constant(value);
            } else if (token.type == ScriptTokenType.FLOAT) {
                float value = Float.parseFloat(token.value);
                return Expression.constant(value);
            } else if (token.type == ScriptTokenType.BOOLEAN_TRUE) {
                return Expression.constant(true);
            } else if (token.type == ScriptTokenType.BOOLEAN_FALSE) {
                return Expression.constant(false);
            } else if (token.type == ScriptTokenType.NAME) {
                return new ExpressionParameter(token.value);
            }
        } else if (tokens.get(0).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            return generateExpression(tokens.subList(1, tokens.size() - 1));
        } else {
            int priorityOperator = getPriorityOperator(tokens);
            if (priorityOperator != -1) {
                Expression preOperator = generateExpression(tokens.subList(0, priorityOperator));
                Expression postOperator = generateExpression(tokens.subList(priorityOperator + 1, tokens.size()));
                return switch (tokens.get(priorityOperator).type) {
                    case MULTIPLY -> new ExpressionMultiply(List.of(preOperator, postOperator));
                    case DIVIDE -> new ExpressionDivide(preOperator, postOperator);
                    case MODULO -> new ExpressionModulo(preOperator, postOperator);
                    case PLUS -> new ExpressionAdd(List.of(preOperator, postOperator));
                    case MINUS -> new ExpressionSubtract(preOperator, postOperator);
                    default -> null;
                };
            }
            if (tokens.get(0).type == ScriptTokenType.NAME && tokens.get(0).value.equals("stat") && tokens.get(1).type == ScriptTokenType.DOT) {
                StatHolderReference parentHolder = null;
                int bracketDepth = 0;
                List<ScriptToken> stringExpression = new ArrayList<>();
                for (int i = 2; i < tokens.size(); i++) {
                    ScriptToken token = tokens.get(i);

                }
            }
            // TODO - Handle non-mathematical expressions
        }
        return null;
    }

    private static Expression parseLiteral(List<ScriptToken> tokens) {
        if (tokens.size() != 1) throw new IllegalArgumentException("Expression is not a valid literal");
        ScriptToken token = tokens.getFirst();
        if (token.type == ScriptTokenType.STRING) {
            return Expression.constant(token.value);
        } else if (token.type == ScriptTokenType.INTEGER) {
            int value = Integer.parseInt(token.value);
            return Expression.constant(value);
        } else if (token.type == ScriptTokenType.FLOAT) {
            float value = Float.parseFloat(token.value);
            return Expression.constant(value);
        } else if (token.type == ScriptTokenType.BOOLEAN_TRUE) {
            return Expression.constant(true);
        } else if (token.type == ScriptTokenType.BOOLEAN_FALSE) {
            return Expression.constant(false);
        } else {
            throw new IllegalArgumentException("Expression is not a valid literal");
        }
    }

    private static int getPriorityOperator(List<ScriptToken> tokens) {
        int bracketDepth = 0;
        int priorityOperator = -1;
        // Iterated in reverse order to make operators left-associative
        for (int i = tokens.size() - 1; i >= 0; i--) {
            ScriptToken currentToken = tokens.get(i);
            if (currentToken.type == ScriptTokenType.PARENTHESIS_OPEN) {
                bracketDepth += 1;
            } else if (currentToken.type == ScriptTokenType.PARENTHESIS_CLOSE) {
                bracketDepth -= 1;
            } else if (bracketDepth == 0 && getOperationPriority(currentToken.type) > (priorityOperator == -1 ? -1 : getOperationPriority(tokens.get(priorityOperator).type))) {
                priorityOperator = i;
            }
        }
        return priorityOperator;
    }

    private static int findFirstTokenIndex(List<ScriptToken> tokens, ScriptTokenType type, int startIndex) {
        Deque<ScriptTokenType> bracketStack = new ArrayDeque<>();
        for (int i = startIndex; i < tokens.size(); i++) {
            ScriptToken token = tokens.get(i);
            if (bracketStack.isEmpty() && token.type == type) {
                return i;
            } else if (token.type == ScriptTokenType.BRACKET_OPEN) {
                bracketStack.push(ScriptTokenType.BRACKET_OPEN);
            } else if (token.type == ScriptTokenType.BRACKET_CLOSE && bracketStack.peek() == ScriptTokenType.BRACKET_OPEN) {
                bracketStack.pop();
            } else if (token.type == ScriptTokenType.PARENTHESIS_OPEN) {
                bracketStack.push(ScriptTokenType.PARENTHESIS_OPEN);
            } else if (token.type == ScriptTokenType.PARENTHESIS_CLOSE && bracketStack.peek() == ScriptTokenType.PARENTHESIS_OPEN) {
                bracketStack.pop();
            }
        }
        return -1;
    }

    private static int findLastTokenIndex(List<ScriptToken> tokens, ScriptTokenType type, int startIndex) {
        Deque<ScriptTokenType> bracketStack = new ArrayDeque<>();
        for (int i = startIndex; i >= 0; i--) {
            ScriptToken token = tokens.get(i);
            if (bracketStack.isEmpty() && token.type == type) {
                return i;
            } else if (token.type == ScriptTokenType.BRACKET_CLOSE) {
                bracketStack.push(ScriptTokenType.BRACKET_CLOSE);
            } else if (token.type == ScriptTokenType.BRACKET_OPEN && bracketStack.peek() == ScriptTokenType.BRACKET_CLOSE) {
                bracketStack.pop();
            } else if (token.type == ScriptTokenType.PARENTHESIS_CLOSE) {
                bracketStack.push(ScriptTokenType.PARENTHESIS_CLOSE);
            } else if (token.type == ScriptTokenType.PARENTHESIS_OPEN && bracketStack.peek() == ScriptTokenType.PARENTHESIS_CLOSE) {
                bracketStack.pop();
            }
        }
        return -1;
    }

    private static int findPairedClosingBracket(List<ScriptToken> tokens, int openBracketIndex) {
        ScriptTokenType targetBracketType;
        switch (tokens.get(openBracketIndex).type) {
            case PARENTHESIS_OPEN -> targetBracketType = ScriptTokenType.PARENTHESIS_CLOSE;
            case BRACKET_OPEN -> targetBracketType = ScriptTokenType.BRACKET_CLOSE;
            default -> throw new IllegalArgumentException("Specified token is not a valid type of open bracket");
        }
        Deque<ScriptTokenType> bracketStack = new ArrayDeque<>();
        for (int i = openBracketIndex + 1; i < tokens.size(); i++) {
            ScriptToken token = tokens.get(i);
            if (bracketStack.isEmpty() && token.type == targetBracketType) {
                return i;
            } else if (token.type == ScriptTokenType.BRACKET_OPEN) {
                bracketStack.push(ScriptTokenType.BRACKET_OPEN);
            } else if (token.type == ScriptTokenType.BRACKET_CLOSE && bracketStack.peek() == ScriptTokenType.BRACKET_OPEN) {
                bracketStack.pop();
            } else if (token.type == ScriptTokenType.PARENTHESIS_OPEN) {
                bracketStack.push(ScriptTokenType.PARENTHESIS_OPEN);
            } else if (token.type == ScriptTokenType.PARENTHESIS_CLOSE && bracketStack.peek() == ScriptTokenType.PARENTHESIS_OPEN) {
                bracketStack.pop();
            }
        }
        return -1;
    }

    private static int getOperationPriority(ScriptTokenType tokenType) {
        if (tokenType == ScriptTokenType.MULTIPLY) {
            return 0;
        } else if (tokenType == ScriptTokenType.DIVIDE) {
            return 0;
        } else if (tokenType == ScriptTokenType.MODULO) {
            return 0;
        } else if (tokenType == ScriptTokenType.PLUS) {
            return 1;
        } else if (tokenType == ScriptTokenType.MINUS) {
            return 1;
        }
        return -1;
    }

    private static Expression.DataType stringToDataType(String name) {
        return switch (name) {
            case "boolean" -> Expression.DataType.BOOLEAN;
            case "int" -> Expression.DataType.INTEGER;
            case "float" -> Expression.DataType.FLOAT;
            case "string" -> Expression.DataType.STRING;
            case "stringSet" -> Expression.DataType.STRING_SET;
            case "inventory" -> Expression.DataType.INVENTORY;
            case "noun" -> Expression.DataType.NOUN;
            case "void" -> null;
            default -> throw new IllegalArgumentException("Invalid data type name: " + name);
        };
    }

    public record ScriptData(String name, Expression.DataType returnType, Set<ScriptParameter> parameters, Script script) {}

    private static class ScriptToken {
        public final ScriptTokenType type;
        public final String value;

        public ScriptToken(ScriptTokenType type) {
            this.type = type;
            this.value = null;
        }

        public ScriptToken(ScriptTokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return type.toString() + (value != null ? ":" + value : "");
        }
    }

    private record ScriptParameter(String name, Expression defaultValue) {}

    private record ScriptTokenFunction(List<ScriptToken> header, List<ScriptToken> parameters, List<ScriptToken> body) {}

}
