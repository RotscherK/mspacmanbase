package pacman.entries.ghosts.fair.ants;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import pacman.entries.ghosts.fair.ants.AntMove.AntMoveType;
import pacman.entries.ghosts.fair.calcutil.Parameters;
import pacman.entries.ghosts.fair.environment.AntNode;
import pacman.entries.ghosts.fair.environment.PheromoneType;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * The HunterAnts try to find the best path for one ghost.
 * 
 * @author Iris Hunkeler
 */
public class HunterAnt extends Ant {

    //private static final Logger LOG = LogManager.getLogger(HunterAnt.class);
    
	private GHOST ghost;
	private double quality;
	
	public HunterAnt(AntNode startNode, MOVE moveMade, GHOST ghost) {
		super(startNode, moveMade, PheromoneType.getPheromoneTypeOfGhost(ghost));
		this.ghost = ghost;
	}
	
	public GHOST getGhost(){
		return ghost;
	}


	public boolean isStoppingConditionReached(Game game) {
		// check if maximum distance has been travelled
		if (getNodesVisited().size() > Parameters.MAX_DISTANCE_PER_HUNTER ) {
			//LOG.trace("HunterAnt {} stopped on node {} because MaxDistance of {} is reached.", getAntId(), getCurrentNode().getNodeIndex(), Parameters.MAX_DISTANCE_PER_HUNTER);
			return true;
		}
		
		// check if pacmans position has been reached
		if (getCurrentNode().getNodeIndex() == game.getPacmanCurrentNodeIndex() ) {
			//LOG.trace("HunterAnt {} stopped on node {} because PACMAN has been reached.", getAntId(), getCurrentNode().getNodeIndex());
			return true;
		}

		// check if there are still neighboring nodes left to visit
		int[] possibleAntMoves = game.getNeighbouringNodes(getCurrentNode().getNodeIndex(), getLastMoveMade());
		for(int i = 0; i < possibleAntMoves.length; i++) {
			if(!isNodeVisited(possibleAntMoves[i])) {
				return false;
			}
		}		

		//LOG.trace("HunterAnt {} stopped on node {} because all Neighbouring Nodes have already been visited.", getAntId(), getCurrentNode().getNodeIndex());
		return true;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("HunterAnt[");
		sb.append("antId:").append(getAntId()).append(";");
		sb.append("quality:").append(quality).append(";");
		sb.append("nodesVisited: ");
		for(AntNode antNode : getNodesVisited()) {
			sb.append(antNode.getNodeIndex()).append(",");
		}
		sb.append("\nmovements(chosen): ");
		for(AntMove move : getMovements()) {
			if(move.getType().equals(AntMoveType.CHOSEN_MAX_PHER) || move.getType().equals(AntMoveType.CHOSEN_PROBABILITY) ) { 
				sb.append("   ").append(move).append(",");
			}
		}
		sb.append("]\n");
		
		return sb.toString();
	}

}
