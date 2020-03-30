package pacman.entries.ghosts.fair.environment;

import pacman.game.Constants.GHOST;

/**
 * Types of Pheromone for each ghost and Ms. Pac-Man
 * 
 * @author Iris Hunkeler
 */
public enum PheromoneType {
	EXPLORER,
	BLINKY, 
	PINKY, 
	INKY, 
	SUE;

	public static PheromoneType getPheromoneTypeOfGhost(GHOST ghost) {
		for (PheromoneType type : PheromoneType.values()) {
			if (type.toString().equals(ghost.name())) {
				return type;
			}
		}
		return null;
	}
}