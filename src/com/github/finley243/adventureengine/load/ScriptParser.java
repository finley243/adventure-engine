package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.expression.*;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptCompound;

import javax.print.attribute.standard.MediaSize;
import java.lang.foreign.AddressLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
            String functionName;
            Expression.DataType functionDataType = null;
            Set<String> parameterNames = new HashSet<>();
            Set<ScriptParameter> functionParameters = new HashSet<>();
            if (function.header().size() == 2) {
                if (function.header().get(0).type != ScriptTokenType.NAME || !"func".equals(function.header().get(0).value)) throw new IllegalArgumentException("Function is missing function identifier");
                if (function.header().get(1).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function has invalid name");
                functionName = function.header().get(1).value;
            } else if (function.header().size() == 3) {
                if (function.header().get(0).type != ScriptTokenType.NAME || !"func".equals(function.header().get(0).value)) throw new IllegalArgumentException("Function is missing function identifier");
                if (function.header().get(1).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function has invalid data type");
                functionDataType = stringToDataType(function.header().get(1).value);
                if (functionDataType == null && !function.header().get(1).value.equals("void")) {
                    throw new IllegalArgumentException("Function has invalid data type");
                }
                if (function.header().get(2).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function has invalid name");
                functionName = function.header().get(2).value;
            } else {
                throw new IllegalArgumentException("Function has malformed header");
            }
            String parameterName = null;
            boolean parameterHasDefault = false;
            Expression parameterDefaultValue = null;
            for (int i = 0; i < function.parameterBlock().size(); i++) {
                ScriptToken currentToken = function.parameterBlock().get(i);
                if (currentToken.type == ScriptTokenType.NAME) {
                    if (parameterName == null) {
                        parameterName = currentToken.value;
                    } else if (parameterHasDefault) {
                        throw new IllegalArgumentException("Function parameter default value cannot be a variable");
                    } else {
                        throw new IllegalArgumentException("Function has malformed parameter");
                    }
                } else if (currentToken.type == ScriptTokenType.COMMA) {
                    if (i == function.parameterBlock().size() - 1) {
                        throw new IllegalArgumentException("Function parameters contain trailing comma");
                    }
                    if (parameterName != null) {
                        if (!parameterHasDefault || parameterDefaultValue != null) {
                            parameterNames.add(parameterName);
                            functionParameters.add(new ScriptParameter(parameterName, parameterDefaultValue));
                            parameterName = null;
                            parameterHasDefault = false;
                            parameterDefaultValue = null;
                        } else {
                            throw new IllegalArgumentException("Function has malformed parameter");
                        }
                    } else {
                        throw new IllegalArgumentException("Function has malformed parameter");
                    }
                } else if (currentToken.type == ScriptTokenType.EQUALS) {
                    if (parameterName != null && !parameterHasDefault) {
                        parameterHasDefault = true;
                    } else {
                        throw new IllegalArgumentException("Function has malformed parameter");
                    }
                } else if (currentToken.type == ScriptTokenType.STRING) {
                    if (parameterName != null && parameterHasDefault) {
                        parameterDefaultValue = Expression.constant(currentToken.value);
                    } else {
                        throw new IllegalArgumentException("Function has malformed parameter");
                    }
                } else if (currentToken.type == ScriptTokenType.INTEGER) {
                    if (parameterName != null && parameterHasDefault) {
                        int value = Integer.parseInt(currentToken.value);
                        parameterDefaultValue = Expression.constant(value);
                    } else {
                        throw new IllegalArgumentException("Function has malformed parameter");
                    }
                } else if (currentToken.type == ScriptTokenType.FLOAT) {
                    if (parameterName != null && parameterHasDefault) {
                        float value = Float.parseFloat(currentToken.value);
                        parameterDefaultValue = Expression.constant(value);
                    } else {
                        throw new IllegalArgumentException("Function has malformed parameter");
                    }
                } else if (currentToken.type == ScriptTokenType.BOOLEAN_TRUE) {
                    if (parameterName != null && parameterHasDefault) {
                        parameterDefaultValue = Expression.constant(true);
                    } else {
                        throw new IllegalArgumentException("Function has malformed parameter");
                    }
                } else if (currentToken.type == ScriptTokenType.BOOLEAN_FALSE) {
                    if (parameterName != null && parameterHasDefault) {
                        parameterDefaultValue = Expression.constant(false);
                    } else {
                        throw new IllegalArgumentException("Function has malformed parameter");
                    }
                } else {
                    throw new IllegalArgumentException("Function has illegal token in parameter block");
                }
            }
            if (parameterName != null) {
                if (!parameterHasDefault || parameterDefaultValue != null) {
                    parameterNames.add(parameterName);
                    functionParameters.add(new ScriptParameter(parameterName, parameterDefaultValue));
                } else {
                    throw new IllegalArgumentException("Function has malformed parameter");
                }
            }
            Script functionScript = generateScript(function.body());
            // GENERATE SCRIPT OBJECTS
            scripts.add(new ScriptData(functionName, functionDataType, functionParameters, functionScript));
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
        int currentSection = 3; // 0=header, 1=parameterBlock, 2=body, 3=terminated
        List<ScriptToken> header = new ArrayList<>();
        List<ScriptToken> parameterBlock = new ArrayList<>();
        List<ScriptToken> body = new ArrayList<>();
        int index = 0;
        int bracketDepth = 0;
        while (index < tokens.size()) {
            ScriptToken current = tokens.get(index);
            if (currentSection == 3) {
                currentSection = 0;
            }
            if (currentSection == 0) {
                if (current.type == ScriptTokenType.PARENTHESIS_OPEN) {
                    currentSection = 1;
                } else {
                    header.add(current);
                }
            } else if (currentSection == 1) {
                if (current.type == ScriptTokenType.PARENTHESIS_CLOSE) {
                    currentSection = 2;
                } else {
                    parameterBlock.add(current);
                }
            } else { // currentSection == 2
                if (current.type == ScriptTokenType.BRACKET_OPEN) {
                    body.add(current);
                    bracketDepth += 1;
                } else if (current.type == ScriptTokenType.BRACKET_CLOSE) {
                    bracketDepth -= 1;
                    body.add(current);
                    if (bracketDepth == 0) {
                        currentSection = 3;
                        tokenFunctions.add(new ScriptTokenFunction(header, parameterBlock, body));
                        header = new ArrayList<>();
                        parameterBlock = new ArrayList<>();
                        body = new ArrayList<>();
                    }
                } else {
                    body.add(current);
                }
            }
            index++;
        }
        if (currentSection != 3) throw new IllegalArgumentException("Script is incomplete");
        if (bracketDepth != 0) throw new IllegalArgumentException("Script contains unpaired brackets");
        return tokenFunctions;
    }

    private static Script generateScript(List<ScriptToken> tokens) {
        if (tokens.isEmpty()) return null;
        if (tokens.get(0).type == ScriptTokenType.BRACKET_OPEN && tokens.get(tokens.size() - 1).type == ScriptTokenType.BRACKET_CLOSE) {
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
        } else if (tokens.get(0).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.get(tokens.size() - 1).type == ScriptTokenType.PARENTHESIS_CLOSE) {
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

        } else if (tokens.get(0).type == ScriptTokenType.NAME && tokens.get(0).value.equals("stat")) {
            // Stat reference

        } else if (tokens.get(0).type == ScriptTokenType.NAME && tokens.get(1).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.get(tokens.size() - 2).type == ScriptTokenType.PARENTHESIS_CLOSE) {
            // Named function call

        } else if (tokens.get(0).type == ScriptTokenType.NAME) {
            // Local variable reference

        }
        return null;
    }

    private static Expression generateExpression(List<ScriptToken> tokens, String dataType) {
        if (tokens.isEmpty()) return null;
        if (tokens.size() == 1) {
            ScriptToken token = tokens.get(0);
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
                return new ExpressionParameter(dataType, token.value);
            }
        } else if (tokens.get(0).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.get(tokens.size() - 1).type == ScriptTokenType.PARENTHESIS_CLOSE) {
            return generateExpression(tokens.subList(1, tokens.size() - 1), dataType);
        } else {
            int priorityOperator = getPriorityOperator(tokens);
            if (priorityOperator != -1) {
                Expression preOperator = generateExpression(tokens.subList(0, priorityOperator), dataType);
                Expression postOperator = generateExpression(tokens.subList(priorityOperator + 1, tokens.size()), dataType);
                return switch (tokens.get(priorityOperator).type) {
                    case MULTIPLY -> new ExpressionMultiply(List.of(preOperator, postOperator));
                    case DIVIDE -> new ExpressionDivide(preOperator, postOperator);
                    case MODULO -> new ExpressionModulo(preOperator, postOperator);
                    case PLUS -> new ExpressionAdd(List.of(preOperator, postOperator));
                    case MINUS -> new ExpressionSubtract(preOperator, postOperator);
                    default -> null;
                };
            }
            // TODO - Handle non-mathematical expressions
        }
        return null;
    }

    private static int getPriorityOperator(List<ScriptToken> tokens) {
        int bracketDepth = 0;
        int priorityOperator = -1;
        for (int i = 0; i < tokens.size(); i++) {
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
            default -> null;
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

    private record ScriptTokenFunction(List<ScriptToken> header, List<ScriptToken> parameterBlock, List<ScriptToken> body) {}

}
