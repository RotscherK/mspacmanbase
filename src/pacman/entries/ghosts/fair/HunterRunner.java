package pacman.entries.ghosts.fair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import pacman.entries.ghosts.fair.ants.AntMove.AntMoveType;
import pacman.entries.ghosts.fair.ants.HunterAnt;
import pacman.entries.ghosts.fair.calcutil.Calculations;
import pacman.entries.ghosts.fair.calcutil.Parameters;
import pacman.entries.ghosts.fair.environment.AntEdge;
import pacman.entries.ghosts.fair.environment.AntMaze;
import pacman.entries.ghosts.fair.environment.AntNode;
import pacman.entries.ghosts.fair.environment.PheromoneType;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * The HunterRunner uses a Game and an AntMaze to establish the best next MOVE for a GHOST.
 * 
 * @author Iris Hunkeler
 *
 */
public class HunterRunner {

    //private static final Logger LOG = LogManager.getLogger(HunterRunner.class);

	private AntMaze maze;

	private Game game;

	public HunterRunner(Game game, AntMaze maze) {
		this.game = game;
		this.maze = maze;
	}

	/**
	 * Get the best Move for a ghost at the current game state and under consideration of the defined AntMaze.
	 * 
	 * @return MOVE for the ghost
	 */
	public MOVE getMoveForGhost(GHOST ghost) {
		//LOG.debug("{} is now on {}", ghost.name(), game.getGhostCurrentNodeIndex(ghost));
		List<HunterAnt> allHunterAnts = new ArrayList<HunterAnt>();

		// run all hunter ants
		while (allHunterAnts.size() < Parameters.MAX_HUNTER) {
			List<HunterAnt> hunterAnts = initializeHunterAnts(ghost);
			allHunterAnts.addAll(hunterAnts);
			
			runHunterAnts(hunterAnts);
			HunterAnt bestAntOfIteration = evaluateHunterSolutionQuality(hunterAnts);
			doHunterPheromoneUpdate(bestAntOfIteration);
		}

		// find and handle the bestHunterAnt
		HunterAnt bestHunterAnt = getBestHunterAnt(allHunterAnts);
		removeExplorerPheromonesOnPath(bestHunterAnt);
		AntNode nodeToChose = bestHunterAnt.getNodeToChoseForGhost();
		MOVE moveToMake = game.getMoveToMakeToReachDirectNeighbour(game.getGhostCurrentNodeIndex(ghost), nodeToChose.getNodeIndex());
		
		// debug information
		VisualUtil.drawAnt(game, bestHunterAnt);
		/*LOG.trace("HunterAnt {} is the best ant for {} with quality {}", bestHunterAnt.getAntId(), ghost.name(), bestHunterAnt.getQuality());
		if (game.doesGhostRequireAction(ghost)) {
			LOG.debug("{} choses to go {}", ghost.name(), moveToMake);
		} else {
			LOG.trace("{} keeps going {}", ghost.name(), moveToMake);
		}*/
		
		return moveToMake;
	}

	/**
	 * Removes all ExplorerPheromones on the path the HunterAnt took.
	 * @param h the HunterAnt whose path has to be considered
	 */
	private void removeExplorerPheromonesOnPath(HunterAnt h) {
		for (int i = 0; i < h.getNodesVisited().size() - 1; i++) {
			AntNode nodeA = h.getNodesVisited().get(i);
			AntNode nodeB = h.getNodesVisited().get(i + 1);

			AntEdge edge = maze.getEdge(nodeA, nodeB);
			edge.setPheromone(PheromoneType.EXPLORER, 0);
		}
	}

	/**
	 * @param hunterAnts to be searche for the one with the best quality
	 * @return the ant with the highest quality from all hunterAnts
	 */
	private HunterAnt getBestHunterAnt(List<HunterAnt> hunterAnts) {
		HunterAnt bestHunterAnt = null;
		for (HunterAnt h : hunterAnts) {
			double quality = h.getQuality();
			if (bestHunterAnt == null || bestHunterAnt.getQuality() < quality) {
				bestHunterAnt = h;
			}
		}
		return bestHunterAnt;
	}
	
	/**
	 * Handle the pheromone evaporation and upate for one hunter ant
	 * @param ghost
	 * @param h
	 */
	private void doHunterPheromoneUpdate(HunterAnt h) {
		// do pheromone update for hunter ants
		for (int i = 0; i < h.getNodesVisited().size() - 1; i++) {
			AntNode nodeA = h.getNodesVisited().get(i);
			AntNode nodeB = h.getNodesVisited().get(i + 1);
			AntEdge edge = maze.getEdge(nodeA, nodeB);
			double oldPheromonesExplorer = edge.getPheromone(PheromoneType.EXPLORER);
			double oldPheromonesGhost = edge.getPheromone(PheromoneType.getPheromoneTypeOfGhost(h.getGhost()));
			double newPheromones = Calculations.getHunterUpdatedPheromones(oldPheromonesGhost,  h.getQuality());

			//LOG.trace("HunterAnt {} changes pheromones on edge {} from {} [{}]" + " to {}", h.getAntId(), edge.getStringCode(), oldPheromonesGhost, oldPheromonesExplorer, newPheromones);
			edge.setPheromone(PheromoneType.getPheromoneTypeOfGhost(h.getGhost()), newPheromones);
		}		
	}

