package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.HashMap;
import java.util.Map;

/**
 * An action that can be executed at a given time
 */
public abstract class Script {

	private final Condition condition;
	private final Map<String, Expression> localParameters;

	public Script(Condition condition, Map<String, Expression> localParameters) {
		this.condition = condition;
		this.localParameters = localParameters;
	}

	/**
	 * Execute the script if the conditions are met
	 * @param context Contains the contextual references (subject, target, etc.)
	 */
	public void execute(Context context) {
		if (canExecute(context)) {
			Context localContext;
			if (generateInnerContext()) {
				/*Map<String, Expression> localParametersComputed = new HashMap<>();
				for (Map.Entry<String, Expression> entry : localParameters.entrySet()) {
					localParametersComputed.put(entry.getKey(), Expression.convertToConstant(entry.getValue(), context));
				}
				localContext = new Context(context, localParametersComputed);*/
				localContext = new Context(context);
			} else {
				localContext = context;
			}
			executeSuccess(localContext);
		} else {
			context.game().eventQueue().executeNext();
		}
	}

	/**
	 * Executed if all conditions are met when calling Script::execute
	 *
	 * @param context Contains the contextual references (subject, target, etc.)
	 */
	protected abstract void executeSuccess(Context context);

	protected boolean canExecute(Context context) {
		return condition == null || condition.isMet(context);
	}

	protected boolean generateInnerContext() {
		return false;
	}

}
