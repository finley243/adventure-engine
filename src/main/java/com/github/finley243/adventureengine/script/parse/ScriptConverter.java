package com.github.finley243.adventureengine.script.parse;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.*;
import com.github.finley243.adventureengine.script.parse.nodes.*;

import java.util.ArrayList;
import java.util.List;

public class ScriptConverter {

    public List<ScriptFunction> convert(List<ASTFile> files) {
        List<ScriptFunction> result = new ArrayList<>();
        for (ASTFile file : files) {
            for (ASTNode node : file.functions()) {
                if (node instanceof ASTFunction function) {
                    result.add(convertFunction(function));
                }
            }
        }
        return result;
    }

    public Script convertInlineExpression(ASTNode expression) {
        return convertExpression(expression);
    }

    public Script convertInlineBlock(ASTCompound block) {
        return convertCompound(block);
    }

    public Expression convertInlineLiteral(ASTLiteral literal) {
        return convertLiteralToExpression(literal);
    }

    private ScriptFunction convertFunction(ASTFunction function) {
        List<ScriptParameter> params = new ArrayList<>();
        for (ASTNode param : function.parameters()) {
            if (param instanceof ASTParameterDefinition def) {
                boolean isRequired = def.defaultValue() == null;
                Expression defaultValue = def.defaultValue() != null ? convertLiteralToExpression(def.defaultValue()) : null;
                params.add(new ScriptParameter(def.name(), isRequired, defaultValue));
            }
        }
        Script body = convertCompound((ASTCompound) function.body());
        return new ScriptFunction(function.name(), params, false, body);
    }

    private Script convertStatement(ASTNode node) {
        return switch (node) {
            case ASTVarDeclaration decl ->
                    new ScriptSetVariable(trace(decl), decl.name(), convertExpression(decl.value()), true);
            case ASTVarAssignment assign ->
                    new ScriptSetVariable(trace(assign), assign.variable().name(), convertExpression(assign.value()), false);
            case ASTMemberAssignment assign -> {
                ScriptValueHolderReference holder = convertToHolderRef(assign.holder().object());
                Script statName = convertMemberName(assign.holder().name());
                yield new ScriptSetStat(trace(assign), holder, statName, convertExpression(assign.value()));
            }
            case ASTGlobalAssignment assign ->
                    new ScriptSetGlobal(trace(assign), convertExpression(assign.name().name()), convertExpression(assign.value()));
            case ASTListIndexAssignment assign ->
                    new ScriptListIndexSetInternal(trace(assign),
                            convertExpression(assign.listIndexRef().list()),
                            convertExpression(assign.listIndexRef().index()),
                            convertExpression(assign.value()));
            case ASTIf ifNode -> convertIf(ifNode);
            case ASTFor forNode ->
                    new ScriptIterator(trace(forNode), convertExpression(forNode.collection()), forNode.iteratorName(),
                            convertCompound((ASTCompound) forNode.body()));
            case ASTReturn ret ->
                    new ScriptReturn(trace(ret), ret.value() != null ? convertExpression(ret.value()) : null);
            case ASTBreak brk ->
                    new ScriptFlowStatement(trace(brk), Script.FlowStatementType.BREAK);
            case ASTContinue cont ->
                    new ScriptFlowStatement(trace(cont), Script.FlowStatementType.CONTINUE);
            case ASTFunctionCall call -> convertFunctionCall(call);
            case ASTLog log -> new ScriptPrintLog(trace(log), convertExpression(log.message()));
            case ASTError err -> new ScriptError(trace(err), convertExpression(err.message()));
            case ASTCompound compound -> convertCompound(compound);
            default -> throw new IllegalArgumentException("Unknown statement node: " + node.getClass().getSimpleName());
        };
    }

    private Script convertCompound(ASTCompound compound) {
        List<Script> statements = new ArrayList<>();
        for (ASTNode statement : compound.statements()) {
            statements.add(convertStatement(statement));
        }
        return new ScriptCompound(trace(compound), statements);
    }

    private Script convertIf(ASTIf ifNode) {
        List<ScriptConditional.ConditionalScriptPair> pairs = new ArrayList<>();
        for (ASTIfBranch branch : ifNode.branches()) {
            pairs.add(new ScriptConditional.ConditionalScriptPair(
                    convertExpression(branch.condition()),
                    convertStatement(branch.body())));
        }
        Script elseScript = ifNode.elseBranch() != null ? convertCompound(ifNode.elseBranch()) : null;
        return new ScriptConditional(trace(ifNode), pairs, elseScript);
    }

