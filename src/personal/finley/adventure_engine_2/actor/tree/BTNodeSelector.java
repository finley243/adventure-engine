package personal.finley.adventure_engine_2.actor.tree;

/*
 * Tries child nodes until one succeeds. If none succeed, the node fails.
 */

public class BTNodeSelector extends BTNode {
	
	public BTNodeSelector() {
		super();
	}

	@Override
	public NodeState execute() {
		for(int i = 0; i < childNodes.size(); i++) {
			NodeState childState = childNodes.get(i).execute();
			switch(childState) {
				case SUCCESS:
					return NodeState.SUCCESS;
				case RUNNING:
					this.indexRunning = i;
					this.isRunning = true;
					return NodeState.RUNNING;
			}
		}
		return NodeState.FAILED;
	}
	
}
