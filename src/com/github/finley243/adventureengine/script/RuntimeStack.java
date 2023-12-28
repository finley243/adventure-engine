package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;

public class RuntimeStack {

    private final Deque<StackData> stack;

    public RuntimeStack() {
        this.stack = new ArrayDeque<>();
    }

    public Context getContext() {
        if (stack.peek() == null) throw new UnsupportedOperationException("Runtime stack is empty");
        return stack.peek().context;
    }

    public ScriptReturnTarget getReturnTarget() {
        if (stack.peek() == null) throw new UnsupportedOperationException("Runtime stack is empty");
        return stack.peek().returnTarget;
    }

    public int getIndex() {
        if (stack.peek() == null) throw new UnsupportedOperationException("Runtime stack is empty");
        return stack.peek().index;
    }

    public Expression removeQueuedExpression() {
        if (stack.peek() == null) throw new UnsupportedOperationException("Runtime stack is empty");
        if (stack.peek().expressionQueue == null) throw new UnsupportedOperationException("Runtime stack has no active expression queue");
        return stack.peek().expressionQueue.removeFirst();
    }

    public boolean expressionQueueIsEmpty() {
        if (stack.peek() == null) throw new UnsupportedOperationException("Runtime stack is empty");
        if (stack.peek().expressionQueue == null) throw new UnsupportedOperationException("Runtime stack has no active expression queue");
        return stack.peek().expressionQueue.isEmpty();
    }

    public Map.Entry<Item, Integer> removeQueuedItem() {
        if (stack.peek() == null) throw new UnsupportedOperationException("Runtime stack is empty");
        if (stack.peek().itemQueue == null) throw new UnsupportedOperationException("Runtime stack has no active item queue");
        return stack.peek().itemQueue.removeFirst();
    }

    public boolean itemQueueIsEmpty() {
        if (stack.peek() == null) throw new UnsupportedOperationException("Runtime stack is empty");
        if (stack.peek().itemQueue == null) throw new UnsupportedOperationException("Runtime stack has no active item queue");
        return stack.peek().itemQueue.isEmpty();
    }

    public void incrementIndex() {
        if (stack.peek() == null) throw new UnsupportedOperationException("Runtime stack is empty");
        stack.peek().index += 1;
    }

    public void closeContext() {
        if (stack.peek() == null) throw new UnsupportedOperationException("Runtime stack is empty");
        stack.pop();
    }

    public void addContext(Context context, ScriptReturnTarget returnTarget) {
        stack.push(new StackData(context, returnTarget, null, null));
    }

    public void addContextExpressionIterator(Context context, ScriptReturnTarget returnTarget, Collection<Expression> expressions) {
        stack.push(new StackData(context, returnTarget, new ArrayDeque<>(expressions), null));
    }

    public void addContextItemIterator(Context context, ScriptReturnTarget returnTarget, Collection<Map.Entry<Item, Integer>> items) {
        stack.push(new StackData(context, returnTarget, null, new ArrayDeque<>(items)));
    }

    private static class StackData {
        public final Context context;
        public final ScriptReturnTarget returnTarget;
        public int index;
        public final Deque<Expression> expressionQueue;
        public final Deque<Map.Entry<Item, Integer>> itemQueue;

        public StackData(Context context, ScriptReturnTarget returnTarget, Deque<Expression> expressionQueue, Deque<Map.Entry<Item, Integer>> itemQueue) {
            this.context = context;
            this.returnTarget = returnTarget;
            this.index = 0;
            this.expressionQueue = expressionQueue;
            this.itemQueue = itemQueue;
        }
    }

}
