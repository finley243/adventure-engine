package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.*;
import com.github.finley243.adventureengine.script.*;
import com.github.finley243.adventureengine.stat.StatHolderReference;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptParser {

    private enum ScriptTokenType {
        END_LINE, STRING, FLOAT, INTEGER, NAME, ASSIGNMENT, COMMA, DOT, PLUS, MINUS, DIVIDE, MULTIPLY, MODULO, POWER, PARENTHESIS_OPEN, PARENTHESIS_CLOSE, BRACKET_OPEN, BRACKET_CLOSE, BOOLEAN_TRUE, BOOLEAN_FALSE, COLON, NOT, AND, OR, EQUAL, NOT_EQUAL, GREATER, LESS, GREATER_EQUAL, LESS_EQUAL
    }

    private static final String REGEX_PATTERN = "/\\*[.*]+\\*/|//.*\n|\"(\\\\\"|[^\"])*\"|'(\\\\'|[^'])*'|_?[a-zA-Z][a-zA-Z0-9_]*|([0-9]*\\.[0-9]+|[0-9]+\\.?[0-9]*)f|[0-9]+|==|!=|<=|>=|<|>|;|=|,|\\.|\\+|-|/|\\*|%|\\^|:|!|&&|\\|\\||\\(|\\)|\\{|\\}";

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
                tokens.add(new ScriptToken(ScriptTokenType.ASSIGNMENT));
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
        Expression.DataType functionReturnType = null;
        if (functionTokens.header().size() == 3) {
            if (functionTokens.header().get(1).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Function header has invalid return type");
            functionReturnType = stringToDataType(functionTokens.header().get(1).value);
        } else if (functionTokens.header().size() != 2) {
            throw new IllegalArgumentException("Function header contains unexpected tokens");
        }
        Set<ScriptParameter> functionParameters = parseFunctionParameters(functionTokens.parameters());
        Script functionScript = parseScript(functionTokens.body());
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
            Expression parameterDefaultValue = null;
            if (parameterGroup.size() >= 3) {
                parameterDefaultValue = parseLiteral(parameterGroup.subList(2, parameterGroup.size()));
            }
            functionParameters.add(new ScriptParameter(parameterName, parameterDefaultValue));
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
        return new ScriptCompound(scripts, false);
    }

    private static Script parseIf(List<ScriptIfTokens> branches, List<ScriptToken> bodyElse) {
        List<ScriptConditional.ConditionalScriptPair> conditionalScriptPairs = new ArrayList<>();
        for (ScriptIfTokens branch : branches) {
            Script conditionExpression = parseExpression(branch.condition());
            Condition condition = new Condition(false, conditionExpression);
            Script scriptBranch = parseScript(branch.body());
            conditionalScriptPairs.add(new ScriptConditional.ConditionalScriptPair(condition, scriptBranch));
        }
        Script scriptElse = parseScript(bodyElse);
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
            if (tokens.get(1).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Variable definition is missing a name");
            String variableName = tokens.get(1).value;
            // TODO - Check for invalid names
            if (tokens.size() == 2) {
                return new ScriptSetVariable(Expression.constant(variableName), null);
            }
            if (tokens.get(2).type != ScriptTokenType.ASSIGNMENT) throw new IllegalArgumentException("Variable definition is missing assignment operator");
            Script variableValue = parseExpression(tokens.subList(3, tokens.size()));
            return new ScriptSetVariable(Expression.constant(variableName), variableValue);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.get(1).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            // Function call
            String functionName = tokens.getFirst().value;
            List<ScriptExternal.ParameterContainer> parameters = parseFunctionCallParameters(tokens.subList(2, tokens.size() - 1));
            return new ScriptExternal(functionName, parameters);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("stat")) {
            // Stat assignment
            int assignmentOperatorIndex = findFirstTokenIndex(tokens, ScriptTokenType.ASSIGNMENT, 0);
            if (assignmentOperatorIndex == -1) throw new IllegalArgumentException("Stat assignment is missing assignment operator");
            ScriptStatReference statReference = parseStatReference(tokens.subList(0, assignmentOperatorIndex));
            Script statValue = parseExpression(tokens.subList(assignmentOperatorIndex + 1, tokens.size()));
            return new ScriptSetState(statReference.statHolder(), statReference.name(), statValue);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME) {
            // Variable assignment
            if (tokens.get(1).type != ScriptTokenType.ASSIGNMENT) throw new IllegalArgumentException("Variable assignment is missing assignment operator");
            String variableName = tokens.getFirst().value;
            Script variableValue = parseExpression(tokens.subList(2, tokens.size()));
            return new ScriptSetVariable(Expression.constant(variableName), variableValue);
        } else {
            throw new IllegalArgumentException("Script contains invalid instruction");
        }
    }

    private static List<ScriptExternal.ParameterContainer> parseFunctionCallParameters(List<ScriptToken> tokens) {
        List<ScriptExternal.ParameterContainer> parameters = new ArrayList<>();
        List<List<ScriptToken>> parameterGroups = new ArrayList<>();
        int index = 0;
        while (index < tokens.size()) {
            int nextCommaIndex = findFirstTokenIndex(tokens, ScriptTokenType.COMMA, index);
            List<ScriptToken> currentGroup;
            if (nextCommaIndex == -1) {
                currentGroup = tokens.subList(index, tokens.size() - 1);
            } else {
                currentGroup = tokens.subList(index, nextCommaIndex);
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
        return parseOr(tokens);
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
        int lastAndOperator = findLastTokenIndex(tokens, ScriptTokenType.OR, tokens.size() - 1);
        if (lastAndOperator != -1) {
            Script firstExpression = parseAnd(tokens.subList(0, lastAndOperator));
            Script secondExpression = parseComparator(tokens.subList(lastAndOperator + 1, tokens.size()));
            return new ScriptAnd(List.of(firstExpression, secondExpression));
        } else {
            return parseComparator(tokens);
        }
    }

    private static Script parseComparator(List<ScriptToken> tokens) {
        int firstComparatorOperator = findFirstTokenIndexFromSet(tokens, Set.of(ScriptTokenType.EQUAL, ScriptTokenType.NOT_EQUAL, ScriptTokenType.GREATER, ScriptTokenType.LESS, ScriptTokenType.GREATER_EQUAL, ScriptTokenType.LESS_EQUAL));
        if (firstComparatorOperator != -1) {
            ExpressionCompare.Comparator comparator = switch (tokens.get(firstComparatorOperator).type) {
                case EQUAL -> ExpressionCompare.Comparator.EQUAL;
                case NOT_EQUAL -> ExpressionCompare.Comparator.NOT_EQUAL;
                case GREATER -> ExpressionCompare.Comparator.GREATER;
                case LESS -> ExpressionCompare.Comparator.LESS;
                case GREATER_EQUAL -> ExpressionCompare.Comparator.GREATER_EQUAL;
                case LESS_EQUAL -> ExpressionCompare.Comparator.LESS_EQUAL;
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
        int lastPlusOperator = findLastTokenIndex(tokens, ScriptTokenType.PLUS, tokens.size() - 1);
        int lastMinusOperator = findLastTokenIndex(tokens, ScriptTokenType.MINUS, tokens.size() - 1);
        if (lastPlusOperator > lastMinusOperator) {
            Script firstExpression = parseSum(tokens.subList(0, lastPlusOperator));
            Script secondExpression = parseProduct(tokens.subList(lastPlusOperator + 1, tokens.size()));
            return new ScriptAdd(firstExpression, secondExpression);
        } else if (lastPlusOperator < lastMinusOperator) {
            Script firstExpression = parseSum(tokens.subList(0, lastMinusOperator));
            Script secondExpression = parseProduct(tokens.subList(lastMinusOperator + 1, tokens.size()));
            return new ScriptSubtract(firstExpression, secondExpression);
        } else {
            return parseProduct(tokens);
        }
    }

    private static Script parseProduct(List<ScriptToken> tokens) {
        int lastMultiplyOperator = findLastTokenIndex(tokens, ScriptTokenType.MULTIPLY, tokens.size() - 1);
        int lastDivideOperator = findLastTokenIndex(tokens, ScriptTokenType.DIVIDE, tokens.size() - 1);
        if (lastMultiplyOperator > lastDivideOperator) {
            Script firstExpression = parseProduct(tokens.subList(0, lastMultiplyOperator));
            Script secondExpression = parsePower(tokens.subList(lastMultiplyOperator + 1, tokens.size()));
            return new ScriptMultiply(firstExpression, secondExpression);
        } else if (lastMultiplyOperator < lastDivideOperator) {
            Script firstExpression = parseProduct(tokens.subList(0, lastDivideOperator));
            Script secondExpression = parsePower(tokens.subList(lastDivideOperator + 1, tokens.size()));
            return new ScriptDivide(firstExpression, secondExpression);
        } else {
            return parsePower(tokens);
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
            return new ExpressionParameter(tokens.getFirst().value);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("stat")) {
            ScriptStatReference statReference = parseStatReference(tokens);
            return new ExpressionStat(statReference.statHolder(), statReference.name());
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("global")) {
            ScriptGlobalReference globalReference = parseGlobalReference(tokens);
            return new ExpressionGlobal(globalReference.name());
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.getFirst().value.equals("game")) {
            return parseGameValue(tokens);
        } else if (tokens.getFirst().type == ScriptTokenType.NAME && tokens.get(1).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            String functionName = tokens.getFirst().value;
            List<ScriptExternal.ParameterContainer> parameters = parseFunctionCallParameters(tokens.subList(2, tokens.size() - 1));
            return new ScriptExternal(functionName, parameters);
        } else {
            Expression literalExpression = parseLiteral(tokens);
            if (literalExpression == null) throw new IllegalArgumentException("Expression contains an invalid value");
            return literalExpression;
        }
    }

    private static Expression parseLiteral(List<ScriptToken> tokens) {
        if (tokens.size() != 1) return null;
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
            return null;
        }
    }

    private static ScriptStatReference parseStatReference(List<ScriptToken> tokens) {
        if (tokens.getFirst().type != ScriptTokenType.NAME || !tokens.getFirst().value.equals("stat")) throw new IllegalArgumentException("Stat reference is missing stat keyword");
        if (tokens.get(1).type != ScriptTokenType.DOT) throw new IllegalArgumentException("Stat reference is missing period after stat keyword");
        int lastDotIndex = findLastTokenIndex(tokens, ScriptTokenType.DOT, tokens.size() - 1);
        Script statName;
        if (lastDotIndex + 2 == tokens.size()) {
            if (tokens.getLast().type != ScriptTokenType.NAME) throw new IllegalArgumentException("Stat reference has invalid name");
            statName = Expression.constant(tokens.getLast().value);
        } else if (tokens.get(lastDotIndex + 1).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            statName = parseExpression(tokens.subList(lastDotIndex + 2, tokens.size() - 1));
        } else {
            throw new IllegalArgumentException("Stat reference has invalid name");
        }
        StatHolderReference statHolder = parseStatHolder(tokens.subList(2, lastDotIndex));
        return new ScriptStatReference(statName, statHolder);
    }

    private static StatHolderReference parseStatHolder(List<ScriptToken> tokens) {
        int lastHolderStartIndex = 0;
        StatHolderReference parentReference = null;
        int lastDotIndex = findLastTokenIndex(tokens, ScriptTokenType.DOT, tokens.size() - 1);
        if (lastDotIndex != -1) {
            lastHolderStartIndex = lastDotIndex + 1;
            parentReference = parseStatHolder(tokens.subList(0, lastDotIndex));
        }
        if (tokens.get(lastHolderStartIndex).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Stat holder reference contains invalid stat holder type");
        String holderType = tokens.get(lastHolderStartIndex).value;
        Script holderID = null;
        if (tokens.size() > lastHolderStartIndex + 1 && tokens.get(lastHolderStartIndex + 1).type == ScriptTokenType.PARENTHESIS_OPEN && tokens.getLast().type == ScriptTokenType.PARENTHESIS_CLOSE) {
            holderID = parseExpression(tokens.subList(lastHolderStartIndex + 2, tokens.size()));
        } else if (tokens.size() - lastHolderStartIndex != 1) {
            throw new IllegalArgumentException("Stat holder reference contains invalid stat holder type");
        }
        return new StatHolderReference(holderType, holderID, parentReference);
    }

    private static ScriptGlobalReference parseGlobalReference(List<ScriptToken> tokens) {
        if (tokens.size() != 3) throw new IllegalArgumentException();
        if (tokens.getFirst().type != ScriptTokenType.NAME || !tokens.getFirst().value.equals("global")) throw new IllegalArgumentException("Global reference is missing global keyword");
        if (tokens.get(1).type != ScriptTokenType.DOT) throw new IllegalArgumentException("Global reference is missing period after global keyword");
        if (tokens.get(2).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Global reference has invalid name");
        String globalName = tokens.get(2).value;
        return new ScriptGlobalReference(globalName);
    }

    private static Expression parseGameValue(List<ScriptToken> tokens) {
        if (tokens.size() != 3) throw new IllegalArgumentException();
        if (tokens.getFirst().type != ScriptTokenType.NAME || !tokens.getFirst().value.equals("game")) throw new IllegalArgumentException("Game value reference is missing game keyword");
        if (tokens.get(1).type != ScriptTokenType.DOT) throw new IllegalArgumentException("Game value reference is missing period after game keyword");
        if (tokens.get(2).type != ScriptTokenType.NAME) throw new IllegalArgumentException("Game value reference has invalid name");
        String gameValueName = tokens.get(2).value;
        switch (gameValueName) {
            case "day" -> { return new ExpressionDay(); }
            case "month" -> { return new ExpressionMonth(); }
            case "year" -> { return new ExpressionYear(); }
            case "weekday" -> { return new ExpressionWeekday(); }
        }
        throw new IllegalArgumentException("Game value reference specifies a non-existent game value");
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

    private static int findFirstTokenIndexFromSet(List<ScriptToken> tokens, Set<ScriptTokenType> types) {
        Deque<ScriptTokenType> bracketStack = new ArrayDeque<>();
        for (int i = 0; i < tokens.size(); i++) {
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

    private record ScriptIfTokens(List<ScriptToken> condition, List<ScriptToken> body) {}

    private record ScriptStatReference(Script name, StatHolderReference statHolder) {}

    private record ScriptGlobalReference(String name) {}

}
