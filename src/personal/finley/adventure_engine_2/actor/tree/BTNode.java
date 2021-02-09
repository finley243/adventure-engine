package personal.finley.adventure_engine_2.actor.tree;

import java.util.ArrayList;
import java.util.List;

public abstract class BTNode {

	public enum NodeState{
		SUCCESS, FAILED, RUNNING
	}
	
	protected List<BTNode> childNodes;
	protected int indexRunning; // Index of currently running node, -1 if none are running
	protected boolean isRunning;
	
	public BTNode() {
		this.childNodes = new ArrayList<BTNode>();
		this.indexRunning = -1;
		this.isRunning = false;
	}
	
	public void addChildNode(BTNode node) {
		childNodes.add(node);
	}

	// Should be overridden in every subclass
	public NodeState execute() {
		return NodeState.FAILED;
	}
	
	public void resetRunning() {
		this.indexRunning = -1;
		this.isRunning = false;
		for(BTNode node : childNodes) {
			node.resetRunning();
		}
	}
	
}
