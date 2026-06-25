package com.github.finley243.adventureengine.script.nodes;

import com.github.finley243.adventureengine.script.SourceRange;

public sealed interface ASTNode permits ASTBinaryOp, ASTBreak, ASTCollection, ASTCompound, ASTContextRef, ASTContinue, ASTError, ASTFile, ASTFor, ASTFunction, ASTFunctionCall, ASTGameDataRef, ASTGlobalAssignment, ASTGlobalRef, ASTIf, ASTIfBranch, ASTListIndexAssignment, ASTListIndexRef, ASTLiteral, ASTLog, ASTMemberAccess, ASTMemberAssignment, ASTParameter, ASTParameterDefinition, ASTPlayerRef, ASTReturn, ASTTernaryOp, ASTUnaryOp, ASTVar, ASTVarAssignment, ASTVarDeclaration, ASTWorldRef {
    SourceRange range();
}
