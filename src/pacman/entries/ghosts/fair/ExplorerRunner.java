package pacman.entries.ghosts.fair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pacman.entries.ghosts.fair.ants.ExplorerAnt;
import pacman.entries.ghosts.fair.calcutil.Calculations;
import pacman.entries.ghosts.fair.calcutil.Parameters;
import pacman.entries.ghosts.fair.environment.AntEdge;
import pacman.entries.ghosts.fair.environment.AntMaze;
import pacman.entries.ghosts.fair.environment.AntNode;
import pacman.entries.ghosts.fair.environment.PheromoneType;
import pacman.game.Constants.MOVE;
import pacman.game.util.Log;
import pacman.game.Game;


/**
 * The ExplorerRunner uses a Game and an AntMaze to estimate the possible next moves of Ms. Pac-Man
 * 
 * @author Iris Hunkeler
 *
 */
public class ExplorerRunner {
	
    private static final Logger LOG = LogManager.getLogger(ExplorerRunner.class);	
	
	private AntMaze maze;

	private Game game;

	public ExplorerRunner(Game game, AntMaze maze) {
		this.game = game;		
		this.maze = maze;
	}
	
	/**
	 * Do a full run of ExplorerAnts on the defined AntMaze
	 * 
	 * @return the AntMaze after the run of all ExplorerAnts
	 */
	public AntMaze runExplorerAnts() {
		LOG.debug("PACMAN is now on {}", game.getPacmanCurrentNodeIndex());
		List<ExplorerAnt> allExplorerAnts = new ArrayList<ExplorerAnt>();
		
		while (allExplorerAnts.size() < Parameters.MAX_EXPLORER) {
			List<ExplorerAnt> explorerAnts = initializeExplorerAnts();
			allExplorerAnts.addAll(explorerAnts);
			
			runExplorerAnts(explorerAnts);
			ExplorerAnt bestAntOfIteration = evaluateExplorerSolutionQuality(explorerAnts);
			
			doExplorerPheromoneUpdate(explorerAnts, bestAntOfIteration);
		}
		
		VisualUtil.drawPheromones(game, maze, PheromoneType.EXPLORER);	
		
		return maze;
	}
	
	
	/**
	 * @return All ExplorerAnts for one iteration (Ants on every position around pacman)
	 */
	private List<ExplorerAnt> initializeExplorerAnts() {
		List<ExplorerAnt> explorerAnts = new ArrayList<ExplorerAnt>();
		int[] nodesToStartExplorerAnts = game.getNeighbouringNodes(game.getPacmanCurrentNodeIndex());
		for (int i = 0; i < nodesToStartExplorerAnts.length; i++) {
			MOVE moveMade = game.getMoveToMakeToReachDirectNeighbour(game.getPacmanCurrentNodeIndex(), nodesToStartExplorerAnts[i]);
			ExplorerAnt e = new ExplorerAnt(maze.getAntNode(nodesToStartExplorerAnts[i]), moveMade);
			explorerAnts.add(e);
			LOG.trace("ExplorerAnt {} created on node {} {} from PACMAN", e.getAntId(), e.getCurrentNode().getNodeIndex(), e.getLastMoveMade());
		}
		return explorerAnts;
	}
	
	/**
	 * Let all ExplorerAnts run until they reach their stopping condition
	 * @param explorerAnts
	 */
	private void runExplorerAnts(List<ExplorerAnt> explorerAnts) {
		for (ExplorerAnt e : explorerAnts) {
			while (true) {
				moveExplorerAntOneStep(e);
				if (e.isStoppingConditionReached(game)) {
					break;
				}
			}
		}
	}


	/**
	 * Handle the pheromone update for an ExlporerAnt
	 * @param explorerAnts the ExplorerAnts for which the pheromone update needs to be done
	 * @param bestAntOfIteration the best ExplorerAnt in the iteration
	 */
	private void doExplorerPheromoneUpdate(List<ExplorerAnt> explorerAnts, ExplorerAnt bestAntOfIteration) {
		for (ExplorerAnt e : explorerAnts) {
			for (int i = 0; i < e.getNodesVisited().size() - 1; i++) {
				AntNode nodeA = e.getNodesVisited().get(i);
				AntNode nodeB = e.getNodesVisited().get(i + 1);
				AntEdge edge = maze.getEdge(nodeA, nodeB);
				double oldPheromones = edge.getPheromone(PheromoneType.EXPLORER);
				double newPheromones = Calculations.getExplorerUpdatedPheromones(oldPheromones,  e.getQuality(), bestAntOfIteration.getQuality());
				edge.setPheromone(PheromoneType.EXPLORER, newPheromones);
								
				//TODO - Remove
				if(Double.valueOf(newPheromones).isNaN()) {
					System.out.println("halt");
				}
				LOG.trace("ExplorerAnt {} changes pheromones on edge {} from {} to {}", e.getAntId(), edge.getStringCode(), oldPheromones, newPheromones);
			}
		}
	}

