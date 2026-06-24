package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Inventory;
import com.github.finley243.adventureengine.script.parse.ScriptFunction;
import com.github.finley243.adventureengine.script.parse.ScriptParameter;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * An action that can be executed at a given time
 */
public abstract class Script {

	public enum FlowStatementType {
		RETURN, BREAK, CONTINUE
	}

	private static final int NATIVE_FUNCTION_LINE = -1;
	private static final String NATIVE_FUNCTION_FILENAME = "NATIVE";

	private final ScriptTraceData traceData;

	public Script(ScriptTraceData traceData) {
		this.traceData = traceData;
	}

	public Expression run(ScriptRuntime scriptRuntime, Context context) {
		Script.ScriptReturnData returnData = execute(scriptRuntime, context);
		if (returnData.error() != null) {
			throw new ScriptExecutionException(returnData.stackTrace());
		} else if (returnData.flowStatement() != null) {
			throw new ScriptExecutionException("Illegal flow statement in script: " + returnData.stackTrace());
		}
		return returnData.value();
	}

	/**
	 * Begin execution of the script (this method is meant to be used internally within Script objects)
	 *
	 * @param scriptRuntime The ScriptRuntime that allows scripts access to engine systems
	 * @param context       The context with which this script will be executed
	 */
	abstract ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context);

	protected ScriptTraceData getTraceData() {
		return traceData;
	}

	public static Script constant(boolean value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.bool(value));
	}

	public static Script constant(int value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.integer(value));
	}

	public static Script constant(float value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.decimal(value));
	}

	public static Script constant(String value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.string(value));
	}

	public static Script constant(Set<String> value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.set(value, Expression::string));
	}

	public static Script constant(Inventory value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.inventory(value));
	}

	public static Script constant(Noun value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.noun(value));
	}

	public static Script constant(ScriptValueHolder value) {
		return new ScriptExpression(new ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME), Expression.valueHolder(value));
	}

	public record ScriptReturnData(Expression value, FlowStatementType flowStatement, ScriptErrorData error) {
		public String stackTrace() {
			if (error == null) {
				return null;
			}
			return error.message() + "\n - (" + error.traceData().line() + ") " + error.traceData().fileName();
		}
	}

	public record ScriptErrorData(String message, ScriptTraceData traceData) {}

	public record ScriptTraceData(int line, String fileName) {}

	public static FunctionBuilder builder(String name, Function<ScriptTraceData, Script> scriptObject) {
		return new FunctionBuilder(name, scriptObject);
	}

	public static class FunctionBuilder {

		private final String name;
		private final List<ScriptParameter> parameters;
		private final Function<ScriptTraceData, Script> scriptConstructor;
		private boolean allowExtraParameters;

		private FunctionBuilder(String name, Function<ScriptTraceData, Script> scriptConstructor) {
			this.name = name;
			this.parameters = new ArrayList<>();
			this.scriptConstructor = scriptConstructor;
			this.allowExtraParameters = false;
		}

		public FunctionBuilder parameter(String name) {
			parameters.add(new ScriptParameter(name, true, null));
			return this;
		}

		public FunctionBuilder optionalParameter(String name) {
			parameters.add(new ScriptParameter(name, false, null));
			return this;
		}

		public FunctionBuilder optionalParameter(String name, Expression defaultValue) {
			parameters.add(new ScriptParameter(name, false, defaultValue));
			return this;
		}

		public FunctionBuilder allowExtraParameters() {
			this.allowExtraParameters = true;
			return this;
		}

		public ScriptFunction build() {
			return new ScriptFunction(name, parameters, allowExtraParameters, scriptConstructor.apply(generateTrace()));
		}

		private Script.ScriptTraceData generateTrace() {
			return new Script.ScriptTraceData(NATIVE_FUNCTION_LINE, NATIVE_FUNCTION_FILENAME);
		}

	}

}
