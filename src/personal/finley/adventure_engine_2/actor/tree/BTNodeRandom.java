package personal.finley.adventure_engine_2.actor.tree;

import java.util.concurrent.ThreadLocalRandom;

public class BTNodeRandom extends BTNode {
	
	public BTNodeRandom() {
		super();
	}

	@Override
	public NodeState execute() {
		if(this.isRunning) {
			NodeState childState = childNodes.get(indexRunning).execute();
			return childState;
		}
		int randomIndex = ThreadLocalRandom.current().nextInt(childNodes.size());
		NodeState childState = childNodes.get(randomIndex).execute();
		switch(childState) {
			case RUNNING:
				this.indexRunning = randomIndex;
				this.isRunning = true;
				return NodeState.RUNNING;
		}
		return childState;
	}
	
}