	/**
	 * Identify and evaluate the possible next steps for an ExplorerAnt and chose one according to the defined rules.
	 * @param e the HunterAnt which needs to take a step
	 */
	private void moveExplorerAntOneStep(ExplorerAnt e) {	
		
		// find all choseable next nodes (neighbouring node and not yet visited)
		int[] neighbouringNodes = game.getNeighbouringNodes(e.getCurrentNode().getNodeIndex(), e.getLastMoveMade());
		Set<AntNode> choosableNodes = new HashSet<AntNode>();
		for (int i = 0; i < neighbouringNodes.length; i++) {
			if (!e.isNodeVisited(neighbouringNodes[i])) {
				choosableNodes.add(maze.getAntNode(neighbouringNodes[i]));
			}
		}
		
		/*
		// get total desirability and desirability of each node
		double totalDesirability = 0;
		Map<AntNode, Double> desirabilityOfNodes = new HashMap<AntNode, Double>();
		for (AntNode choosableNode : choosableNodes) {
			AntEdge edge = maze.getEdge(e.getCurrentNode(), choosableNode);
			
			double pheromonesExplorer = edge.getPheromone(PheromoneType.EXPLORER);
			
			double heuristicValue = Calculations.getHeuristicGhostValue(choosableNode.getNodeIndex(), h.getGhost(), game);
			double desirability = totalPheromones * heuristicValue;
			totalDesirability += desirability;
			desirabilityOfNodes.put(choosableNode, desirability);

			possibilities.put(game.getMoveToMakeToReachDirectNeighbour(h.getCurrentNode().getNodeIndex(), choosableNode.getNodeIndex()), desirability);
		}
		*/
		
		
		// chose node
		AntNode nextNode = null;
		if (Math.random() > Parameters.EXPLORATION_RATE_EXPLORER) {
			/* CHOSE NODE WITH HIGHEST PHEROMONE LEVEL */
			
			AntEdge edgeWithHighestPheromone = null;
			for (AntNode possibleNextAntNode : choosableNodes) {
				AntEdge edge = maze.getEdge(e.getCurrentNode(), possibleNextAntNode);
				double pheromones = edge.getPheromone(PheromoneType.EXPLORER);

				if (edgeWithHighestPheromone == null || edgeWithHighestPheromone.getPheromone(PheromoneType.EXPLORER) < pheromones) {
					edgeWithHighestPheromone = edge;
				}
			}

			nextNode = edgeWithHighestPheromone.getNextAntNode(e.getCurrentNode());

		} else {		
			/* CHOSE NODE RANDOMLY */
			int random = new Random().nextInt(choosableNodes.size());
			int i = 0;
			for(AntNode obj : choosableNodes) {
			    if (i == random) {
			    	nextNode = obj;
			    }
			    i = i + 1;
			}
		}

		MOVE moveMadeAnt = game.getMoveToMakeToReachDirectNeighbour(e.getCurrentNode().getNodeIndex(), nextNode.getNodeIndex());
		e.moveToNode(moveMadeAnt, nextNode, null, null);
	}
	
	/**
	 * Evaluate the solution quality of explorerAnts and set the quality for each Ant
	 * 
	 * @param explorerAnts
	 * @return
	 */
	private ExplorerAnt evaluateExplorerSolutionQuality(List<ExplorerAnt> explorerAnts) {
		ExplorerAnt bestAntOfIteration = null;
		for (ExplorerAnt e : explorerAnts) {
			double quality = Calculations.getExplorerAntSolutionQuality(e, game);
			e.setQuality(quality);

			if (bestAntOfIteration == null || bestAntOfIteration.getQuality() < quality) {
				bestAntOfIteration = e;
			}
		}
		
		LOG.trace("ExplorerAnt {} has been evaluated as best ant of iteration with quality {}",  bestAntOfIteration.getAntId(), bestAntOfIteration.getQuality());

		return bestAntOfIteration;
	}	
}
