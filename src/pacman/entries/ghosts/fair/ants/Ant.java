package pacman.entries.ghosts.fair.ants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import pacman.entries.ghosts.fair.ants.AntMove.AntMoveType;
import pacman.entries.ghosts.fair.environment.AntNode;
import pacman.entries.ghosts.fair.environment.PheromoneType;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * 
 * 
 * @author Iris Hunkeler
 */
public abstract class Ant {
	
	private static AtomicInteger seq = new AtomicInteger();
	
	private int antId;
	private List<AntNode> nodesVisited;
	private MOVE lastMoveMade;
	private PheromoneType pheromoneType;
	private double quality;
	private List<AntMove> movements;
		
	public Ant(AntNode startNode, MOVE moveMade, PheromoneType pheromoneType) {
		lastMoveMade = moveMade;			
		nodesVisited = new ArrayList<AntNode>();
		nodesVisited.add(startNode);
		this.pheromoneType = pheromoneType;
		this.antId = seq.incrementAndGet();
		movements = new ArrayList<AntMove>();
	}		
	
	public void moveToNode(MOVE moveToMake, AntNode newNode, AntMoveType type, Map<MOVE, Double> possibilities) {
		lastMoveMade = moveToMake;
		nodesVisited.add(newNode);
		movements.add(new AntMove(moveToMake, type, possibilities));
	}

	public AntNode getStartNode() {
		return nodesVisited.get(0);
	}

	public AntNode getCurrentNode() {
		return nodesVisited.get(nodesVisited.size()-1);
	}

	public MOVE getLastMoveMade() {
		return lastMoveMade;
	}

	public List<AntNode> getNodesVisited() {
		return nodesVisited;
	}	
	
	public AntNode getLastNode() {
		return nodesVisited.get(nodesVisited.size()-2);
	}
	
	public boolean isNodeVisited(int nodeIndex) {
		for(int i = 0; i < nodesVisited.size(); i++) {
			if (nodesVisited.get(i).getNodeIndex() == nodeIndex ){
				return true;
			}
		}
		return false;
	}
	
	public int[] getVisitedNodeIndices(){
		int visitedNodeIdices[] = new int[getNodesVisited().size()];
		for(int i = 0; i < getNodesVisited().size(); i++) {
			visitedNodeIdices[i] = getNodesVisited().get(i).getNodeIndex();
		}
		
		return visitedNodeIdices;
	}
		
	public PheromoneType getPheromoneType() {
		return pheromoneType;
	}

	public AntNode getNodeToChoseForGhost() {
		return nodesVisited.get(0);
	}
	
	public int getAntId() {
		return antId;
	}
	
	public List<AntMove> getMovements() {
		return movements;
	}
	
	
	public double getQuality() {
		return quality;
	}

	public void setQuality(double quality) {
		this.quality = quality;
	}
	
	public abstract boolean isStoppingConditionReached(Game game);
}