	/**
	 * Let all hunter ants run until they reach their stopping condition
	 * @param hunterAnts
	 */
	private void runHunterAnts(List<HunterAnt> hunterAnts) {
		for (HunterAnt h : hunterAnts) {
			while (true) {
				moveHunterAntOneStep(h);
				if (h.isStoppingConditionReached(game)) {
					break;
				}
			}
		}
	}

	/**
	 * Evaluate the solution quality of hunterAnts and set the quality for each Ant
	 * @param hunterAnts
	 * @return
	 */
	private HunterAnt evaluateHunterSolutionQuality(List<HunterAnt> hunterAnts) {
		HunterAnt bestAntOfIteration = null;
		for (HunterAnt h : hunterAnts) {
			double quality = Calculations.getHunterAntSolutionQuality(h, maze, game);
			h.setQuality(quality);
			
			if (bestAntOfIteration == null || bestAntOfIteration.getQuality() < quality) {
				bestAntOfIteration = h;
			}
		}
		//LOG.trace("HunterAnt {} is the best ant of iteration with quality {}", bestAntOfIteration.getAntId(),  bestAntOfIteration.getQuality());
		return bestAntOfIteration;
	}

	/**
	 * @return All HunterAnts for one iteration (Ants on every possible next
	 *         position of the GHOST)
	 */
	private List<HunterAnt> initializeHunterAnts(GHOST ghost) {
		List<HunterAnt> hunterAnts = new ArrayList<HunterAnt>();
		int[] nodesToStartHunterAnts = game.getNeighbouringNodes(game.getGhostCurrentNodeIndex(ghost), game.getGhostLastMoveMade(ghost));
		for (int i = 0; i < nodesToStartHunterAnts.length; i++) {
			MOVE moveToMake = game.getMoveToMakeToReachDirectNeighbour(game.getGhostCurrentNodeIndex(ghost), nodesToStartHunterAnts[i]);
			HunterAnt h = new HunterAnt(maze.getAntNode(nodesToStartHunterAnts[i]), moveToMake, ghost);
			hunterAnts.add(h);
			//LOG.trace("HunterAnt {} created on node {} {} from {}", h.getAntId(), h.getCurrentNode().getNodeIndex(), moveToMake, ghost.name());
		}

		return hunterAnts;
	}

	/**
	 * Identify and evaluate the possible next steps for a HunterAnt and chose one according to the defined rules.
	 * @param h the HunterAnt which needs to take a step
	 */
	private void moveHunterAntOneStep(HunterAnt h) {

		AntMoveType moveType = null;
		Map<MOVE, Double> possibilities = new HashMap<MOVE, Double>();

		// find all choseable next nodes (neighbouring node and not yet visited)
		int[] neighbouringNodes = game.getNeighbouringNodes(h.getCurrentNode().getNodeIndex(), h.getLastMoveMade());
		Set<AntNode> choosableNodes = new HashSet<AntNode>();
		for (int i = 0; i < neighbouringNodes.length; i++) {
			if (!h.isNodeVisited(neighbouringNodes[i])) {
				choosableNodes.add(maze.getAntNode(neighbouringNodes[i]));
			}
		}

		// get total desirability and desirability of each node
		double totalDesirability = 0;
		Map<AntNode, Double> desirabilityOfNodes = new HashMap<AntNode, Double>();
		for (AntNode choosableNode : choosableNodes) {
			AntEdge edge = maze.getEdge(h.getCurrentNode(), choosableNode);
			double pheromonesExplorer = edge.getPheromone(PheromoneType.EXPLORER);
			double pheromonesGhost = edge.getPheromone(PheromoneType.getPheromoneTypeOfGhost(h.getGhost()));
			double totalPheromones = pheromonesExplorer + pheromonesGhost;
			double heuristicValue = Calculations.getHeuristicGhostValue(choosableNode.getNodeIndex(), h.getGhost(), game);
			double desirability = totalPheromones * heuristicValue;
			totalDesirability += desirability;
			desirabilityOfNodes.put(choosableNode, desirability);

			possibilities.put(game.getMoveToMakeToReachDirectNeighbour(h.getCurrentNode().getNodeIndex(), choosableNode.getNodeIndex()), desirability);
		}

		// chose node
		AntNode nextNode = null;
		if (choosableNodes.size() > 1) {
			
//			if (Math.random() < Parameters.EXPLORATION_RATE_HUNTER) {
				/* CHOSE NODE ACCORDING TO PROBABILITY BASED ON DESIRABILITY */
				moveType = AntMoveType.CHOSEN_PROBABILITY;
				nextNode = Calculations.choseNextAntNodeBasedOnProbability(totalDesirability, desirabilityOfNodes, nextNode);
//			} else {
//				/* CHOSE NODE WITH HIGHEST DESIRABILITY */
//				moveType = AntMoveType.CHOSEN_MAX_PHER;
//				nextNode = Calculations.choseNextAntNodeBasedOnMaxDesirability(desirabilityOfNodes, nextNode);
//			}
		} else {
			moveType = AntMoveType.FORCED;
			nextNode = desirabilityOfNodes.keySet().iterator().next();
		}

		// evaluate which move the ant chose and move it to the according node
		MOVE moveMadeAnt = game.getMoveToMakeToReachDirectNeighbour(h.getCurrentNode().getNodeIndex(), nextNode.getNodeIndex());
		h.moveToNode(moveMadeAnt, nextNode, moveType, possibilities);
		
		if (choosableNodes.size() > 1) {
			//LOG.trace("HunterAnt {} goes {} based on {}. Possibilities: {}", h.getAntId(), moveMadeAnt, moveType, possibilities);
		}		
	}
}
