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
        END_LINE, STRING, FLOAT, INTEGER, NAME, ASSIGNMENT, COMMA, DOT, PLUS, MINUS, DIVIDE, MULTIPLY, MODULO, POWER, PARENTHESIS_OPEN, PARENTHESIS_CLOSE, BRACKET_OPEN, BRACKET_CLOSE, BOOLEAN_TRUE, BOOLEAN_FALSE, NULL, COLON, NOT, AND, OR, EQUAL, NOT_EQUAL, GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, TERNARY_IF, RETURN, BREAK, CONTINUE, MODIFIER_PLUS, MODIFIER_MINUS, MODIFIER_MULTIPLY, MODIFIER_DIVIDE, MODIFIER_MODULO, ERROR, LOG
    }

    private static final Set<String> RESERVED_KEYWORDS = Sets.newHashSet("var", "func", "true", "false", "for", "if", "else", "stat", "statHolder", "return", "break", "continue", "game", "global", "null", "set", "list", "error", "log");
    private static final String REGEX_PATTERN = "/\\*[.*]+\\*/|//.*[\n\r]|\"(\\\\\"|[^\"])*\"|'(\\\\'|[^'])*'|_?[a-zA-Z][a-zA-Z0-9_]*|([0-9]*\\.[0-9]+|[0-9]+\\.?[0-9]*)f|[0-9]+|\\+=|-=|\\*=|/=|%=|==|!=|<=|>=|<|>|;|=|\\?|,|\\.|\\+|-|/|\\*|%|\\^|:|!|&&|\\|\\||\\(|\\)|\\{|\\}";

    public static List<ScriptData> parseFunctions(String scriptText) {
        List<ScriptData> scripts = new ArrayList<>();
        List<ScriptToken> tokens = parseToTokens(scriptText);
        List<ScriptTokenFunction> functions = groupTokensToFunctions(tokens);
        for (ScriptTokenFunction function : functions) {
            ScriptData script = parseFunction(function);
            scripts.add(script);
        }
        return scripts;
    }

    public static Script parseExpression(String scriptText) {
        List<ScriptToken> tokens = parseToTokens(scriptText);
        return parseExpression(tokens);
    }

    public static Script parseScript(String scriptText) {
        List<ScriptToken> tokens = parseToTokens(scriptText);
        return parseScript(tokens);
    }

    public static Expression parseLiteral(String scriptText) {
        List<ScriptToken> tokens = parseToTokens(scriptText);
        return parseLiteral(tokens);
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
                tokens.add(new ScriptToken(ScriptTokenType.ASSIGNMENT));
            } else if (currentToken.equals("+=")) {
                tokens.add(new ScriptToken(ScriptTokenType.MODIFIER_PLUS));
            } else if (currentToken.equals("-=")) {
                tokens.add(new ScriptToken(ScriptTokenType.MODIFIER_MINUS));
            } else if (currentToken.equals("*=")) {
                tokens.add(new ScriptToken(ScriptTokenType.MODIFIER_MULTIPLY));
            } else if (currentToken.equals("/=")) {
                tokens.add(new ScriptToken(ScriptTokenType.MODIFIER_DIVIDE));
            } else if (currentToken.equals("%=")) {
                tokens.add(new ScriptToken(ScriptTokenType.MODIFIER_MODULO));
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
            } else if (currentToken.equals("^")) {
                tokens.add(new ScriptToken(ScriptTokenType.POWER));
            } else if (currentToken.equals(":")) {
                tokens.add(new ScriptToken(ScriptTokenType.COLON));
            } else if (currentToken.equals("?")) {
                tokens.add(new ScriptToken(ScriptTokenType.TERNARY_IF));
            } else if (currentToken.equals("!")) {
                tokens.add(new ScriptToken(ScriptTokenType.NOT));
            } else if (currentToken.equals("&&")) {
                tokens.add(new ScriptToken(ScriptTokenType.AND));
            } else if (currentToken.equals("||")) {
                tokens.add(new ScriptToken(ScriptTokenType.OR));
            } else if (currentToken.equals("==")) {
                tokens.add(new ScriptToken(ScriptTokenType.EQUAL));
            } else if (currentToken.equals("!=")) {
                tokens.add(new ScriptToken(ScriptTokenType.NOT_EQUAL));
            } else if (currentToken.equals("<=")) {
                tokens.add(new ScriptToken(ScriptTokenType.LESS_EQUAL));
            } else if (currentToken.equals(">=")) {
                tokens.add(new ScriptToken(ScriptTokenType.GREATER_EQUAL));
            } else if (currentToken.equals("<")) {
                tokens.add(new ScriptToken(ScriptTokenType.LESS));
            } else if (currentToken.equals(">")) {
                tokens.add(new ScriptToken(ScriptTokenType.GREATER));
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
            } else if (currentToken.equals("null")) {
                tokens.add(new ScriptToken(ScriptTokenType.NULL));
            } else if (currentToken.equals("return")) {
                tokens.add(new ScriptToken(ScriptTokenType.RETURN));
            } else if (currentToken.equals("break")) {
                tokens.add(new ScriptToken(ScriptTokenType.BREAK));
            } else if (currentToken.equals("continue")) {
                tokens.add(new ScriptToken(ScriptTokenType.CONTINUE));
            } else if (currentToken.equals("error")) {
                tokens.add(new ScriptToken(ScriptTokenType.ERROR));
            } else if (currentToken.equals("log")) {
                tokens.add(new ScriptToken(ScriptTokenType.LOG));
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
        Expression.DataType functionReturnType = parseFunctionReturnType(functionTokens.header());
        List<ScriptParameter> functionParameters = parseFunctionParameters(functionTokens.parameters());
        Script functionScript = parseScript(functionTokens.body());
        return new ScriptData(functionName, functionReturnType, functionParameters, false, functionScript);
    }

    private static Expression.DataType parseFunctionReturnType(List<ScriptToken> headerTokens) {
        Expression.DataType functionReturnType = null;
        if (headerTokens.size() == 3) {
            if (headerTokens.get(1).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function header has invalid return type");
            functionReturnType = stringToDataType(headerTokens.get(1).value);
        } else if (headerTokens.size() != 2) {
            throw new IllegalArgumentException("Function header contains unexpected tokens");
        }
        return functionReturnType;
    }

    private static List<ScriptParameter> parseFunctionParameters(List<ScriptToken> parameterTokens) {
        List<ScriptParameter> functionParameters = new ArrayList<>();
        List<List<ScriptToken>> parameterGroups = new ArrayList<>();
        int index = 0;
        while (index < parameterTokens.size()) {
            int nextCommaIndex = findFirstTokenIndex(parameterTokens, ScriptTokenType.COMMA, index);
            List<ScriptToken> currentGroup;
            if (nextCommaIndex == -1) {
                currentGroup = parameterTokens.subList(index, parameterTokens.size());
                index = parameterTokens.size();
            } else {
                currentGroup = parameterTokens.subList(index, nextCommaIndex);
                index = nextCommaIndex;
            }
            if (currentGroup.isEmpty() || currentGroup.size() == 2) throw new IllegalArgumentException("Function contains invalid parameter definition (1)");
            if (currentGroup.getFirst().type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function contains invalid parameter definition (2)");
            if (currentGroup.size() >= 3 && currentGroup.get(1).type != ScriptTokenType.ASSIGNMENT) throw new IllegalArgumentException("Function contains invalid parameter definition (3)");
            parameterGroups.add(currentGroup);
        }
        for (List<ScriptToken> parameterGroup : parameterGroups) {
            String parameterName = parameterGroup.getFirst().value;
            boolean parameterIsRequired = true;
            Expression parameterDefaultValue = null;
            if (parameterGroup.size() >= 3) {
                parameterIsRequired = false;
                parameterDefaultValue = parseLiteral(parameterGroup.subList(2, parameterGroup.size()));
            }
            functionParameters.add(new ScriptParameter(parameterName, parameterIsRequired, parameterDefaultValue));
        }
        return functionParameters;
    }

    // Provided token list should NOT be enclosed in brackets
    private static Script parseScript(List<ScriptToken> tokens) {
        List<Script> scripts = new ArrayList<>();
        int index = 0;
        while (index < tokens.size()) {
            if (tokens.get(index).type == ScriptTokenType.NAME && tokens.get(index).value.equals("if")) {
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
                Script script = parseIf(branches, bodyElse);
                scripts.add(script);
            } else if (tokens.get(index).type == ScriptTokenType.NAME && tokens.get(index).value.equals("for")) {
                if (tokens.get(index + 1).type != ScriptTokenType.PARENTHESIS_OPEN) throw new IllegalArgumentException("For loop is missing iterator");
                int iteratorEndIndex = findPairedClosingBracket(tokens, index + 1);
                if (iteratorEndIndex == -1) throw new IllegalArgumentException("For loop iterator is not closed");
                if (tokens.get(iteratorEndIndex + 1).type != ScriptTokenType.BRACKET_OPEN) throw new IllegalArgumentException("For loop is missing body");
                int bodyEndIndex = findPairedClosingBracket(tokens, iteratorEndIndex + 1);
                if (bodyEndIndex == -1) throw new IllegalArgumentException("For loop body is not closed");
                Script script = parseFor(tokens.subList(index + 1, iteratorEndIndex), tokens.subList(iteratorEndIndex + 2, bodyEndIndex));
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
        return new ScriptCompound(scripts);
    }

    private static Script parseIf(List<ScriptIfTokens> branches, List<ScriptToken> bodyElse) {
        List<ScriptConditional.ConditionalScriptPair> conditionalScriptPairs = new ArrayList<>();
        for (ScriptIfTokens branch : branches) {
            Script conditionExpression = parseExpression(branch.condition());
            Script scriptBranch = parseScript(branch.body());
            conditionalScriptPairs.add(new ScriptConditional.ConditionalScriptPair(conditionExpression, scriptBranch));
        }
        Script scriptElse = bodyElse == null ? null : parseScript(bodyElse);
        return new ScriptConditional(conditionalScriptPairs, scriptElse);
    }

    private static Script parseFor(List<ScriptToken> iterator, List<ScriptToken> body) {
        if (iterator.getFirst().type != ScriptTokenType.NAME || iterator.get(1).type != ScriptTokenType.COLON) throw new IllegalArgumentException("For loop has invalid iterator format");
        String iteratorVariableName = iterator.getFirst().value;
        // TODO - Check for invalid variable name
        Script iteratedValuesExpression = parseExpression(iterator.subList(2, iterator.size()));
        Script iteratedScript = parseScript(body);
        return new ScriptIterator(iteratedValuesExpression, iteratorVariableName, iteratedScript);
    }

    private static Script parseSingleInstruction(List<ScriptToken> tokens) {
        if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("var")) {
            // Variable declaration
            return parseVariableDeclaration(tokens);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.get(1).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            // Function call
            return parseFunctionCall(tokens);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("stat")) {
            // Stat assignment
            return parseStatAssignment(tokens);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("global")) {
            // Global assignment
            return parseGlobalAssignment(tokens);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME) {
            // Variable assignment
            return parseVariableAssignment(tokens);
        } else if (tokens.getFirst().type == ScriptTokenType.RETURN) {
            if (tokens.size() == 1) {
                return new ScriptReturn(null);
            }
            Script returnValue = parseExpression(tokens.subList(1, tokens.size()));
            return new ScriptReturn(returnValue);
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
            return new ScriptError(errorMessage);
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
            return new ScriptPrintLog(logMessage);
        } else if (tokens.getFirst().type == ScriptTokenType.BREAK) {
            if (tokens.size() != 1) throw new IllegalArgumentException("Break statement must be called on its own");
            return new ScriptFlowStatement(Script.FlowStatementType.BREAK);
        } else if (tokens.getFirst().type == ScriptTokenType.CONTINUE) {
            if (tokens.size() != 1) throw new IllegalArgumentException("Continue statement must be called on its own");
            return new ScriptFlowStatement(Script.FlowStatementType.CONTINUE);
        } else {
            throw new IllegalArgumentException("Script contains invalid instruction");
        }
    }

    private static Script parseVariableDeclaration(List<ScriptToken> tokens) {
        if (tokens.get(1).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Variable definition is missing a name");
        String variableName = tokens.get(1).value;
        if (RESERVED_KEYWORDS.contains(variableName)) throw new IllegalArgumentException("Variable name is reserved");
        if (tokens.size() == 2) {
            return new ScriptSetVariable(variableName, null, true);
        }
        if (tokens.get(2).type != ScriptTokenType.ASSIGNMENT) throw new IllegalArgumentException("Variable definition is missing assignment operator");
        Script variableValue = parseExpression(tokens.subList(3, tokens.size()));
        return new ScriptSetVariable(variableName, variableValue, true);
    }

    private static Script parseStatAssignment(List<ScriptToken> tokens) {
        int assignmentOperatorIndex = findFirstTokenIndexFromSet(tokens, Set.of(ScriptTokenType.ASSIGNMENT, ScriptTokenType.MODIFIER_PLUS, ScriptTokenType.MODIFIER_MINUS, ScriptTokenType.MODIFIER_MULTIPLY, ScriptTokenType.MODIFIER_DIVIDE, ScriptTokenType.MODIFIER_MODULO), 0);
        if (assignmentOperatorIndex == -1) throw new IllegalArgumentException("Stat assignment is missing assignment operator");
        ScriptStatReference statReference = parseStatReference(tokens.subList(0, assignmentOperatorIndex));
        Script statValue;
        if (tokens.get(assignmentOperatorIndex).type == ScriptTokenType.ASSIGNMENT) {
            statValue = parseExpression(tokens.subList(assignmentOperatorIndex + 1, tokens.size()));
        } else if (tokens.get(assignmentOperatorIndex).type == ScriptTokenType.MODIFIER_PLUS) {
            statValue = new ScriptAdd(new ScriptGetStat(statReference.statHolder(), statReference.name()), parseExpression(tokens.subList(assignmentOperatorIndex + 1, tokens.size())));
        } else if (tokens.get(assignmentOperatorIndex).type == ScriptTokenType.MODIFIER_MINUS) {
            statValue = new ScriptSubtract(new ScriptGetStat(statReference.statHolder(), statReference.name()), parseExpression(tokens.subList(assignmentOperatorIndex + 1, tokens.size())));
        } else if (tokens.get(assignmentOperatorIndex).type == ScriptTokenType.MODIFIER_MULTIPLY) {
            statValue = new ScriptMultiply(new ScriptGetStat(statReference.statHolder(), statReference.name()), parseExpression(tokens.subList(assignmentOperatorIndex + 1, tokens.size())));
        } else if (tokens.get(assignmentOperatorIndex).type == ScriptTokenType.MODIFIER_DIVIDE) {
            statValue = new ScriptDivide(new ScriptGetStat(statReference.statHolder(), statReference.name()), parseExpression(tokens.subList(assignmentOperatorIndex + 1, tokens.size())));
        } else { // Modulo modifier
            statValue = new ScriptDivide(new ScriptGetStat(statReference.statHolder(), statReference.name()), parseExpression(tokens.subList(assignmentOperatorIndex + 1, tokens.size())));
        }
        return new ScriptSetStat(statReference.statHolder(), statReference.name(), statValue);
    }

    private static Script parseGlobalAssignment(List<ScriptToken> tokens) {
        if (tokens.get(1).type != ScriptTokenType.DOT) throw new IllegalArgumentException("Global assignment is missing dot after global keyword");
        if (tokens.get(2).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Global assignment is missing global name");
        String globalName = tokens.get(2).value;
        Script globalValue;
        if (tokens.get(3).type == ScriptTokenType.ASSIGNMENT) {
            globalValue = parseExpression(tokens.subList(4, tokens.size()));
        } else if (tokens.get(3).type == ScriptTokenType.MODIFIER_PLUS) {
            globalValue = new ScriptAdd(new ScriptGetGlobal(globalName), parseExpression(tokens.subList(4, tokens.size())));
        } else if (tokens.get(3).type == ScriptTokenType.MODIFIER_MINUS) {
            globalValue = new ScriptSubtract(new ScriptGetGlobal(globalName), parseExpression(tokens.subList(4, tokens.size())));
        } else if (tokens.get(3).type == ScriptTokenType.MODIFIER_MULTIPLY) {
            globalValue = new ScriptMultiply(new ScriptGetGlobal(globalName), parseExpression(tokens.subList(4, tokens.size())));
        } else if (tokens.get(3).type == ScriptTokenType.MODIFIER_DIVIDE) {
            globalValue = new ScriptDivide(new ScriptGetGlobal(globalName), parseExpression(tokens.subList(4, tokens.size())));
        } else if (tokens.get(3).type == ScriptTokenType.MODIFIER_MODULO) {
            globalValue = new ScriptModulo(new ScriptGetGlobal(globalName), parseExpression(tokens.subList(4, tokens.size())));
        } else {
            throw new IllegalArgumentException("Global assignment is missing assignment operator");
        }
        return new ScriptSetGlobal(globalName, globalValue);
    }

    private static Script parseVariableAssignment(List<ScriptToken> tokens) {
        String variableName = tokens.getFirst().value;
        Script variableValue;
        if (tokens.get(1).type == ScriptTokenType.ASSIGNMENT) {
            variableValue = parseExpression(tokens.subList(2, tokens.size()));
        } else if (tokens.get(1).type == ScriptTokenType.MODIFIER_PLUS) {
            variableValue = new ScriptAdd(new ScriptGetVariable(variableName), parseExpression(tokens.subList(2, tokens.size())));
        } else if (tokens.get(1).type == ScriptTokenType.MODIFIER_MINUS) {
            variableValue = new ScriptSubtract(new ScriptGetVariable(variableName), parseExpression(tokens.subList(2, tokens.size())));
        } else if (tokens.get(1).type == ScriptTokenType.MODIFIER_MULTIPLY) {
            variableValue = new ScriptMultiply(new ScriptGetVariable(variableName), parseExpression(tokens.subList(2, tokens.size())));
        } else if (tokens.get(1).type == ScriptTokenType.MODIFIER_DIVIDE) {
            variableValue = new ScriptDivide(new ScriptGetVariable(variableName), parseExpression(tokens.subList(2, tokens.size())));
        } else if (tokens.get(1).type == ScriptTokenType.MODIFIER_MODULO) {
            variableValue = new ScriptModulo(new ScriptGetVariable(variableName), parseExpression(tokens.subList(2, tokens.size())));
        } else {
            throw new IllegalArgumentException("Variable assignment is missing assignment operator");
        }
        return new ScriptSetVariable(variableName, variableValue, false);
    }

    private static Script parseFunctionCall(List<ScriptToken> tokens) {
        String functionName = tokens.getFirst().value;
        List<ScriptExternal.ParameterContainer> parameters = parseFunctionCallParameters(tokens.subList(2, tokens.size() - 1));
        return new ScriptExternal(functionName, parameters);
    }

    private static List<ScriptExternal.ParameterContainer> parseFunctionCallParameters(List<ScriptToken> tokens) {
        List<ScriptExternal.ParameterContainer> parameters = new ArrayList<>();
        List<List<ScriptToken>> parameterGroups = new ArrayList<>();
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

            if (currentGroup.size() < 3) throw new IllegalArgumentException("Function call contains invalid parameter");
            if (currentGroup.getFirst().type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function call contains invalid parameter");
            if (currentGroup.get(1).type != ScriptTokenType.ASSIGNMENT) throw new IllegalArgumentException("Function call contains invalid parameter");
            parameterGroups.add(currentGroup);
        }
        for (List<ScriptToken> parameterGroup : parameterGroups) {
            String parameterName = parameterGroup.getFirst().value;
            Script parameterValue = parseExpression(parameterGroup.subList(2, parameterGroup.size()));
            parameters.add(new ScriptExternal.ParameterContainer(parameterName, parameterValue));
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
            if (firstTernaryElse == -1) throw new IllegalArgumentException("Ternary expression is missing an else expression");
            Script scriptCondition = parseOr(tokens.subList(0, firstTernaryOperator));
            Script scriptTrue = parseOr(tokens.subList(firstTernaryOperator + 1, firstTernaryElse));
            Script scriptFalse = parseTernary(tokens.subList(firstTernaryElse + 1, tokens.size()));
            return new ScriptTernary(scriptCondition, scriptTrue, scriptFalse);
        } else {
            return parseOr(tokens);
        }
    }

    private static Script parseOr(List<ScriptToken> tokens) {
        int lastOrOperator = findLastTokenIndex(tokens, ScriptTokenType.OR, tokens.size() - 1);
        if (lastOrOperator != -1) {
            Script firstExpression = parseOr(tokens.subList(0, lastOrOperator));
            Script secondExpression = parseAnd(tokens.subList(lastOrOperator + 1, tokens.size()));
            return new ScriptOr(List.of(firstExpression, secondExpression));
        } else {
            return parseAnd(tokens);
        }
    }

    private static Script parseAnd(List<ScriptToken> tokens) {
        int lastAndOperator = findLastTokenIndex(tokens, ScriptTokenType.AND, tokens.size() - 1);
        if (lastAndOperator != -1) {
            Script firstExpression = parseAnd(tokens.subList(0, lastAndOperator));
            Script secondExpression = parseComparator(tokens.subList(lastAndOperator + 1, tokens.size()));
            return new ScriptAnd(List.of(firstExpression, secondExpression));
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
                default -> throw new IllegalArgumentException("Expression contains an invalid comparator statement");
            };
            Script firstExpression = parseSum(tokens.subList(0, firstComparatorOperator));
            Script secondExpression = parseSum(tokens.subList(firstComparatorOperator + 1, tokens.size()));
            return new ScriptComparator(firstExpression, secondExpression, comparator);
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
            return new ScriptAdd(firstExpression, secondExpression);
        } else {
            return new ScriptSubtract(firstExpression, secondExpression);
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
            return new ScriptMultiply(firstExpression, secondExpression);
        } else if (tokens.get(lastProductOperator).type == ScriptTokenType.DIVIDE) {
            return new ScriptDivide(firstExpression, secondExpression);
        } else {
            return new ScriptModulo(firstExpression, secondExpression);
        }
    }

    private static Script parsePower(List<ScriptToken> tokens) {
        int firstPowerSymbol = findFirstTokenIndex(tokens, ScriptTokenType.POWER, 0);
        if (firstPowerSymbol != -1) {
            Script powerBase = parseNot(tokens.subList(0, firstPowerSymbol));
            Script powerExponent = parsePower(tokens.subList(firstPowerSymbol + 1, tokens.size()));
            return new ScriptPower(powerBase, powerExponent);
        } else {
            return parseNot(tokens);
        }
    }

    private static Script parseNot(List<ScriptToken> tokens) {
        if (tokens.getFirst().type == ScriptTokenType.NOT) {
            Script innerValue = parseValue(tokens.subList(1, tokens.size()));
            return new ScriptNot(innerValue);
        } else {
            return parseValue(tokens);
        }
    }

    private static Script parseValue(List<ScriptToken> tokens) {
        if (tokens.getFirst().type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            return parseExpression(tokens.subList(1, tokens.size() - 1));
        } else if (tokens.size() == 1 && tokens.getFirst().type == ScriptTokenType.NAME) {
            return new ScriptGetVariable(tokens.getFirst().value);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("stat")) {
            ScriptStatReference statReference = parseStatReference(tokens);
            return new ScriptGetStat(statReference.statHolder(), statReference.name());
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("statHolder")) {
            StatHolderReference statHolderReference = parseStatHolderReference(tokens);
            return new ScriptStatHolder(statHolderReference);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("global")) {
            ScriptGlobalReference globalReference = parseGlobalReference(tokens);
            return new ScriptGetGlobal(globalReference.name());
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("game")) {
            return parseGameValue(tokens);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && (tokens.getFirst().value.equals("set") || tokens.getFirst().value.equals("list"))) {
            return parseCollection(tokens);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.get(1).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            return parseFunctionCall(tokens);
        } else {
            Expression literalExpression = parseLiteral(tokens);
            return new ScriptExpression(literalExpression);
        }
    }

    private static Script parseCollection(List<ScriptToken> tokens) {
        if (tokens.getFirst().type != ScriptTokenType.NAME || (!tokens.getFirst().value.equals("set") && !tokens.getFirst().value.equals("list"))) throw new IllegalArgumentException("Collection constructor is missing set or list keyword");
        if (tokens.get(1).type != ScriptTokenType.PARENTHESIS_OPEN) throw new IllegalArgumentException("Collection constructor is missing value block");
        if (tokens.getLast().type != ScriptTokenType.PARENTHESIS_CLOSE) throw new IllegalArgumentException("Collection constructor value block is not closed");
        if (tokens.size() == 3) return new ScriptBuildSet(new ArrayList<>());
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
            return new ScriptBuildSet(collectionValues);
        } else {
            return new ScriptBuildList(collectionValues);
        }
    }

    private static Expression parseLiteral(List<ScriptToken> tokens) {
        if (tokens.size() != 1) throw new IllegalArgumentException("Invalid literal expression");
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
        throw new IllegalArgumentException("Invalid literal expression");
    }

    private static ScriptStatReference parseStatReference(List<ScriptToken> tokens) {
        if (tokens.getFirst().type != ScriptTokenType.NAME || !tokens.getFirst().value.equals("stat")) throw new IllegalArgumentException("Stat reference is missing stat keyword");
        if (tokens.get(1).type != ScriptTokenType.DOT) throw new IllegalArgumentException("Stat reference is missing period after stat keyword");
        int lastDotIndex = findLastTokenIndex(tokens, ScriptTokenType.DOT, tokens.size() - 1);
        if (lastDotIndex == -1) throw new IllegalArgumentException("Stat reference has invalid name");
        Script statName;
        if (lastDotIndex + 2 == tokens.size()) {
            if (tokens.getLast().type != ScriptTokenType.NAME) throw new IllegalArgumentException("Stat reference has invalid name");
            statName = Script.constant(tokens.getLast().value);
        } else if (tokens.size() > lastDotIndex + 1 && tokens.get(lastDotIndex + 1).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            statName = parseExpression(tokens.subList(lastDotIndex + 2, tokens.size() - 1));
        } else {
            throw new IllegalArgumentException("Stat reference has invalid name");
        }
        StatHolderReference statHolder = parseStatHolder(tokens.subList(2, lastDotIndex));
        return new ScriptStatReference(statName, statHolder);
    }

    private static StatHolderReference parseStatHolderReference(List<ScriptToken> tokens) {
        if (tokens.getFirst().type != ScriptTokenType.NAME || !tokens.getFirst().value.equals("statHolder")) throw new IllegalArgumentException("Stat holder reference is missing statHolder keyword");
        if (tokens.get(1).type != ScriptTokenType.DOT) throw new IllegalArgumentException("Stat holder reference is missing period after statHolder keyword");
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
            throw new IllegalArgumentException("Stat holder reference contains invalid stat holder type");
        }
        if (holderExpression == null && tokens.size() > lastHolderStartIndex + 1 && tokens.get(lastHolderStartIndex + 1).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            holderID = parseExpression(tokens.subList(lastHolderStartIndex + 2, tokens.size() - 1));
        }
        return new StatHolderReference(holderType, holderID, parentReference, holderExpression);
    }

    private static ScriptGlobalReference parseGlobalReference(List<ScriptToken> tokens) {
        if (tokens.size() != 3) throw new IllegalArgumentException();
        if (tokens.getFirst().type != ScriptTokenType.NAME || !tokens.getFirst().value.equals("global")) throw new IllegalArgumentException("Global reference is missing global keyword");
        if (tokens.get(1).type != ScriptTokenType.DOT) throw new IllegalArgumentException("Global reference is missing period after global keyword");
        if (tokens.get(2).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Global reference has invalid name");
        String globalName = tokens.get(2).value;
        return new ScriptGlobalReference(globalName);
    }

    private static Script parseGameValue(List<ScriptToken> tokens) {
        if (tokens.size() != 3) throw new IllegalArgumentException();
        if (tokens.getFirst().type != ScriptTokenType.NAME || !tokens.getFirst().value.equals("game")) throw new IllegalArgumentException("Game value reference is missing game keyword");
        if (tokens.get(1).type != ScriptTokenType.DOT) throw new IllegalArgumentException("Game value reference is missing period after game keyword");
        if (tokens.get(2).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Game value reference has invalid name");
        String gameValueName = tokens.get(2).value;
        return new ScriptGetGameValue(gameValueName);
    }

    private static int findFirstTokenIndex(List<ScriptToken> tokens, ScriptTokenType type, int startIndex) {
        if (startIndex < 0 || startIndex >= tokens.size()) throw new IllegalArgumentException("Start index is outside the valid range");
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
        if (startIndex < 0 || startIndex >= tokens.size()) throw new IllegalArgumentException("Start index is outside the valid range");
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

    private static int findFirstTokenIndexFromSet(List<ScriptToken> tokens, Set<ScriptTokenType> types, int startIndex) {
        if (startIndex < 0 || startIndex >= tokens.size()) throw new IllegalArgumentException("Start index is outside the valid range");
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
            }
        }
        return -1;
    }

    private static int findLastTokenIndexFromSet(List<ScriptToken> tokens, Set<ScriptTokenType> types, int startIndex) {
        if (startIndex < 0 || startIndex >= tokens.size()) throw new IllegalArgumentException("Start index is outside the valid range");
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

    private static Expression.DataType stringToDataType(String name) {
        return switch (name) {
            case "boolean" -> Expression.DataType.BOOLEAN;
            case "int" -> Expression.DataType.INTEGER;
            case "float" -> Expression.DataType.FLOAT;
            case "string" -> Expression.DataType.STRING;
            case "stringSet" -> Expression.DataType.SET;
            case "inventory" -> Expression.DataType.INVENTORY;
            case "noun" -> Expression.DataType.NOUN;
            case "void" -> null;
            default -> throw new IllegalArgumentException("Invalid data type name: " + name);
        };
    }

    public record ScriptData(String name, Expression.DataType returnType, List<ScriptParameter> parameters, boolean allowExtraParameters, Script script) {}

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

    public record ScriptParameter(String name, boolean isRequired, Expression defaultValue) {}

    private record ScriptTokenFunction(List<ScriptToken> header, List<ScriptToken> parameters, List<ScriptToken> body) {}

    private record ScriptIfTokens(List<ScriptToken> condition, List<ScriptToken> body) {}

    private record ScriptStatReference(Script name, StatHolderReference statHolder) {}

    private record ScriptGlobalReference(String name) {}

}
