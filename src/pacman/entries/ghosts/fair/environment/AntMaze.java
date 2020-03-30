package pacman.entries.ghosts.fair.environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pacman.entries.ghosts.fair.calcutil.Parameters;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

/**
 * The AntMaze represents the environment in which the HunterAnts and ExplorerAnts need to find their path.
 * The AntMaze is build based on the current graph of the game (which changes according to the level).
 * 
 * @author Iris Hunkeler
 */
public class AntMaze {
	
	private Map<Integer, AntNode> antNodes;
	private Map<String, AntEdge> edges;
	
	public AntMaze(Game game) {
		edges = new HashMap<String, AntEdge>();
		antNodes = new HashMap<Integer, AntNode>();
		init(game);
	}	
	
	private void init(Game game) {
		
		for(int i = 0; i < game.getCurrentMaze().graph.length; i++)  {
			// create AntNodes
			Node nodeA = game.getCurrentMaze().graph[i];
			AntNode antNodeA =  new AntNode(nodeA);
			antNodes.put(nodeA.nodeIndex, antNodeA);
			
			// create AntEdges
			createEdgeIfNeeded(antNodeA, MOVE.RIGHT);
			createEdgeIfNeeded(antNodeA, MOVE.LEFT);
			createEdgeIfNeeded(antNodeA, MOVE.UP);
			createEdgeIfNeeded(antNodeA, MOVE.DOWN);
			
		}

	}

	private void createEdgeIfNeeded(AntNode antNodeA, MOVE move) {
		if(antNodeA.getGameNode().neighbourhood.get(move) != null) {
			if(antNodes.get(antNodeA.getGameNode().neighbourhood.get(move)) != null) {
				AntNode antNodeB = antNodes.get(antNodeA.getGameNode().neighbourhood.get(move));

				if(getEdge(antNodeA, antNodeB) == null) {
					AntEdge newEdge = new AntEdge(antNodeA, antNodeB, 1);
					edges.put(newEdge.getStringCode(), newEdge);
				}
				
			}						
		}			
	}
	
	
	public AntNode getAntNode(int i) {
		return antNodes.get(i);
	}
	
	public AntEdge getEdge(AntNode nodeA, AntNode nodeB) {
		return edges.get(getStringCode(nodeA, nodeB));
	}
	
	public boolean containsEdge(AntNode nodeA, AntNode nodeB) {
		return edges.get(getStringCode(nodeA, nodeB)) != null;
	}
		
	public String getStringCode(AntNode nodeA, AntNode nodeB) {
		if(nodeA.getNodeIndex() < nodeB.getNodeIndex()) {
			return nodeA.getNodeIndex() + "-" + nodeB.getNodeIndex();
		} else {
			return nodeB.getNodeIndex() + "-" + nodeA.getNodeIndex();
		}		
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		List<String> keyList = new ArrayList<String>(edges.keySet());
		Collections.sort(keyList);
			
		sb.append("antEdges [ ");
		for(int i = 0; i < keyList.size(); i++) {
			String s = keyList.get(i);
			AntEdge edge = edges.get(s);
			if(edge.hasPheromone()) {
				sb.append(s).append(": EXPLORER=").append(edge.getPheromone(PheromoneType.EXPLORER));
				sb.append("|BLINKY=").append(edge.getPheromone(PheromoneType.BLINKY));
				sb.append("|PINKY=").append(edge.getPheromone(PheromoneType.PINKY));
				sb.append("|INKY=").append(edge.getPheromone(PheromoneType.INKY));
				sb.append("|SUE=").append(edge.getPheromone(PheromoneType.SUE)).append(",\n");
			}
		}
		sb.append("]");
		
		return sb.toString();
	}
	
	public Set<AntEdge> getEdgesWithPheromones(PheromoneType pht) {
		Set<AntEdge> edgesWithPheromones = new HashSet<AntEdge>();
		
		for(String edgeKey : edges.keySet()) {
			if(edges.get(edgeKey).getPheromone(pht) != Parameters.MINIMUM_PHEROMONE_LEVEL){
				edgesWithPheromones.add(edges.get(edgeKey));
			}
		}
		
		return edgesWithPheromones;
	}


	

}
