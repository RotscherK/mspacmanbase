package pacman.entries.ghosts.fair.calcutil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import pacman.entries.ghosts.fair.ants.ExplorerAnt;
import pacman.entries.ghosts.fair.ants.HunterAnt;
import pacman.entries.ghosts.fair.environment.AntEdge;
import pacman.entries.ghosts.fair.environment.AntMaze;
import pacman.entries.ghosts.fair.environment.AntNode;
import pacman.entries.ghosts.fair.environment.PheromoneType;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

/**
 * The Calculations implement the needed mathematical functions needed for the ACO algorithm.
 * 
 * @author Iris Hunkeler
 */
public class Calculations {

	
	/**
	 * [Function 28]: Calculate the solution quality for one HunterAnt 
	 * 
	 * @return The quality of one HunterAnt 
	 */
	public static double getHunterAntSolutionQuality(HunterAnt h, AntMaze maze, Game game){
		double quality = 0;
		
		for(int i = 0; i < h.getNodesVisited().size(); i++) {
			
			// distance walked by ant (can't be 0 for mathematical reasons)
			double distanceAntStartToAntCurrent = i;
			if(distanceAntStartToAntCurrent==0) {
				distanceAntStartToAntCurrent=1;
			}
			
			// both nodes for the edge to consider
			AntNode nodeJ = h.getNodesVisited().get(i);
			AntNode nodeI;
			if(i == 0) {
				nodeI = maze.getAntNode(game.getGhostCurrentNodeIndex( h.getGhost()	));			
			} else {
				nodeI = h.getNodesVisited().get(i-1);
			}

			// calculate quality of edge
			double edgeQuality = 0;
			edgeQuality = getHeuristicGhostValue(nodeI.getNodeIndex(), h.getGhost(), game)/distanceAntStartToAntCurrent;
			edgeQuality = edgeQuality * maze.getEdge(nodeI, nodeJ).getPheromone(PheromoneType.EXPLORER);

			// calculate total quality of ant
			quality += edgeQuality;
		}
		
		return quality;
	}
	
	/**
	 * [Function 22] Calculate the solution quality for one Explorer Ant
	 * @param e
	 * @param game
	 * @return The quality of one ExplorerAnt
	 */
	public static double getExplorerAntSolutionQuality(ExplorerAnt e, Game game) {
		double totalPathQuality = 0;
		
		for(int i = 0; i < e.getNodesVisited().size(); i++) {
			
			// distance walked by ant (can't be 0 for mathematical reasons)
			double distanceAntStartToAntCurrent = i;
			if(distanceAntStartToAntCurrent==0) {
				distanceAntStartToAntCurrent=1;
			}
			
			// distance to nearest ghost
			Double distanceAntCurrentToNearestGhost = e.calculateDistanceToNearestGhost(game);	
			
			double edgeQuality = distanceAntCurrentToNearestGhost/Math.pow(distanceAntStartToAntCurrent, 2);
			totalPathQuality += edgeQuality;
		}

		return totalPathQuality;
	}
	
	/**
	 * [Function 24 and 25]: Calculate the new pheromone level for an edge visited by a HunterAnt
	 * 
	 * @param oldPheromones the old Pheromone Level
	 * @param solutionQuality the solution quality of the HunterAnt which visited the edge to calculate
	 * @return new pheromone level the new Pheromone level to be set on the edge
	 */
	public static double getHunterUpdatedPheromones(double oldPheromones, double solutionQuality){
		return (1 - Parameters.UPDATING_RATE_HUNTER) * oldPheromones + (Parameters.UPDATING_RATE_HUNTER * (solutionQuality));
	}
	
	/**
	 * Based on [Function 20]: Calculate the new pheromone level for an edge visited by a Hunter Ant
	 * contrary to the original function, no pheromone evaporation is done.
	 * 
	 * @param oldPheromones the old pheromone level
	 * @param solutionQuality the solution quality of the HunterAnt which visited the edge to calculate
	 * @param solutionQualityBestAnt the solution Quality of the best Ant in the iteration
	 * @return new pheromone level to be set on the edge
	 */
	public static double getExplorerUpdatedPheromones(double oldPheromones, double solutionQuality, double solutionQualityBestAnt){
		return oldPheromones  + (solutionQuality / solutionQualityBestAnt);		
//		return (1 - Parameters.UPDATING_RATE_EXPLORER) * oldPheromones + (Parameters.UPDATING_RATE_EXPLORER * (solutionQuality));
	}
	
