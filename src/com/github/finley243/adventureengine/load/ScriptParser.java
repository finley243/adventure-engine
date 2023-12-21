package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptCompound;

import javax.print.attribute.standard.MediaSize;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptParser {

    private enum ScriptTokenType {
        END_LINE, STRING, FLOAT, INTEGER, NAME, EQUALS, COMMA, DOT, PLUS, MINUS, DIVIDE, MULTIPLY, PARENTHESIS_OPEN, PARENTHESIS_CLOSE, BRACKET_OPEN, BRACKET_CLOSE
    }

    private static final String REGEX_PATTERN = "/\\*[.*]+\\*/|//.*\n|\"(\\\\\"|[^\"])*\"|'(\\\\'|[^'])*'|_?[a-zA-Z][a-zA-Z0-9_]*|([0-9]*\\.[0-9]+|[0-9]+\\.?[0-9]*)f|[0-9]+|;|=|,|\\.|\\+|-|/|\\*|\\(|\\)|\\{|\\}";

    public static List<ScriptData> parseScripts(String scriptText) {
        List<ScriptData> scripts = new ArrayList<>();
        List<ScriptToken> tokens = parseToTokens(scriptText);
        List<ScriptTokenFunction> functions = groupTokensToFunctions(tokens);
        for (ScriptTokenFunction function : functions) {
            String functionName;
            Expression.DataType functionDataType = null;
            Set<String> functionParameters = new HashSet<>();
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
            for (int i = 0; i < function.parameterBlock().size(); i++) {
                if (i % 2 == 0) {
                    if (function.parameterBlock().get(i).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function has invalid parameter block");
                    String parameterValue = function.parameterBlock().get(i).value;
                    if (functionParameters.contains(parameterValue)) throw new IllegalArgumentException("Function has duplicate parameter");
                    functionParameters.add(parameterValue);
                } else {
                    if (function.parameterBlock().get(i).type != ScriptTokenType.COMMA) throw new IllegalArgumentException("Function has invalid parameter block");
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
            } else if (currentToken.equals("(")) {
                tokens.add(new ScriptToken(ScriptTokenType.PARENTHESIS_OPEN));
            } else if (currentToken.equals(")")) {
                tokens.add(new ScriptToken(ScriptTokenType.PARENTHESIS_CLOSE));
            } else if (currentToken.equals("{")) {
                tokens.add(new ScriptToken(ScriptTokenType.BRACKET_OPEN));
            } else if (currentToken.equals("}")) {
                tokens.add(new ScriptToken(ScriptTokenType.BRACKET_CLOSE));
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
            if (tokens.get(0).type != ScriptTokenType.PARENTHESIS_OPEN) {
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

    public record ScriptData(String name, Expression.DataType returnType, Set<String> parameters, Script script) {}

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

    private record ScriptTokenFunction(List<ScriptToken> header, List<ScriptToken> parameterBlock, List<ScriptToken> body) {}

}
