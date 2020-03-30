package pacman.entries.ghosts.fair.ants;

import java.util.Map;

import pacman.game.Constants.MOVE;

/**
 * An AntMove shows what choice an Ant took at a certain point and shows the alternative possibilties and reasoning behind a choice
 * 
 * @author Iris Hunkeler
 */
public class AntMove {

	public enum AntMoveType {
		FORCED,
		CHOSEN_MAX_PHER,
		CHOSEN_PROBABILITY		
	}
	
	private AntMoveType type;
	private MOVE direction;
	
	private Map<MOVE, Double> possibilities;
	
	public AntMove(MOVE direction, AntMoveType type, Map<MOVE, Double> possibilities) {
		this.direction = direction;
		this.type = type;
		this.possibilities = possibilities;
	}

	public MOVE getDirection() {
		return direction;
	}
	public void setDirection(MOVE direction) {
		this.direction = direction;
	}

	public AntMoveType getType() {
		return type;
	}

	public void setType(AntMoveType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {		
		StringBuilder sb = new StringBuilder();
		
		sb.append("AntMove[");
		sb.append("type:").append(getType()).append(";");
		sb.append("direction:").append(direction).append(";");
		sb.append("possibilities: ");
		for(MOVE m : possibilities.keySet()) {
			sb.append(m + ": " + possibilities.get(m)).append(",");
		}
		sb.append("]\n");
		
		return sb.toString();
	}
	
	
}