	/**
	 * [Function 23]: Calculate the heuristic rule for HunterAnts, bast on how close they are to Ms. Pac-Man and whether their ghost is edible or not
	 * 
	 * ATTENTION: This formula as proposed by Recio et al. would be the other way around, which was deemed to be a mistake!
	 * 
	 * @return the distance between the node and pacman if not edible, its inverse value otherwise
	 */
	public static double getHeuristicGhostValue(int nodeindex, GHOST ghost, Game game) {
		double heuristicGhostValue = game.getDistance(nodeindex, game.getPacmanCurrentNodeIndex(), DM.PATH);
		if(heuristicGhostValue==0) {
			heuristicGhostValue=1;
		}
		
		//the closer the better
		if(!game.isGhostEdible(ghost)) {
			heuristicGhostValue = 1 / heuristicGhostValue;
		}
		
		return heuristicGhostValue;
	}	
	
	/**
	 * [Function 11]
	 * @param e
	 * @param neighbouringNodes
	 * @param game
	 * @return
	 */
	public static double getHeuristicExplorerValue(ExplorerAnt e, int[] neighbouringNodes, Game game) {	
		double distanceToGhost = e.calculateDistanceToNearestGhost(game);
		double heuristicExplorerValue = distanceToGhost + neighbouringNodes.length;
		
		return heuristicExplorerValue;
	}	
	
	/**
	 * [Function 4]: Select a next node based on the rule of maximised desirability.
	 * 
	 * @param desirabilityOfNodes
	 * @param nextNode
	 * @return the next node to chose for one ant
	 */
	public static AntNode choseNextAntNodeBasedOnMaxDesirability(Map<AntNode, Double> desirabilityOfNodes, AntNode nextNode) {
		AntEdge edgeWithHighestDesirability = null;
		double highestDesirability = 0;
		for (AntNode possibleNextAntNode : desirabilityOfNodes.keySet()) {
			double desirability = desirabilityOfNodes.get(possibleNextAntNode);

			if (edgeWithHighestDesirability == null || highestDesirability < desirability) {
				highestDesirability = desirability;
				nextNode = possibleNextAntNode;
			}
		}
		return nextNode;
	} 
	
	
	/**
	 * [Function 5]: Select a next node based on the rule of probability based on desirability.
	 * 
	 * @param totalDesirability
	 * @param desirabilityOfNodes
	 * @param nextNode
	 * @return the next node to chose for one ant
	 */
	public static AntNode choseNextAntNodeBasedOnProbability(double totalDesirability, Map<AntNode, Double> desirabilityOfNodes, AntNode nextNode) {
		// calculate relative desirability for each possible node
		Map<AntNode, Double> relativeDesirabilityMap = new HashMap<AntNode, Double>();
		for (AntNode possibleNextAntNode : desirabilityOfNodes.keySet()) {
			double desirability = desirabilityOfNodes.get(possibleNextAntNode);
			double relativeDesirability = desirability / totalDesirability;
			if(Double.isNaN(relativeDesirability)) {
				relativeDesirability = 0;
			}
			relativeDesirabilityMap.put(possibleNextAntNode, relativeDesirability);
		}

		// map relative desirabilities to ranges between 0 and 100
		int startRange = 0;
		Map<AntNode, Integer[]> desirabilities = new HashMap<AntNode, Integer[]>();
		for (AntNode node : relativeDesirabilityMap.keySet()) {
			int probability = new Double(relativeDesirabilityMap.get(node)* 100).intValue();
			if(probability == 0) {
				probability = 1;
			}
			int endRange = startRange + probability;
			desirabilities.put(node, new Integer[] { startRange, endRange });
			startRange = endRange + 1;
		}
		
		// get a random int and chose the node according to the range it belogs to
		int randomInt = new Random().nextInt(100);
		for (AntNode node : desirabilities.keySet()) {
			if (desirabilities.get(node)[0] <= randomInt && desirabilities.get(node)[1] >= randomInt) {
				nextNode = node;

			}
		}
		return nextNode;
	}
}
