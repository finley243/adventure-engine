package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.expression.*;
import com.github.finley243.adventureengine.script.*;
import com.github.finley243.adventureengine.stat.StatHolderReference;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptParser {

    private enum ScriptTokenType {
        END_LINE, STRING, FLOAT, INTEGER, NAME, ASSIGNMENT, COMMA, DOT, PLUS, MINUS, DIVIDE, MULTIPLY, MODULO, POWER, PARENTHESIS_OPEN, PARENTHESIS_CLOSE, BRACKET_OPEN, BRACKET_CLOSE, BRACKET_SQUARE_OPEN, BRACKET_SQUARE_CLOSE, BOOLEAN_TRUE, BOOLEAN_FALSE, NULL, COLON, NOT, AND, OR, EQUAL, NOT_EQUAL, GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, TERNARY_IF, RETURN, BREAK, CONTINUE, MODIFIER_PLUS, MODIFIER_MINUS, MODIFIER_MULTIPLY, MODIFIER_DIVIDE, MODIFIER_MODULO, ERROR, LOG
    }

    private static final Set<String> RESERVED_KEYWORDS = Sets.newHashSet("var", "func", "true", "false", "for", "if", "else", "stat", "statHolder", "return", "break", "continue", "game", "global", "null", "set", "list", "error", "log");
    private static final String REGEX_PATTERN = "/\\*[.*]+\\*/|//.*[\n\r]|\"(\\\\\"|[^\"])*\"|'(\\\\'|[^'])*'|_?[a-zA-Z][a-zA-Z0-9_]*|([0-9]*\\.[0-9]+|[0-9]+\\.?[0-9]*)f|[0-9]+|\\+=|-=|\\*=|/=|%=|==|!=|<=|>=|<|>|;|=|\\?|,|\\.|\\+|-|/|\\*|%|\\^|:|!|&&|\\|\\||\\(|\\)|\\{|\\}|\\[|\\]|\\S+";
    private static final Set<ScriptTokenType> ASSIGNMENT_OPERATORS = Set.of(ScriptTokenType.ASSIGNMENT, ScriptTokenType.MODIFIER_PLUS, ScriptTokenType.MODIFIER_MINUS, ScriptTokenType.MODIFIER_MULTIPLY, ScriptTokenType.MODIFIER_DIVIDE, ScriptTokenType.MODIFIER_MODULO);
    private static final Map<String, ScriptTokenType> SIMPLE_TOKENS_MAP = new HashMap<>() {
        {
            put(";", ScriptTokenType.END_LINE);
            put("=", ScriptTokenType.ASSIGNMENT);
            put("+=", ScriptTokenType.MODIFIER_PLUS);
            put("-=", ScriptTokenType.MODIFIER_MINUS);
            put("*=", ScriptTokenType.MODIFIER_MULTIPLY);
            put("/=", ScriptTokenType.MODIFIER_DIVIDE);
            put("%=", ScriptTokenType.MODIFIER_MODULO);
            put(",", ScriptTokenType.COMMA);
            put(".", ScriptTokenType.DOT);
            put("+", ScriptTokenType.PLUS);
            put("-", ScriptTokenType.MINUS);
            put("/", ScriptTokenType.DIVIDE);
            put("*", ScriptTokenType.MULTIPLY);
            put("%", ScriptTokenType.MODULO);
            put("^", ScriptTokenType.POWER);
            put(":", ScriptTokenType.COLON);
            put("?", ScriptTokenType.TERNARY_IF);
            put("!", ScriptTokenType.NOT);
            put("&&", ScriptTokenType.AND);
            put("||", ScriptTokenType.OR);
            put("==", ScriptTokenType.EQUAL);
            put("!=", ScriptTokenType.NOT_EQUAL);
            put("<=", ScriptTokenType.LESS_EQUAL);
            put(">=", ScriptTokenType.GREATER_EQUAL);
            put("<", ScriptTokenType.LESS);
            put(">", ScriptTokenType.GREATER);
            put("(", ScriptTokenType.PARENTHESIS_OPEN);
            put(")", ScriptTokenType.PARENTHESIS_CLOSE);
            put("{", ScriptTokenType.BRACKET_OPEN);
            put("}", ScriptTokenType.BRACKET_CLOSE);
            put("[", ScriptTokenType.BRACKET_SQUARE_OPEN);
            put("]", ScriptTokenType.BRACKET_SQUARE_CLOSE);
            put("true", ScriptTokenType.BOOLEAN_TRUE);
            put("false", ScriptTokenType.BOOLEAN_FALSE);
            put("null", ScriptTokenType.NULL);
            put("return", ScriptTokenType.RETURN);
            put("break", ScriptTokenType.BREAK);
            put("continue", ScriptTokenType.CONTINUE);
            put("error", ScriptTokenType.ERROR);
            put("log", ScriptTokenType.LOG);
        }
    };

    public static List<ScriptData> parseFunctions(String scriptText, String fileName) {
        List<ScriptData> scripts = new ArrayList<>();
        List<ScriptToken> tokens = parseToTokens(scriptText, fileName);
        List<ScriptTokenFunction> functions = groupTokensToFunctions(tokens);
        for (ScriptTokenFunction function : functions) {
            ScriptData script = parseFunction(function);
            scripts.add(script);
        }
        return scripts;
    }

    public static Script parseExpression(String scriptText, String fileName) {
        List<ScriptToken> tokens = parseToTokens(scriptText, fileName);
        return parseExpression(tokens);
    }

    public static Script parseScript(String scriptText, String fileName) {
        List<ScriptToken> tokens = parseToTokens(scriptText, fileName);
        return parseScript(tokens);
    }

    public static Expression parseLiteral(String scriptText, String fileName) {
        List<ScriptToken> tokens = parseToTokens(scriptText, fileName);
        return parseLiteral(tokens);
    }

    private static List<ScriptToken> parseToTokens(String scriptText, String fileName) {
        List<ScriptToken> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile(REGEX_PATTERN).matcher(scriptText);
        int lastEnd = 0;
        int lastLine = 0;
        while (matcher.find()) {
            String currentToken = matcher.group();
            int tokenLine = lastLine + scriptText.substring(lastEnd, matcher.start()).split("\n", -1).length;
            if (currentToken.startsWith("\"") && currentToken.endsWith("\"")) {
                String value = stringLiteralToValue(currentToken);
                tokens.add(new ScriptToken(ScriptTokenType.STRING, value, tokenLine, fileName));
            } else if (currentToken.startsWith("'") && currentToken.endsWith("'")) {
                String value = stringLiteralToValue(currentToken);
                tokens.add(new ScriptToken(ScriptTokenType.STRING, value, tokenLine, fileName));
            } else if (currentToken.matches("([0-9]*\\.[0-9]+|[0-9]+\\.?[0-9]*)f")) {
                String value = currentToken.substring(0, currentToken.length() - 1);
                tokens.add(new ScriptToken(ScriptTokenType.FLOAT, value, tokenLine, fileName));
            } else if (currentToken.matches("[0-9]+")) {
                tokens.add(new ScriptToken(ScriptTokenType.INTEGER, currentToken, tokenLine, fileName));
            } else if (SIMPLE_TOKENS_MAP.containsKey(currentToken)) {
                tokens.add(new ScriptToken(SIMPLE_TOKENS_MAP.get(currentToken), tokenLine, fileName));
            } else if (currentToken.matches("_?[a-zA-Z][a-zA-Z0-9_]*")) {
                tokens.add(new ScriptToken(ScriptTokenType.NAME, currentToken, tokenLine, fileName));
            } else if (currentToken.matches("\\S+")) {
                throw new ScriptCompileException("Invalid token found", fileName, tokenLine);
            }
        }
        return tokens;
    }

    private static String stringLiteralToValue(String token) {
        return token.substring(1, token.length() - 1).replaceAll("\\\\(.)", "$1");
    }

    private static List<ScriptTokenFunction> groupTokensToFunctions(List<ScriptToken> tokens) {
        List<ScriptTokenFunction> tokenFunctions = new ArrayList<>();
        int index = 0;
        while (index < tokens.size()) {
            int parameterStartIndex = findFirstTokenIndex(tokens, ScriptTokenType.PARENTHESIS_OPEN, index);
            if (parameterStartIndex == -1) throw new IllegalArgumentException("Function is missing parameter block");
            int parameterEndIndex = findPairedClosingBracket(tokens, parameterStartIndex);
            if (parameterEndIndex == -1) throw new IllegalArgumentException("Function parameter block is not properly closed");
            int bodyStartIndex = parameterEndIndex + 1;
            if (tokens.get(bodyStartIndex).type != ScriptTokenType.BRACKET_OPEN) throw new IllegalArgumentException("Function is missing body");
            int bodyEndIndex = findPairedClosingBracket(tokens, bodyStartIndex);
            if (bodyEndIndex == -1) throw new IllegalArgumentException("Function body is not properly closed");
            List<ScriptToken> header = tokens.subList(index, parameterStartIndex);
            List<ScriptToken> parameters = tokens.subList(parameterStartIndex + 1, parameterEndIndex);
            List<ScriptToken> body = tokens.subList(bodyStartIndex + 1, bodyEndIndex);
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
        if (RESERVED_KEYWORDS.contains(functionName)) throw new IllegalArgumentException("Function name is reserved");
        boolean functionHasReturn = functionHasReturn(functionTokens.header());
        Expression.DataType functionReturnType = parseFunctionReturnType(functionTokens.header());
        List<ScriptParameter> functionParameters = parseFunctionParameters(functionTokens.parameters());
        Script functionScript = parseScript(functionTokens.body());
        return new ScriptData(functionName, functionHasReturn, functionReturnType, functionParameters, false, functionScript);
    }

    private static boolean functionHasReturn(List<ScriptToken> headerTokens) {
        return headerTokens.size() == 3;
    }

    private static Expression.DataType parseFunctionReturnType(List<ScriptToken> headerTokens) {
        if (headerTokens.size() == 3) {
            if (headerTokens.get(1).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function header has invalid return type");
            return stringToDataType(headerTokens.get(1).value, headerTokens.getFirst().fileName, headerTokens.getFirst().line);
        }
        if (headerTokens.size() == 2) {
            return null;
        }
        throw new IllegalArgumentException("Function header contains unexpected tokens");
    }

    private static List<ScriptParameter> parseFunctionParameters(List<ScriptToken> parameterTokens) {
        List<ScriptParameter> functionParameters = new ArrayList<>();
        boolean hasParsedNamedParameter = false;
        int index = 0;
        while (index < parameterTokens.size()) {
            int nextCommaIndex = findFirstTokenIndex(parameterTokens, ScriptTokenType.COMMA, index);
            List<ScriptToken> currentGroup = parameterTokens.subList(index, nextCommaIndex == -1 ? parameterTokens.size() : nextCommaIndex);
            index = nextCommaIndex == -1 ? parameterTokens.size() : nextCommaIndex;
            if (currentGroup.isEmpty() || currentGroup.size() == 2) throw new IllegalArgumentException("Function contains invalid parameter definition");
            if (currentGroup.getFirst().type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function contains invalid parameter name");
            if (currentGroup.size() >= 3 && currentGroup.get(1).type != ScriptTokenType.ASSIGNMENT) throw new IllegalArgumentException("Function contains invalid parameter definition");
            String parameterName = currentGroup.getFirst().value;
            boolean parameterIsRequired = currentGroup.size() < 3;
            if (parameterIsRequired) {
                hasParsedNamedParameter = true;
            } else if (hasParsedNamedParameter) {
                throw new IllegalArgumentException("Function contains unnamed parameter after named parameter");
            }
            Expression parameterDefaultValue = parameterIsRequired ? null : parseLiteral(currentGroup.subList(2, index));
            functionParameters.add(new ScriptParameter(parameterName, parameterIsRequired, parameterDefaultValue));
            index += 1;
        }
        return functionParameters;
    }

    // Provided token list should NOT be enclosed in brackets
    private static Script parseScript(List<ScriptToken> tokens) {
        List<Script> scripts = new ArrayList<>();
        int index = 0;
        while (index < tokens.size()) {
            if (tokens.get(index).type == ScriptTokenType.NAME && tokens.get(index).value.equals("if")) {
                int lineStart = tokens.get(index).line;
                List<ScriptIfTokens> branches = new ArrayList<>();
                List<ScriptToken> bodyElse = null;
                if (tokens.get(index + 1).type != ScriptTokenType.PARENTHESIS_OPEN) throw new IllegalArgumentException("If statement is missing condition");
                int conditionEndIndex = findPairedClosingBracket(tokens, index + 1);
                if (conditionEndIndex == -1) throw new IllegalArgumentException("If statement condition is not closed");
                if (tokens.get(conditionEndIndex + 1).type != ScriptTokenType.BRACKET_OPEN) throw new IllegalArgumentException("If statement is missing body");
                int bodyEndIndex = findPairedClosingBracket(tokens, conditionEndIndex + 1);
                if (bodyEndIndex == -1) throw new IllegalArgumentException("If statement body is not closed");
                branches.add(new ScriptIfTokens(tokens.subList(index + 2, conditionEndIndex), tokens.subList(conditionEndIndex + 2, bodyEndIndex)));
                index = bodyEndIndex + 1;
                while (bodyElse == null && index < tokens.size() && tokens.get(index).type == ScriptTokenType.NAME && tokens.get(index).value.equals("else")) {
                    if (tokens.get(index + 1).type == ScriptTokenType.NAME && tokens.get(index + 1).value.equals("if")) {
                        if (tokens.get(index + 2).type != ScriptTokenType.PARENTHESIS_OPEN) throw new IllegalArgumentException("Else if statement is missing condition");
                        int branchConditionEndIndex = findPairedClosingBracket(tokens, index + 2);
                        if (branchConditionEndIndex == -1) throw new IllegalArgumentException("Else if statement condition is not closed");
                        if (tokens.get(branchConditionEndIndex + 1).type != ScriptTokenType.BRACKET_OPEN) throw new IllegalArgumentException("Else if statement is missing body");
                        int branchBodyEndIndex = findPairedClosingBracket(tokens, branchConditionEndIndex + 1);
                        if (branchBodyEndIndex == -1) throw new IllegalArgumentException("Else if statement body is not closed");
                        branches.add(new ScriptIfTokens(tokens.subList(index + 2, branchConditionEndIndex), tokens.subList(branchConditionEndIndex + 2, branchBodyEndIndex)));
                        index = branchBodyEndIndex + 1;
                    } else {
                        if (tokens.get(index + 1).type != ScriptTokenType.PARENTHESIS_OPEN) throw new IllegalArgumentException("Else statement is missing body");
                        int branchBodyEndIndex = findPairedClosingBracket(tokens, index + 1);
                        if (branchBodyEndIndex == -1) throw new IllegalArgumentException("Else statement body is not closed");
                        bodyElse = tokens.subList(index + 2, branchBodyEndIndex);
                        index = branchBodyEndIndex + 1;
                    }
                }
                Script script = parseIf(branches, bodyElse, lineStart);
                scripts.add(script);
            } else if (tokens.get(index).type == ScriptTokenType.NAME && tokens.get(index).value.equals("for")) {
                int lineStart = tokens.get(index).line;
                if (tokens.get(index + 1).type != ScriptTokenType.PARENTHESIS_OPEN) throw new IllegalArgumentException("For loop is missing iterator");
                int iteratorEndIndex = findPairedClosingBracket(tokens, index + 1);
                if (iteratorEndIndex == -1) throw new IllegalArgumentException("For loop iterator is not closed");
                if (tokens.get(iteratorEndIndex + 1).type != ScriptTokenType.BRACKET_OPEN) throw new IllegalArgumentException("For loop is missing body");
                int bodyEndIndex = findPairedClosingBracket(tokens, iteratorEndIndex + 1);
                if (bodyEndIndex == -1) throw new IllegalArgumentException("For loop body is not closed");
                Script script = parseFor(tokens.subList(index + 1, iteratorEndIndex), tokens.subList(iteratorEndIndex + 2, bodyEndIndex), lineStart);
                scripts.add(script);
                index = bodyEndIndex + 1;
            } else {
                int endIndex = findFirstTokenIndex(tokens, ScriptTokenType.END_LINE, index);
                if (endIndex == -1) throw new IllegalArgumentException("Function is missing a line end token");
                Script script = parseSingleInstruction(tokens.subList(index, endIndex));
                scripts.add(script);
                index = endIndex + 1;
            }
        }
        int lineStart = tokens.isEmpty() ? 0 : tokens.getFirst().line;
        String fileName = tokens.isEmpty() ? null : tokens.getFirst().fileName;
        return new ScriptCompound(new Script.ScriptTraceData(lineStart, fileName), scripts);
    }

    private static Script parseIf(List<ScriptIfTokens> branches, List<ScriptToken> bodyElse, int lineStart) {
        List<ScriptConditional.ConditionalScriptPair> conditionalScriptPairs = new ArrayList<>();
        for (ScriptIfTokens branch : branches) {
            Script conditionExpression = parseExpression(branch.condition());
            Script scriptBranch = parseScript(branch.body());
            conditionalScriptPairs.add(new ScriptConditional.ConditionalScriptPair(conditionExpression, scriptBranch));
        }
        Script scriptElse = bodyElse == null ? null : parseScript(bodyElse);
        return new ScriptConditional(new Script.ScriptTraceData(lineStart, branches.getFirst().condition().getFirst().fileName), conditionalScriptPairs, scriptElse);
    }

    private static Script parseFor(List<ScriptToken> iterator, List<ScriptToken> body, int lineStart) {
        if (iterator.getFirst().type != ScriptTokenType.NAME || iterator.get(1).type != ScriptTokenType.COLON) throw new IllegalArgumentException("For loop has invalid iterator format");
        String iteratorVariableName = iterator.getFirst().value;
        // TODO - Check for invalid variable name
        Script iteratedValuesExpression = parseExpression(iterator.subList(2, iterator.size()));
        Script iteratedScript = parseScript(body);
        return new ScriptIterator(new Script.ScriptTraceData(lineStart, iterator.getFirst().fileName), iteratedValuesExpression, iteratorVariableName, iteratedScript);
    }

    private static Script parseSingleInstruction(List<ScriptToken> tokens) {
        if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("var")) {
            // Variable declaration
            return parseVariableDeclaration(tokens);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.get(1).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            // Function call
            return parseFunctionCall(tokens);
        } else if (findFirstTokenIndexFromSet(tokens, ASSIGNMENT_OPERATORS, 0) != -1) {
            // Assignment
            return parseAssignment(tokens);
        } else if (tokens.getFirst().type == ScriptTokenType.RETURN) {
            if (tokens.size() == 1) {
                return new ScriptReturn(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), null);
            }
            Script returnValue = parseExpression(tokens.subList(1, tokens.size()));
            return new ScriptReturn(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), returnValue);
        } else if (tokens.getFirst().type == ScriptTokenType.ERROR) {
            if (tokens.size() < 2 || tokens.get(1).type != ScriptTokenType.PARENTHESIS_OPEN) {
                throw new IllegalArgumentException("Error statement is missing opening parenthesis");
            }
            int closingParenthesisIndex = findPairedClosingBracket(tokens, 1);
            if (closingParenthesisIndex == -1) {
                throw new IllegalArgumentException("Error statement is missing closing parenthesis");
            } else if (closingParenthesisIndex != tokens.size() - 1) {
                throw new IllegalArgumentException("Error statement is improperly terminated");
            }
            Script errorMessage = parseExpression(tokens.subList(2, tokens.size() - 1));
            return new ScriptError(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), errorMessage);
        } else if (tokens.getFirst().type == ScriptTokenType.LOG) {
            if (tokens.size() < 2 || tokens.get(1).type != ScriptTokenType.PARENTHESIS_OPEN) {
                throw new IllegalArgumentException("Log statement is missing opening parenthesis");
            }
            int closingParenthesisIndex = findPairedClosingBracket(tokens, 1);
            if (closingParenthesisIndex == -1) {
                throw new IllegalArgumentException("Log statement is missing closing parenthesis");
            } else if (closingParenthesisIndex != tokens.size() - 1) {
                throw new IllegalArgumentException("Log statement is improperly terminated");
            }
            Script logMessage = parseExpression(tokens.subList(2, tokens.size() - 1));
            return new ScriptPrintLog(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), logMessage);
        } else if (tokens.getFirst().type == ScriptTokenType.BREAK) {
            if (tokens.size() != 1) throw new IllegalArgumentException("Break statement must be called on its own");
            return new ScriptFlowStatement(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), Script.FlowStatementType.BREAK);
        } else if (tokens.getFirst().type == ScriptTokenType.CONTINUE) {
            if (tokens.size() != 1) throw new IllegalArgumentException("Continue statement must be called on its own");
            return new ScriptFlowStatement(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), Script.FlowStatementType.CONTINUE);
        } else {
            throw new IllegalArgumentException("Script contains invalid instruction");
        }
    }

    private static Script parseAssignment(List<ScriptToken> tokens) {
        int assignmentOperatorIndex = findFirstTokenIndexFromSet(tokens, ASSIGNMENT_OPERATORS, 0);
        int firstSquareBracketIndex = findFirstTokenIndex(tokens, ScriptTokenType.BRACKET_SQUARE_OPEN, 0);
        if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("stat")) {
            // Stat assignment
            return parseStatAssignment(tokens);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("global")) {
            // Global assignment
            return parseGlobalAssignment(tokens);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && assignmentOperatorIndex == 1) {
            // Variable assignment
            return parseVariableAssignment(tokens);
        } else if (firstSquareBracketIndex != -1 && firstSquareBracketIndex < assignmentOperatorIndex) {
            // List element assignment
            return parseListElementAssignment(tokens);
        } else {
            throw new ScriptCompileException("Assignment statement is invalid", tokens.getFirst().fileName, tokens.getFirst().line);
        }
    }

    private static Script parseListElementAssignment(List<ScriptToken> tokens) {
        int listIndexOpen = findFirstTokenIndex(tokens, ScriptTokenType.BRACKET_SQUARE_OPEN, 0);
        int listIndexClose = findPairedClosingBracket(tokens, listIndexOpen);
        if (listIndexClose == -1) {
            throw new ScriptCompileException("List element assignment is missing closing bracket on index block", tokens.getFirst().fileName, tokens.getFirst().line);
        }
        int assignmentOperatorIndex = findFirstTokenIndexFromSet(tokens, ASSIGNMENT_OPERATORS, listIndexClose);
        if (assignmentOperatorIndex == -1) {
            throw new ScriptCompileException("List element assignment has no assignment operator", tokens.getFirst().fileName, tokens.getFirst().line);
        } else if (assignmentOperatorIndex != listIndexClose + 1) {
            throw new ScriptCompileException("List element assignment has unexpected tokens before assignment operator", tokens.getFirst().fileName, tokens.getFirst().line);
        }
        Script listScript = parseValue(tokens.subList(0, listIndexOpen));
        Script indexScript = parseExpression(tokens.subList(listIndexOpen + 1, listIndexClose));
        Script valueScript = parseExpression(tokens.subList(assignmentOperatorIndex + 1, tokens.size()));
        int lineStart = tokens.getFirst().line;
        String fileName = tokens.getFirst().fileName;
        Script valueScriptWithOperators = applyAssignmentOperator(tokens.get(assignmentOperatorIndex).type, new ScriptListIndexGetInternal(new Script.ScriptTraceData(lineStart, fileName), listScript, indexScript), valueScript, new Script.ScriptTraceData(lineStart, fileName));
        return new ScriptListIndexSetInternal(new Script.ScriptTraceData(lineStart, fileName), listScript, indexScript, valueScriptWithOperators);
    }

    private static Script parseVariableDeclaration(List<ScriptToken> tokens) {
        if (tokens.get(1).type != ScriptTokenType.NAME) throw new ScriptCompileException("Variable definition is missing a name", tokens.getFirst().fileName, tokens.getFirst().line);
        String variableName = tokens.get(1).value;
        if (RESERVED_KEYWORDS.contains(variableName)) throw new ScriptCompileException("Variable name is reserved", tokens.getFirst().fileName, tokens.getFirst().line);
        if (tokens.size() == 2) {
            return new ScriptSetVariable(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), variableName, null, true);
        }
        if (tokens.get(2).type != ScriptTokenType.ASSIGNMENT) throw new ScriptCompileException("Variable definition is missing assignment operator", tokens.getFirst().fileName, tokens.getFirst().line);
        Script variableValue = parseExpression(tokens.subList(3, tokens.size()));
        return new ScriptSetVariable(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), variableName, variableValue, true);
    }

    private static Script parseStatAssignment(List<ScriptToken> tokens) {
        int assignmentOperatorIndex = findFirstTokenIndexFromSet(tokens, ASSIGNMENT_OPERATORS, 0);
        if (assignmentOperatorIndex == -1) throw new ScriptCompileException("Stat assignment is missing assignment operator", tokens.getFirst().fileName, tokens.getFirst().line);
        ScriptStatReference statReference = parseStatReference(tokens.subList(0, assignmentOperatorIndex));
        Script valueScript = parseExpression(tokens.subList(assignmentOperatorIndex + 1, tokens.size()));
        int lineStart = tokens.getFirst().line;
        String fileName = tokens.getFirst().fileName;
        Script valueScriptWithOperators = applyAssignmentOperator(tokens.get(assignmentOperatorIndex).type, new ScriptGetStat(new Script.ScriptTraceData(lineStart, fileName), statReference.statHolder(), statReference.name()), valueScript, new Script.ScriptTraceData(lineStart, fileName));
        return new ScriptSetStat(new Script.ScriptTraceData(lineStart, fileName), statReference.statHolder(), statReference.name(), valueScriptWithOperators);
    }

    private static Script parseGlobalAssignment(List<ScriptToken> tokens) {
        if (tokens.get(1).type != ScriptTokenType.DOT) throw new ScriptCompileException("Global assignment is missing dot after global keyword", tokens.getFirst().fileName, tokens.getFirst().line);
        if (tokens.get(2).type != ScriptTokenType.NAME) throw new ScriptCompileException("Global assignment is missing global name", tokens.getFirst().fileName, tokens.getFirst().line);
        String globalName = tokens.get(2).value;
        Script valueScript = parseExpression(tokens.subList(4, tokens.size()));
        int lineStart = tokens.getFirst().line;
        String fileName = tokens.getFirst().fileName;
        Script valueScriptWithOperators = applyAssignmentOperator(tokens.get(3).type, new ScriptGetGlobal(new Script.ScriptTraceData(lineStart, fileName), globalName), valueScript, new Script.ScriptTraceData(lineStart, fileName));
        return new ScriptSetGlobal(new Script.ScriptTraceData(lineStart, fileName), globalName, valueScriptWithOperators);
    }

    private static Script parseVariableAssignment(List<ScriptToken> tokens) {
        String variableName = tokens.getFirst().value;
        Script valueScript = parseExpression(tokens.subList(2, tokens.size()));
        int lineStart = tokens.getFirst().line;
        String fileName = tokens.getFirst().fileName;
        Script valueScriptWithOperators = applyAssignmentOperator(tokens.get(1).type, new ScriptGetVariable(new Script.ScriptTraceData(lineStart, fileName), variableName), valueScript, new Script.ScriptTraceData(lineStart, fileName));
        return new ScriptSetVariable(new Script.ScriptTraceData(lineStart, fileName), variableName, valueScriptWithOperators, false);
    }

    private static Script parseFunctionCall(List<ScriptToken> tokens) {
        String functionName = tokens.getFirst().value;
        List<ScriptExternal.ParameterContainer> parameters = parseFunctionCallParameters(tokens.subList(2, tokens.size() - 1));
        return new ScriptExternal(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), functionName, parameters);
    }

    private static List<ScriptExternal.ParameterContainer> parseFunctionCallParameters(List<ScriptToken> tokens) {
        List<ScriptExternal.ParameterContainer> parameters = new ArrayList<>();
        boolean hasParsedNamedParameter = false;
        int index = 0;
        while (index < tokens.size()) {
            int nextCommaIndex = findFirstTokenIndex(tokens, ScriptTokenType.COMMA, index);
            List<ScriptToken> currentGroup;
            if (nextCommaIndex == -1) {
                currentGroup = tokens.subList(index, tokens.size());
                index = tokens.size() + 1;
            } else {
                currentGroup = tokens.subList(index, nextCommaIndex);
                index = nextCommaIndex + 1;
            }
            if (currentGroup.getFirst().type == ScriptTokenType.NAME && currentGroup.size() >= 2 && currentGroup.get(1).type == ScriptTokenType.ASSIGNMENT) {
                // Named parameter
                if (currentGroup.size() < 3) throw new ScriptCompileException("Function call contains invalid parameter", tokens.getFirst().fileName, tokens.getFirst().line);
                String parameterName = currentGroup.getFirst().value;
                Script parameterValue = parseExpression(currentGroup.subList(2, currentGroup.size()));
                parameters.add(new ScriptExternal.ParameterContainer(parameterName, parameterValue));
                hasParsedNamedParameter = true;
            } else {
                // Unnamed parameter
                if (hasParsedNamedParameter) throw new ScriptCompileException("Function call contains unnamed parameter after named parameter", tokens.getFirst().fileName, tokens.getFirst().line);
                Script parameterValue = parseExpression(currentGroup);
                parameters.add(new ScriptExternal.ParameterContainer(null, parameterValue));
            }
        }
        return parameters;
    }

    private static Script parseExpression(List<ScriptToken> tokens) {
        return parseTernary(tokens);
    }

    private static Script parseTernary(List<ScriptToken> tokens) {
        int firstTernaryOperator = findFirstTokenIndex(tokens, ScriptTokenType.TERNARY_IF, 0);
        if (firstTernaryOperator != -1) {
            int firstTernaryElse = findFirstTokenIndex(tokens, ScriptTokenType.COLON, firstTernaryOperator);
            if (firstTernaryElse == -1) throw new ScriptCompileException("Ternary expression is missing an else expression", tokens.getFirst().fileName, tokens.getFirst().line);
            Script scriptCondition = parseOr(tokens.subList(0, firstTernaryOperator));
            Script scriptTrue = parseOr(tokens.subList(firstTernaryOperator + 1, firstTernaryElse));
            Script scriptFalse = parseTernary(tokens.subList(firstTernaryElse + 1, tokens.size()));
            return new ScriptTernary(new Script.ScriptTraceData(tokens.get(firstTernaryOperator).line, tokens.get(firstTernaryOperator).fileName), scriptCondition, scriptTrue, scriptFalse);
        } else {
            return parseOr(tokens);
        }
    }

    private static Script parseOr(List<ScriptToken> tokens) {
        int lastOrOperator = findLastTokenIndex(tokens, ScriptTokenType.OR, tokens.size() - 1);
        if (lastOrOperator != -1) {
            Script firstExpression = parseOr(tokens.subList(0, lastOrOperator));
            Script secondExpression = parseAnd(tokens.subList(lastOrOperator + 1, tokens.size()));
            return new ScriptOr(new Script.ScriptTraceData(tokens.get(lastOrOperator).line, tokens.get(lastOrOperator).fileName), List.of(firstExpression, secondExpression));
        } else {
            return parseAnd(tokens);
        }
    }

    private static Script parseAnd(List<ScriptToken> tokens) {
        int lastAndOperator = findLastTokenIndex(tokens, ScriptTokenType.AND, tokens.size() - 1);
        if (lastAndOperator != -1) {
            Script firstExpression = parseAnd(tokens.subList(0, lastAndOperator));
            Script secondExpression = parseComparator(tokens.subList(lastAndOperator + 1, tokens.size()));
            return new ScriptAnd(new Script.ScriptTraceData(tokens.get(lastAndOperator).line, tokens.get(lastAndOperator).fileName), List.of(firstExpression, secondExpression));
        } else {
            return parseComparator(tokens);
        }
    }

    private static Script parseComparator(List<ScriptToken> tokens) {
        int firstComparatorOperator = findFirstTokenIndexFromSet(tokens, Set.of(ScriptTokenType.EQUAL, ScriptTokenType.NOT_EQUAL, ScriptTokenType.GREATER, ScriptTokenType.LESS, ScriptTokenType.GREATER_EQUAL, ScriptTokenType.LESS_EQUAL), 0);
        if (firstComparatorOperator != -1) {
            ScriptComparator.Comparator comparator = switch (tokens.get(firstComparatorOperator).type) {
                case EQUAL -> ScriptComparator.Comparator.EQUAL;
                case NOT_EQUAL -> ScriptComparator.Comparator.NOT_EQUAL;
                case GREATER -> ScriptComparator.Comparator.GREATER;
                case LESS -> ScriptComparator.Comparator.LESS;
                case GREATER_EQUAL -> ScriptComparator.Comparator.GREATER_EQUAL;
                case LESS_EQUAL -> ScriptComparator.Comparator.LESS_EQUAL;
                default -> throw new ScriptCompileException("Expression contains an invalid comparator statement", tokens.getFirst().fileName, tokens.getFirst().line);
            };
            Script firstExpression = parseSum(tokens.subList(0, firstComparatorOperator));
            Script secondExpression = parseSum(tokens.subList(firstComparatorOperator + 1, tokens.size()));
            return new ScriptComparator(new Script.ScriptTraceData(tokens.get(firstComparatorOperator).line, tokens.get(firstComparatorOperator).fileName), firstExpression, secondExpression, comparator);
        } else {
            return parseSum(tokens);
        }
    }

    private static Script parseSum(List<ScriptToken> tokens) {
        int lastSumOperator = findLastTokenIndexFromSet(tokens, Set.of(ScriptTokenType.PLUS, ScriptTokenType.MINUS), tokens.size() - 1);
        if (lastSumOperator == -1) {
            return parseProduct(tokens);
        }
        Script firstExpression = parseSum(tokens.subList(0, lastSumOperator));
        Script secondExpression = parseProduct(tokens.subList(lastSumOperator + 1, tokens.size()));
        if (tokens.get(lastSumOperator).type == ScriptTokenType.PLUS) {
            return new ScriptAdd(new Script.ScriptTraceData(tokens.get(lastSumOperator).line, tokens.get(lastSumOperator).fileName), firstExpression, secondExpression);
        } else {
            return new ScriptSubtract(new Script.ScriptTraceData(tokens.get(lastSumOperator).line, tokens.get(lastSumOperator).fileName), firstExpression, secondExpression);
        }
    }

    private static Script parseProduct(List<ScriptToken> tokens) {
        int lastProductOperator = findLastTokenIndexFromSet(tokens, Set.of(ScriptTokenType.MULTIPLY, ScriptTokenType.DIVIDE, ScriptTokenType.MODULO), tokens.size() - 1);
        if (lastProductOperator == -1) {
            return parsePower(tokens);
        }
        Script firstExpression = parseProduct(tokens.subList(0, lastProductOperator));
        Script secondExpression = parsePower(tokens.subList(lastProductOperator + 1, tokens.size()));
        if (tokens.get(lastProductOperator).type == ScriptTokenType.MULTIPLY) {
            return new ScriptMultiply(new Script.ScriptTraceData(tokens.get(lastProductOperator).line, tokens.get(lastProductOperator).fileName), firstExpression, secondExpression);
        } else if (tokens.get(lastProductOperator).type == ScriptTokenType.DIVIDE) {
            return new ScriptDivide(new Script.ScriptTraceData(tokens.get(lastProductOperator).line, tokens.get(lastProductOperator).fileName), firstExpression, secondExpression);
        } else {
            return new ScriptModulo(new Script.ScriptTraceData(tokens.get(lastProductOperator).line, tokens.get(lastProductOperator).fileName), firstExpression, secondExpression);
        }
    }

    private static Script parsePower(List<ScriptToken> tokens) {
        int firstPowerSymbol = findFirstTokenIndex(tokens, ScriptTokenType.POWER, 0);
        if (firstPowerSymbol != -1) {
            Script powerBase = parseNot(tokens.subList(0, firstPowerSymbol));
            Script powerExponent = parsePower(tokens.subList(firstPowerSymbol + 1, tokens.size()));
            return new ScriptPower(new Script.ScriptTraceData(tokens.get(firstPowerSymbol).line, tokens.get(firstPowerSymbol).fileName), powerBase, powerExponent);
        } else {
            return parseNot(tokens);
        }
    }

    private static Script parseNot(List<ScriptToken> tokens) {
        if (tokens.getFirst().type == ScriptTokenType.NOT) {
            Script innerValue = parseValue(tokens.subList(1, tokens.size()));
            return new ScriptNot(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), innerValue);
        } else {
            return parseValue(tokens);
        }
    }

    private static Script parseValue(List<ScriptToken> tokens) {
        if (tokens.getFirst().type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            return parseExpression(tokens.subList(1, tokens.size() - 1));
        } else if (tokens.getLast().type == ScriptTokenType.BRACKET_SQUARE_CLOSE) {
            return parseListElementReference(tokens);
        } else if (tokens.size() == 1 && tokens.getFirst().type == ScriptTokenType.NAME) {
            return new ScriptGetVariable(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), tokens.getFirst().value);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("stat")) {
            ScriptStatReference statReference = parseStatReference(tokens);
            return new ScriptGetStat(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), statReference.statHolder(), statReference.name());
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("statHolder")) {
            StatHolderReference statHolderReference = parseStatHolderReference(tokens);
            return new ScriptStatHolder(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), statHolderReference);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("global")) {
            ScriptGlobalReference globalReference = parseGlobalReference(tokens);
            return new ScriptGetGlobal(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), globalReference.name());
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("game")) {
            return parseGameValue(tokens);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && (tokens.getFirst().value.equals("set") || tokens.getFirst().value.equals("list"))) {
            return parseCollection(tokens);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.get(1).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            return parseFunctionCall(tokens);
        } else {
            Expression literalExpression = parseLiteral(tokens);
            return new ScriptExpression(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), literalExpression);
        }
    }

    private static Script parseListElementReference(List<ScriptToken> tokens) {
        int indexBracketOpen = findFirstTokenIndex(tokens, ScriptTokenType.BRACKET_SQUARE_OPEN, 0);
        int indexBracketClose = findPairedClosingBracket(tokens, indexBracketOpen);
        Script listExpression = parseExpression(tokens.subList(0, indexBracketOpen));
        Script indexExpression = parseExpression(tokens.subList(indexBracketOpen + 1, indexBracketClose));
        return new ScriptListIndexGetInternal(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), listExpression, indexExpression);
    }

    private static Script parseCollection(List<ScriptToken> tokens) {
        if (tokens.getFirst().type != ScriptTokenType.NAME || (!tokens.getFirst().value.equals("set") && !tokens.getFirst().value.equals("list"))) throw new ScriptCompileException("Collection constructor is missing set or list keyword", tokens.getFirst().fileName, tokens.getFirst().line);
        if (tokens.get(1).type != ScriptTokenType.PARENTHESIS_OPEN) throw new ScriptCompileException("Collection constructor is missing value block", tokens.getFirst().fileName, tokens.getFirst().line);
        if (tokens.getLast().type != ScriptTokenType.PARENTHESIS_CLOSE) throw new ScriptCompileException("Collection constructor value block is not closed", tokens.getFirst().fileName, tokens.getFirst().line);
        if (tokens.size() == 3) {
            if (tokens.getFirst().value.equals("set")) {
                return new ScriptBuildSet(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), new ArrayList<>());
            } else {
                return new ScriptBuildList(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), new ArrayList<>());
            }
        }
        List<Script> collectionValues = new ArrayList<>();
        int index = 2;
        while (index < tokens.size()) {
            int nextCommaIndex = findFirstTokenIndex(tokens, ScriptTokenType.COMMA, index);
            List<ScriptToken> currentGroup;
            if (nextCommaIndex == -1) {
                currentGroup = tokens.subList(index, tokens.size() - 1);
                index = tokens.size();
            } else {
                currentGroup = tokens.subList(index, nextCommaIndex);
                index = nextCommaIndex + 1;
            }
            Script currentValueScript = parseExpression(currentGroup);
            collectionValues.add(currentValueScript);
        }
        if (tokens.getFirst().value.equals("set")) {
            return new ScriptBuildSet(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), collectionValues);
        } else {
            return new ScriptBuildList(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), collectionValues);
        }
    }

    private static Expression parseLiteral(List<ScriptToken> tokens) {
        if (tokens.size() != 1) throw new ScriptCompileException("Invalid literal expression", tokens.getFirst().fileName, tokens.getFirst().line);
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
        } else if (token.type == ScriptTokenType.NULL) {
            return null;
        }
        throw new ScriptCompileException("Invalid literal expression", tokens.getFirst().fileName, tokens.getFirst().line);
    }

    private static ScriptStatReference parseStatReference(List<ScriptToken> tokens) {
        if (tokens.getFirst().type != ScriptTokenType.NAME || !tokens.getFirst().value.equals("stat")) throw new ScriptCompileException("Stat reference is missing stat keyword", tokens.getFirst().fileName, tokens.getFirst().line);
        if (tokens.get(1).type != ScriptTokenType.DOT) throw new ScriptCompileException("Stat reference is missing period after stat keyword", tokens.getFirst().fileName, tokens.getFirst().line);
        int lastDotIndex = findLastTokenIndex(tokens, ScriptTokenType.DOT, tokens.size() - 1);
        if (lastDotIndex == -1) throw new ScriptCompileException("Stat reference has invalid name", tokens.getFirst().fileName, tokens.getFirst().line);
        Script statName;
        if (lastDotIndex + 2 == tokens.size()) {
            if (tokens.getLast().type != ScriptTokenType.NAME) throw new ScriptCompileException("Stat reference has invalid name", tokens.getFirst().fileName, tokens.getFirst().line);
            statName = Script.constant(tokens.getLast().value);
        } else if (tokens.size() > lastDotIndex + 1 && tokens.get(lastDotIndex + 1).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            statName = parseExpression(tokens.subList(lastDotIndex + 2, tokens.size() - 1));
        } else {
            throw new ScriptCompileException("Stat reference has invalid name", tokens.getFirst().fileName, tokens.getFirst().line);
        }
        StatHolderReference statHolder = parseStatHolder(tokens.subList(2, lastDotIndex));
        return new ScriptStatReference(statName, statHolder);
    }

    private static StatHolderReference parseStatHolderReference(List<ScriptToken> tokens) {
        if (tokens.getFirst().type != ScriptTokenType.NAME || !tokens.getFirst().value.equals("statHolder")) throw new ScriptCompileException("Stat holder reference is missing statHolder keyword", tokens.getFirst().fileName, tokens.getFirst().line);
        if (tokens.get(1).type != ScriptTokenType.DOT) throw new ScriptCompileException("Stat holder reference is missing period after statHolder keyword", tokens.getFirst().fileName, tokens.getFirst().line);
        return parseStatHolder(tokens.subList(2, tokens.size()));
    }

    private static StatHolderReference parseStatHolder(List<ScriptToken> tokens) {
        int lastHolderStartIndex = 0;
        StatHolderReference parentReference = null;
        int lastDotIndex = findLastTokenIndex(tokens, ScriptTokenType.DOT, tokens.size() - 1);
        if (lastDotIndex != -1) {
            lastHolderStartIndex = lastDotIndex + 1;
            parentReference = parseStatHolder(tokens.subList(0, lastDotIndex));
        }
        String holderType = null;
        Script holderID = null;
        Script holderExpression = null;
        if (lastDotIndex == -1 && tokens.get(lastHolderStartIndex).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            holderExpression = parseExpression(tokens.subList(lastHolderStartIndex + 1, tokens.size() - 1));
        } else if (tokens.get(lastHolderStartIndex).type == ScriptTokenType.NAME) {
            holderType = tokens.get(lastHolderStartIndex).value;
        } else {
            throw new ScriptCompileException("Stat holder reference contains invalid stat holder type", tokens.getFirst().fileName, tokens.getFirst().line);
        }
        if (holderExpression == null && tokens.size() > lastHolderStartIndex + 1 && tokens.get(lastHolderStartIndex + 1).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            holderID = parseExpression(tokens.subList(lastHolderStartIndex + 2, tokens.size() - 1));
        }
        return new StatHolderReference(holderType, holderID, parentReference, holderExpression);
    }

    private static ScriptGlobalReference parseGlobalReference(List<ScriptToken> tokens) {
        if (tokens.size() != 3) throw new ScriptCompileException("Global reference has invalid format", tokens.getFirst().fileName, tokens.getFirst().line);
        if (tokens.getFirst().type != ScriptTokenType.NAME || !tokens.getFirst().value.equals("global")) throw new ScriptCompileException("Global reference is missing global keyword", tokens.getFirst().fileName, tokens.getFirst().line);
        if (tokens.get(1).type != ScriptTokenType.DOT) throw new ScriptCompileException("Global reference is missing period after global keyword", tokens.getFirst().fileName, tokens.getFirst().line);
        if (tokens.get(2).type != ScriptTokenType.NAME) throw new ScriptCompileException("Global reference has invalid name", tokens.getFirst().fileName, tokens.getFirst().line);
        String globalName = tokens.get(2).value;
        return new ScriptGlobalReference(globalName);
    }

    private static Script parseGameValue(List<ScriptToken> tokens) {
        if (tokens.size() != 3) throw new ScriptCompileException("Game value reference has invalid format", tokens.getFirst().fileName, tokens.getFirst().line);
        if (tokens.getFirst().type != ScriptTokenType.NAME || !tokens.getFirst().value.equals("game")) throw new ScriptCompileException("Game value reference is missing game keyword", tokens.getFirst().fileName, tokens.getFirst().line);
        if (tokens.get(1).type != ScriptTokenType.DOT) throw new ScriptCompileException("Game value reference is missing period after game keyword", tokens.getFirst().fileName, tokens.getFirst().line);
        if (tokens.get(2).type != ScriptTokenType.NAME) throw new ScriptCompileException("Game value reference has invalid name", tokens.getFirst().fileName, tokens.getFirst().line);
        String gameValueName = tokens.get(2).value;
        return new ScriptGetGameValue(new Script.ScriptTraceData(tokens.getFirst().line, tokens.getFirst().fileName), gameValueName);
    }

    private static Script applyAssignmentOperator(ScriptTokenType operator, Script leftValue, Script rightValue, Script.ScriptTraceData trace) {
        return switch(operator) {
            case ASSIGNMENT -> rightValue;
            case MODIFIER_PLUS -> new ScriptAdd(trace, leftValue, rightValue);
            case MODIFIER_MINUS -> new ScriptSubtract(trace, leftValue, rightValue);
            case MODIFIER_MULTIPLY -> new ScriptMultiply(trace, leftValue, rightValue);
            case MODIFIER_DIVIDE -> new ScriptDivide(trace, leftValue, rightValue);
            case MODIFIER_MODULO -> new ScriptModulo(trace, leftValue, rightValue);
            default -> throw new ScriptCompileException("Not a valid assignment operator", trace.fileName(), trace.line());
        };
    }

    private static int findFirstTokenIndex(List<ScriptToken> tokens, ScriptTokenType type, int startIndex) {
        if (startIndex < 0 || startIndex >= tokens.size()) throw new ScriptCompileException("Start index is outside the valid range", tokens.getFirst().fileName, tokens.getFirst().line);
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
            } else if (token.type == ScriptTokenType.BRACKET_SQUARE_OPEN) {
                bracketStack.push(ScriptTokenType.BRACKET_SQUARE_OPEN);
            } else if (token.type == ScriptTokenType.BRACKET_SQUARE_CLOSE && bracketStack.peek() == ScriptTokenType.BRACKET_SQUARE_OPEN) {
                bracketStack.pop();
            }
        }
        return -1;
    }

    private static int findLastTokenIndex(List<ScriptToken> tokens, ScriptTokenType type, int startIndex) {
        if (startIndex < 0 || startIndex >= tokens.size()) throw new ScriptCompileException("Start index is outside the valid range", tokens.getFirst().fileName, tokens.getFirst().line);
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
            } else if (token.type == ScriptTokenType.BRACKET_SQUARE_CLOSE) {
                bracketStack.push(ScriptTokenType.BRACKET_SQUARE_CLOSE);
            } else if (token.type == ScriptTokenType.BRACKET_SQUARE_OPEN && bracketStack.peek() == ScriptTokenType.BRACKET_SQUARE_CLOSE) {
                bracketStack.pop();
            }
        }
        return -1;
    }

    private static int findFirstTokenIndexFromSet(List<ScriptToken> tokens, Set<ScriptTokenType> types, int startIndex) {
        if (startIndex < 0 || startIndex >= tokens.size()) throw new ScriptCompileException("Start index is outside the valid range", tokens.getFirst().fileName, tokens.getFirst().line);
        Deque<ScriptTokenType> bracketStack = new ArrayDeque<>();
        for (int i = startIndex; i < tokens.size(); i++) {
            ScriptToken token = tokens.get(i);
            if (bracketStack.isEmpty() && types.contains(token.type)) {
                return i;
            } else if (token.type == ScriptTokenType.BRACKET_OPEN) {
                bracketStack.push(ScriptTokenType.BRACKET_OPEN);
            } else if (token.type == ScriptTokenType.BRACKET_CLOSE && bracketStack.peek() == ScriptTokenType.BRACKET_OPEN) {
                bracketStack.pop();
            } else if (token.type == ScriptTokenType.PARENTHESIS_OPEN) {
                bracketStack.push(ScriptTokenType.PARENTHESIS_OPEN);
            } else if (token.type == ScriptTokenType.PARENTHESIS_CLOSE && bracketStack.peek() == ScriptTokenType.PARENTHESIS_OPEN) {
                bracketStack.pop();
            } else if (token.type == ScriptTokenType.BRACKET_SQUARE_OPEN) {
                bracketStack.push(ScriptTokenType.BRACKET_SQUARE_OPEN);
            } else if (token.type == ScriptTokenType.BRACKET_SQUARE_CLOSE && bracketStack.peek() == ScriptTokenType.BRACKET_SQUARE_OPEN) {
                bracketStack.pop();
            }
        }
        return -1;
    }

    private static int findLastTokenIndexFromSet(List<ScriptToken> tokens, Set<ScriptTokenType> types, int startIndex) {
        if (startIndex < 0 || startIndex >= tokens.size()) throw new ScriptCompileException("Start index is outside the valid range", tokens.getFirst().fileName, tokens.getFirst().line);
        Deque<ScriptTokenType> bracketStack = new ArrayDeque<>();
        for (int i = startIndex; i >= 0; i--) {
            ScriptToken token = tokens.get(i);
            if (bracketStack.isEmpty() && types.contains(token.type)) {
                return i;
            } else if (token.type == ScriptTokenType.BRACKET_CLOSE) {
                bracketStack.push(ScriptTokenType.BRACKET_CLOSE);
            } else if (token.type == ScriptTokenType.BRACKET_OPEN && bracketStack.peek() == ScriptTokenType.BRACKET_CLOSE) {
                bracketStack.pop();
            } else if (token.type == ScriptTokenType.PARENTHESIS_CLOSE) {
                bracketStack.push(ScriptTokenType.PARENTHESIS_CLOSE);
            } else if (token.type == ScriptTokenType.PARENTHESIS_OPEN && bracketStack.peek() == ScriptTokenType.PARENTHESIS_CLOSE) {
                bracketStack.pop();
            } else if (token.type == ScriptTokenType.BRACKET_SQUARE_CLOSE) {
                bracketStack.push(ScriptTokenType.BRACKET_SQUARE_CLOSE);
            } else if (token.type == ScriptTokenType.BRACKET_SQUARE_OPEN && bracketStack.peek() == ScriptTokenType.BRACKET_SQUARE_CLOSE) {
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
            case BRACKET_SQUARE_OPEN -> targetBracketType = ScriptTokenType.BRACKET_SQUARE_CLOSE;
            default -> throw new ScriptCompileException("Specified token is not a valid type of open bracket", tokens.getFirst().fileName, tokens.getFirst().line);
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
            } else if (token.type == ScriptTokenType.BRACKET_SQUARE_OPEN) {
                bracketStack.push(ScriptTokenType.BRACKET_SQUARE_OPEN);
            } else if (token.type == ScriptTokenType.BRACKET_SQUARE_CLOSE && bracketStack.peek() == ScriptTokenType.BRACKET_SQUARE_OPEN) {
                bracketStack.pop();
            }
        }
        return -1;
    }

    private static Expression.DataType stringToDataType(String name, String fileName, int line) {
        return switch (name) {
            case "boolean" -> Expression.DataType.BOOLEAN;
            case "int" -> Expression.DataType.INTEGER;
            case "float" -> Expression.DataType.FLOAT;
            case "string" -> Expression.DataType.STRING;
            case "set" -> Expression.DataType.SET;
            case "list" -> Expression.DataType.LIST;
            case "statHolder" -> Expression.DataType.STAT_HOLDER;
            case "inventory" -> Expression.DataType.INVENTORY;
            case "noun" -> Expression.DataType.NOUN;
            case "any" -> null;
            default -> throw new ScriptCompileException("Invalid data type name: " + name, fileName, line);
        };
    }

    public record ScriptData(String name, boolean hasReturn, Expression.DataType returnType, List<ScriptParameter> parameters, boolean allowExtraParameters, Script script) {}

    private static class ScriptToken {
        public final ScriptTokenType type;
        public final String value;
        public final int line;
        public final String fileName;

        public ScriptToken(ScriptTokenType type, int line, String fileName) {
            this.type = type;
            this.value = null;
            this.line = line;
            this.fileName = fileName;
        }

        public ScriptToken(ScriptTokenType type, String value, int line, String fileName) {
            this.type = type;
            this.value = value;
            this.line = line;
            this.fileName = fileName;
        }

        @Override
        public String toString() {
            return type.toString() + (value != null ? ":" + value : "");
        }
    }

    public record ScriptParameter(String name, boolean isRequired, Expression defaultValue) {}

    private record ScriptTokenFunction(List<ScriptToken> header, List<ScriptToken> parameters, List<ScriptToken> body) {}

    private record ScriptIfTokens(List<ScriptToken> condition, List<ScriptToken> body) {}

    private record ScriptStatReference(Script name, StatHolderReference statHolder) {}

    private record ScriptGlobalReference(String name) {}

}