    private Script convertExpression(ASTNode node) {
        return switch (node) {
            case ASTLiteral lit -> new ScriptExpression(trace(lit), convertLiteralToExpression(lit));
            case ASTVar var -> new ScriptGetVariable(trace(var), var.name());
            case ASTBinaryOp op -> convertBinaryOp(op);
            case ASTUnaryOp op -> switch (op.operator()) {
                case NEGATE -> new ScriptNegate(trace(op), convertExpression(op.operand()));
                case NOT -> new ScriptNot(trace(op), convertExpression(op.operand()));
            };
            case ASTTernaryOp op ->
                    new ScriptTernary(trace(op), convertExpression(op.left()), convertExpression(op.center()), convertExpression(op.right()));
            case ASTMemberAccess access -> {
                ScriptValueHolderReference holder = convertToHolderRef(access.object());
                Script statName = convertMemberName(access.name());
                yield new ScriptGetStat(trace(access), holder, statName);
            }
            case ASTListIndexRef ref ->
                    new ScriptListIndexGetInternal(trace(ref), convertExpression(ref.list()), convertExpression(ref.index()));
            case ASTGlobalRef ref -> new ScriptGetGlobal(trace(ref), convertExpression(ref.name()));
            case ASTFunctionCall call -> convertFunctionCall(call);
            case ASTCollection col -> convertCollection(col);
            default -> throw new IllegalArgumentException("Unknown expression node: " + node.getClass().getSimpleName());
        };
    }

    private Script convertBinaryOp(ASTBinaryOp op) {
        Script left = convertExpression(op.left());
        Script right = convertExpression(op.right());
        Script.ScriptTraceData t = trace(op);
        return switch (op.operator()) {
            case ADD -> new ScriptAdd(t, left, right);
            case SUBTRACT -> new ScriptSubtract(t, left, right);
            case MULTIPLY -> new ScriptMultiply(t, left, right);
            case DIVIDE -> new ScriptDivide(t, left, right);
            case MODULO -> new ScriptModulo(t, left, right);
            case POWER -> new ScriptPower(t, left, right);
            case AND -> new ScriptAnd(t, List.of(left, right));
            case OR -> new ScriptOr(t, List.of(left, right));
            case EQUAL -> new ScriptComparator(t, left, right, ScriptComparator.Comparator.EQUAL);
            case NOT_EQUAL -> new ScriptComparator(t, left, right, ScriptComparator.Comparator.NOT_EQUAL);
            case LESS -> new ScriptComparator(t, left, right, ScriptComparator.Comparator.LESS);
            case GREATER -> new ScriptComparator(t, left, right, ScriptComparator.Comparator.GREATER);
            case LESS_EQUAL -> new ScriptComparator(t, left, right, ScriptComparator.Comparator.LESS_EQUAL);
            case GREATER_EQUAL -> new ScriptComparator(t, left, right, ScriptComparator.Comparator.GREATER_EQUAL);
            case NULL_COALESCING -> new ScriptNullCoalesce(t, left, right);
        };
    }

    private Script convertFunctionCall(ASTFunctionCall call) {
        List<ScriptExternal.ParameterContainer> params = new ArrayList<>();
        for (ASTNode param : call.parameters()) {
            if (param instanceof ASTParameter named) {
                params.add(new ScriptExternal.ParameterContainer(named.name(), convertExpression(named.value())));
            } else {
                params.add(new ScriptExternal.ParameterContainer(null, convertExpression(param)));
            }
        }
        return new ScriptExternal(trace(call), call.name(), params);
    }

    private Script convertCollection(ASTCollection col) {
        List<Script> elements = col.elements().stream().map(this::convertExpression).toList();
        return switch (col.type()) {
            case SET -> new ScriptBuildSet(trace(col), elements);
            case LIST -> new ScriptBuildList(trace(col), elements);
        };
    }

    private ScriptValueHolderReference convertToHolderRef(ASTNode node) {
        return switch (node) {
            case ASTPlayerRef ignored ->
                    new ScriptValueHolderReference("player", null, null, null);
            case ASTContextRef ref ->
                    new ScriptValueHolderReference(ref.name(), null, null, null);
            case ASTGameDataRef ref ->
                    new ScriptValueHolderReference(ref.type(), convertExpression(ref.id()), null, null);
            case ASTVar var ->
                    new ScriptValueHolderReference(null, null, null, new ScriptGetVariable(trace(var), var.name()));
            case ASTMemberAccess access -> {
                if (access.name() instanceof ASTMemberNameStatic(String name)) {
                    yield new ScriptValueHolderReference(name, null, convertToHolderRef(access.object()), null);
                } else {
                    // Dynamic sub-holder traversal: resolve as expression, wrap in holderExpression path
                    yield new ScriptValueHolderReference(null, null, null, convertExpression(access));
                }
            }
            default -> throw new IllegalArgumentException("Cannot convert to holder reference: " + node.getClass().getSimpleName());
        };
    }

    private Script convertMemberName(ASTMemberName name) {
        return switch (name) {
            case ASTMemberNameStatic s -> Script.constant(s.name());
            case ASTMemberNameDynamic d -> convertExpression(d.expression());
        };
    }

    private Expression convertLiteralToExpression(ASTLiteral lit) {
        return switch (lit.type()) {
            case STRING -> Expression.string(lit.value());
            case INTEGER -> Expression.integer(Integer.parseInt(lit.value()));
            case FLOAT -> Expression.decimal(Float.parseFloat(lit.value()));
            case BOOLEAN -> Expression.bool(Boolean.parseBoolean(lit.value()));
            case NULL -> null;
        };
    }

    private Script.ScriptTraceData trace(ASTNode node) {
        return new Script.ScriptTraceData(node.range().line(), node.range().fileName());
    }

}