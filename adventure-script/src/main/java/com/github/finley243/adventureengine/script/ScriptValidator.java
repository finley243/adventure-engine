package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.script.nodes.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScriptValidator {

    public void validate(List<ASTFile> files, List<CompileError> errors, Set<String> nativeFunctions) {
        Set<String> knownFunctions = collectFunctionNames(files);
        knownFunctions.addAll(nativeFunctions);
        for (ASTFile file : files) {
            if (file == null) continue;
            for (ASTFunction function : file.functions()) {
                validateFunction(function, knownFunctions, errors);
            }
        }
    }

    public void validateInlineExpression(ASTNode expression, List<CompileError> errors, Set<String> knownFunctions, Set<String> externalVariables) {
        if (expression == null) return;
        ValidationContext ctx = new ValidationContext(false, new HashSet<>(externalVariables), knownFunctions);
        validateExpression(expression, ctx, errors);
    }

    public void validateInlineBlock(ASTCompound block, List<CompileError> errors, Set<String> knownFunctions, Set<String> externalVariables) {
        if (block == null) return;
        ValidationContext ctx = new ValidationContext(false, new HashSet<>(externalVariables), knownFunctions);
        validateCompound(block, ctx, errors);
    }

    private Set<String> collectFunctionNames(List<ASTFile> files) {
        Set<String> names = new HashSet<>();
        for (ASTFile file : files) {
            for (ASTFunction function : file.functions()) {
                names.add(function.name());
            }
        }
        return names;
    }

    private void validateFunction(ASTFunction function, Set<String> knownFunctions, List<CompileError> errors) {
        Set<String> variables = new HashSet<>();
        for (ASTNode param : function.parameters()) {
            if (param instanceof ASTParameterDefinition paramDef) {
                variables.add(paramDef.name());
            }
        }
        ValidationContext ctx = new ValidationContext(false, variables, knownFunctions);
        if (function.body() instanceof ASTCompound compound) {
            validateCompound(compound, ctx, errors);
        }
    }

    private ValidationContext validateStatement(ASTNode node, ValidationContext ctx, List<CompileError> errors) {
        return switch (node) {
            case ASTVarDeclaration decl -> {
                validateExpression(decl.value(), ctx, errors);
                if (ctx.variables().contains(decl.name())) {
                    errors.add(new CompileError("Variable already defined: " + decl.name(), decl.range()));
                }
                yield ctx.withVariable(decl.name());
            }
            case ASTVarAssignment assign -> {
                if (!ctx.variables().contains(assign.variable().name())) {
                    errors.add(new CompileError("Undefined variable: " + assign.variable().name(), assign.variable().range()));
                }
                validateExpression(assign.value(), ctx, errors);
                yield ctx;
            }
            case ASTMemberAssignment assign -> {
                validateExpression(assign.holder(), ctx, errors);
                validateExpression(assign.value(), ctx, errors);
                yield ctx;
            }
            case ASTGlobalAssignment assign -> {
                validateExpression(assign.name().name(), ctx, errors);
                validateExpression(assign.value(), ctx, errors);
                yield ctx;
            }
            case ASTListIndexAssignment assign -> {
                validateExpression(assign.listIndexRef().list(), ctx, errors);
                validateExpression(assign.listIndexRef().index(), ctx, errors);
                validateExpression(assign.value(), ctx, errors);
                yield ctx;
            }
            case ASTIf ifNode -> {
                validateIf(ifNode, ctx, errors);
                yield ctx;
            }
            case ASTFor forNode -> {
                validateFor(forNode, ctx, errors);
                yield ctx;
            }
            case ASTReturn ret -> {
                if (ret.value() != null) {
                    validateExpression(ret.value(), ctx, errors);
                }
                yield ctx;
            }
            case ASTBreak brk -> {
                if (!ctx.inLoop()) {
                    errors.add(new CompileError("'break' used outside of loop", brk.range()));
                }
                yield ctx;
            }
            case ASTContinue cont -> {
                if (!ctx.inLoop()) {
                    errors.add(new CompileError("'continue' used outside of loop", cont.range()));
                }
                yield ctx;
            }
            case ASTFunctionCall call -> {
                validateFunctionCall(call, ctx, errors);
                yield ctx;
            }
            case ASTLog log -> {
                validateExpression(log.message(), ctx, errors);
                yield ctx;
            }
            case ASTError err -> {
                validateExpression(err.message(), ctx, errors);
                yield ctx;
            }
            default -> {
                errors.add(new CompileError("Unexpected statement type: " + node.getClass().getSimpleName(), node.range()));
                yield ctx;
            }
        };
    }

    private void validateCompound(ASTCompound compound, ValidationContext ctx, List<CompileError> errors) {
        for (ASTNode statement : compound.statements()) {
            ctx = validateStatement(statement, ctx, errors);
        }
    }

    private void validateIf(ASTIf ifNode, ValidationContext ctx, List<CompileError> errors) {
        for (ASTIfBranch branch : ifNode.branches()) {
            validateExpression(branch.condition(), ctx, errors);
            if (branch.body() instanceof ASTCompound compound) {
                validateCompound(compound, ctx, errors);
            }
        }
        if (ifNode.elseBranch() != null) {
            validateCompound(ifNode.elseBranch(), ctx, errors);
        }
    }

    private void validateFor(ASTFor forNode, ValidationContext ctx, List<CompileError> errors) {
        validateExpression(forNode.collection(), ctx, errors);
        ValidationContext loopCtx = ctx.withInLoop().withVariable(forNode.iteratorName());
        if (forNode.body() instanceof ASTCompound compound) {
            validateCompound(compound, loopCtx, errors);
        }
    }

    private void validateExpression(ASTNode node, ValidationContext ctx, List<CompileError> errors) {
        if (node == null) return;
        switch (node) {
            case ASTLiteral ignored -> {}
            case ASTVar var -> {
                if (!ctx.variables().contains(var.name())) {
                    errors.add(new CompileError("Undefined variable: " + var.name(), var.range()));
                }
            }
            case ASTBinaryOp op -> {
                validateExpression(op.left(), ctx, errors);
                validateExpression(op.right(), ctx, errors);
            }
            case ASTUnaryOp op -> validateExpression(op.operand(), ctx, errors);
            case ASTTernaryOp op -> {
                validateExpression(op.left(), ctx, errors);
                validateExpression(op.center(), ctx, errors);
                validateExpression(op.right(), ctx, errors);
            }
            case ASTFunctionCall call -> validateFunctionCall(call, ctx, errors);
            case ASTMemberAccess access -> validateExpression(access.object(), ctx, errors);
            case ASTListIndexRef ref -> {
                validateExpression(ref.list(), ctx, errors);
                validateExpression(ref.index(), ctx, errors);
            }
            case ASTGlobalRef ref -> validateExpression(ref.name(), ctx, errors);
            case ASTGameDataRef ref -> validateExpression(ref.id(), ctx, errors);
            case ASTContextRef ignored -> {}
            case ASTPlayerRef ignored -> {}
            case ASTWorldRef ignored -> {}
            case ASTCollection col -> {
                for (ASTNode element : col.elements()) {
                    validateExpression(element, ctx, errors);
                }
            }
            default -> errors.add(new CompileError("Unexpected expression type: " + node.getClass().getSimpleName(), node.range()));
        }
    }

    private void validateFunctionCall(ASTFunctionCall call, ValidationContext ctx, List<CompileError> errors) {
        if (!ctx.knownFunctions().contains(call.name())) {
            errors.add(new CompileError("Unknown function: " + call.name(), call.nameRange()));
        }
        for (ASTNode param : call.parameters()) {
            if (param instanceof ASTParameter p) {
                validateExpression(p.value(), ctx, errors);
            } else {
                validateExpression(param, ctx, errors);
            }
        }
    }

    private record ValidationContext(boolean inLoop, Set<String> variables, Set<String> knownFunctions) {
        ValidationContext withInLoop() {
            return new ValidationContext(true, variables, knownFunctions);
        }
        ValidationContext withVariable(String name) {
            Set<String> newVariables = new HashSet<>(variables);
            newVariables.add(name);
            return new ValidationContext(inLoop, newVariables, knownFunctions);
        }
    }

}
