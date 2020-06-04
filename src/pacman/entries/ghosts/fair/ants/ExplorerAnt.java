package pacman.entries.ghosts.fair.ants;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pacman.entries.ghosts.fair.calcutil.Parameters;
import pacman.entries.ghosts.fair.environment.AntNode;
import pacman.entries.ghosts.fair.environment.PheromoneType;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * The ExplorerAnts are sent out from Ms. Pac-Man's position. They are intended
 * to mark the possible paths Ms. Pac-Man might take for the HunterAnts.
 * 
 * @author Iris Hunkeler
 *
 */
public class ExplorerAnt extends Ant {

	private static final Logger LOG = LogManager.getLogger(ExplorerAnt.class);

	public ExplorerAnt(AntNode startNode, MOVE moveMade) {
		super(startNode, moveMade, PheromoneType.EXPLORER);
	}

	public double calculateDistanceToNearestGhost(Game game) {
		Double distanceAntCurrentToNearestGhost = null;
		for (GHOST ghost : GHOST.values()) {
			if (game.getGhostLairTime(ghost) <= 0) {
				double distanceToGhost = game.getDistance(getCurrentNode().getNodeIndex(),
						game.getGhostCurrentNodeIndex(ghost), DM.PATH);
				if (distanceAntCurrentToNearestGhost == null || distanceAntCurrentToNearestGhost > distanceToGhost) {
					distanceAntCurrentToNearestGhost = distanceToGhost;
				}
			}
		}
		if (distanceAntCurrentToNearestGhost == null) {
			distanceAntCurrentToNearestGhost = 1000d;
		}

		return distanceAntCurrentToNearestGhost;
	}
	
	//Tim & Roger
	public double calculateDistanceAntToNearestPowerpill(Game game) {
		Double distanceNodeCurrentToNearestPowerpill = null;
		for (int powerpillNodeIndex: game.getPowerPillIndices()) {
			double distanceToPowerPill = game.getDistance(getCurrentNode().getNodeIndex(),
					powerpillNodeIndex, DM.PATH);
			if (distanceNodeCurrentToNearestPowerpill == null || distanceNodeCurrentToNearestPowerpill > distanceToPowerPill) {
				distanceNodeCurrentToNearestPowerpill = distanceToPowerPill;
			}
		}
		if (distanceNodeCurrentToNearestPowerpill == 0) {
			distanceNodeCurrentToNearestPowerpill = 1d;
		}
		LOG.trace("ExplorerAnt {} distance to nearest Powerpill {}.", getAntId(), distanceNodeCurrentToNearestPowerpill);

		return distanceNodeCurrentToNearestPowerpill;
	}
	
	// NEW: Alternative power pill - Tim & Roger
	// returns true if a power pill is on the node
	public boolean isPowerPillOnNode(Game game) {
		LOG.trace("ExplorerAnt {} on node {} Powerpillindices  {}.", getAntId(), this.getCurrentNode().getNodeIndex(), Arrays.toString(game.getPowerPillIndices()));
		return IntStream.of(game.getPowerPillIndices()).anyMatch(x -> x == this.getCurrentNode().getNodeIndex());
	}
	
	@Override
	public boolean isStoppingConditionReached(Game game) {

		// check if maximum distance has been travelled
		if (getNodesVisited().size() > Parameters.MAX_DISTANCE_PER_EXPLORER) {
			LOG.trace("ExplorerAnt {} stopped on node {} because MaxDistance of {} is reached.", getAntId(), getCurrentNode().getNodeIndex(), Parameters.MAX_DISTANCE_PER_EXPLORER);
			return true;
		}

		boolean isPowerPillOnNode = false;

		// check if ghost can be faster at this point
		Double distanceAntCurrentToNearestGhost = calculateDistanceToNearestGhost(game);
		if (distanceAntCurrentToNearestGhost < getNodesVisited().size()) {
			isPowerPillOnNode = isPowerPillOnNode(game);
			LOG.trace("ExplorerAnt {} stopped on node {} because a ghost can reach this point earlier.", getAntId(), getCurrentNode().getNodeIndex());

			// Power pill - Tim & Roger
			// According to Recio, an ant will stop if a ghost can reach the node before the
			// ant
			// UNLESS there is a power pill on the way
			if(Parameters.POWERPILLS_EXPLORER) {
				isPowerPillOnNode = isPowerPillOnNode(game);
				if (isPowerPillOnNode) {
					return false;
				}
			}
			return true;
		}

		// check if there are still neighboring nodes left to visit
		int[] possibleAntMoves = game.getNeighbouringNodes(getCurrentNode().getNodeIndex(), getLastMoveMade());
		for (int i = 0; i < possibleAntMoves.length; i++) {
			if (!isNodeVisited(possibleAntMoves[i])) {
				return false;
			}
		}
		LOG.trace("ExplorerAnt {} stopped on node {} because all Neighbouring Nodes have already been visited.", getAntId(), getCurrentNode().getNodeIndex());
		return true;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("ExplorerAnt[");
		sb.append("antId:").append(getAntId()).append(";");
		sb.append("quality:").append(getQuality()).append(";");
		sb.append("nodesVisited: ");
		for (AntNode antNode : getNodesVisited()) {
			sb.append(antNode.getNodeIndex()).append(",");
		}
		sb.append("]\n");

		return sb.toString();
	}

}
