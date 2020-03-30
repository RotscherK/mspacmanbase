package pacman.entries.ghosts.fair.environment;

import java.util.HashMap;
import java.util.Map;

import pacman.entries.ghosts.fair.calcutil.Parameters;

/**
 * An AntEdge represents one Edge in the AntMaze
 * 
 * @author Iris Hunkeler
 */
public class AntEdge implements Comparable<AntEdge> {

	private AntNode nodeA;
	private AntNode nodeB;
	private int cost;


	
	private Map<PheromoneType, Double> pheromoneLevels = new HashMap<PheromoneType, Double>();

	public AntEdge(AntNode nodeA, AntNode nodeB, int cost) {
		this.nodeA = nodeA;
		this.nodeB = nodeB;
		this.cost = cost;
		
		pheromoneLevels.put(PheromoneType.EXPLORER, Parameters.MINIMUM_PHEROMONE_LEVEL);
		pheromoneLevels.put(PheromoneType.BLINKY, Parameters.MINIMUM_PHEROMONE_LEVEL);
		pheromoneLevels.put(PheromoneType.PINKY, Parameters.MINIMUM_PHEROMONE_LEVEL);
		pheromoneLevels.put(PheromoneType.INKY, Parameters.MINIMUM_PHEROMONE_LEVEL);
		pheromoneLevels.put(PheromoneType.SUE, Parameters.MINIMUM_PHEROMONE_LEVEL);		
	}

	public Double getPheromone(PheromoneType type){
		return pheromoneLevels.get(type);
	}
	
	public void setPheromone(PheromoneType type, double pheromoneLevel) {
		pheromoneLevels.put(type, pheromoneLevel);
	}
	
	public AntNode getNodeA() {
		return nodeA;
	}

	public AntNode getNodeB() {
		return nodeB;
	}

	public boolean containsNodes(AntNode nodeA, AntNode nodeB) {
		if ((this.getNodeA().equals(nodeA) && this.getNodeB().equals(nodeB)) || (this.getNodeA().equals(nodeB) && this.getNodeB().equals(nodeA))) {
			return true;
		} else {
			return false;
		}
	}

	public String getStringCode() {
		if(nodeA.getNodeIndex() < nodeB.getNodeIndex()) {
			return nodeA.getNodeIndex() + "-" + nodeB.getNodeIndex();
		} else {
			return nodeB.getNodeIndex() + "-" + nodeA.getNodeIndex();
		}		
	}
	
	@Override
	public int compareTo(AntEdge o) {
		if (this.containsNodes(o.getNodeA(), o.getNodeB())) {
			return 0;
		} else {
			return this.getNodeA().compareTo(o.getNodeA());
		}
	}
	
	public AntNode getNextAntNode(AntNode startNode) {
		if(getNodeA() == startNode) {
			return getNodeB();
		} else {
			return getNodeA();
		}
	}
	
	public boolean hasPheromone(){
		for(Double d : pheromoneLevels.values()) {
			if(d != Parameters.MINIMUM_PHEROMONE_LEVEL) {
				return true;
			}
		}
		
		return false;
	}

	public int getCost() {
		return cost;
	}
	
	


}
