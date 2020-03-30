package pacman.entries.ghosts.fair.environment;

import pacman.game.internal.Node;

/**
 * Represents one AntNode on the AntMaze
 * 
 * @author Iris Hunkeler
 */
public class AntNode implements Comparable<AntNode> {
	
	private Node node;

	public AntNode(Node node) {
		this.node = node;
	}
	
	
	public int getNodeIndex() {
		return node.nodeIndex;
	}
	
	public Node getGameNode() {
		return node;
	}
	
	@Override
	public int compareTo(AntNode o) {
		if (this.getNodeIndex() < o.getNodeIndex()) {
			return -1;
		} else if (this.getNodeIndex() > o.getNodeIndex()) {
			return 1;
		} else {
			return 0;
		}
	}	
}
