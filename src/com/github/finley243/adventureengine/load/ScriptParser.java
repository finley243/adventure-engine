package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.Script;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
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
            String functionName = null;
            Expression.DataType functionDataType = null;
            List<String> functionParameters = new ArrayList<>();
            if (function.header().size() == 2) {
                if (function.header().get(0).type != ScriptTokenType.NAME || !"func".equals(function.header().get(0).value)) throw new IllegalArgumentException("Function is missing function identifier");
                if (function.header().get(1).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function has invalid name");
                functionName = function.header().get(1).value;
            } else if (function.header().size() == 3) {
                if (function.header().get(0).type != ScriptTokenType.NAME || !"func".equals(function.header().get(0).value)) throw new IllegalArgumentException("Function is missing function identifier");
                if (function.header().get(1).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function has invalid data type");
                switch (function.header().get(1).value) {
                    case "void" -> functionDataType = null;
                    case "int" -> functionDataType = Expression.DataType.INTEGER;
                    case "float" -> functionDataType = Expression.DataType.FLOAT;
                    case "boolean" -> functionDataType = Expression.DataType.BOOLEAN;
                    case "string" -> functionDataType = Expression.DataType.STRING;
                    case "stringSet" -> functionDataType = Expression.DataType.STRING_SET;
                    case "inventory" -> functionDataType = Expression.DataType.INVENTORY;
                    case "noun" -> functionDataType = Expression.DataType.NOUN;
                    default -> throw new IllegalArgumentException("Function has invalid data type");
                }
                if (function.header().get(2).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function has invalid name");
                functionName = function.header().get(2).value;
            } else {
                throw new IllegalArgumentException("Function has malformed header");
            }
            for (int i = 0; i < function.parameterBlock().size(); i++) {
                if (i % 2 == 0) {
                    if (function.parameterBlock().get(i).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function has invalid parameter block");
                    functionParameters.add(function.parameterBlock().get(i).value);
                } else {
                    if (function.parameterBlock().get(i).type != ScriptTokenType.COMMA) throw new IllegalArgumentException("Function has invalid parameter block");
                }
            }

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
                    if (bracketDepth > 0) {
                        body.add(current);
                    }
                    bracketDepth += 1;
                } else if (current.type == ScriptTokenType.BRACKET_CLOSE) {
                    bracketDepth -= 1;
                    if (bracketDepth == 0) {
                        currentSection = 3;
                        tokenFunctions.add(new ScriptTokenFunction(header, parameterBlock, body));
                        header = new ArrayList<>();
                        parameterBlock = new ArrayList<>();
                        body = new ArrayList<>();
                    } else {
                        body.add(current);
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

    public record ScriptData(String name, Expression.DataType returnType, Script script) {}

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
    }

    private record ScriptTokenFunction(List<ScriptToken> header, List<ScriptToken> parameterBlock, List<ScriptToken> body) {}

}
