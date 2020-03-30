package pacman.entries.ghosts.fair;

import java.awt.Color;
import java.util.Set;

import pacman.entries.ghosts.fair.ants.Ant;
import pacman.entries.ghosts.fair.ants.HunterAnt;
import pacman.entries.ghosts.fair.calcutil.Parameters;
import pacman.entries.ghosts.fair.environment.AntEdge;
import pacman.entries.ghosts.fair.environment.AntMaze;
import pacman.entries.ghosts.fair.environment.PheromoneType;
import pacman.game.Game;
import pacman.game.GameViewExt;

/**
 * Utility Class to help visualize information on the game.
 * 
 * @author Iris Hunkeler
 */
public class VisualUtil {
		
	public static void drawAnt(Game game, Ant ant) {
		if(Parameters.EXPERIMENTAL_MODE){
			return;
		}
		
		Color color = Color.WHITE;
		
		if(ant instanceof HunterAnt) {
			if(game.isGhostEdible(((HunterAnt)ant).getGhost())) {
				color = Color.BLUE;
			} else if (ant.getPheromoneType().equals(PheromoneType.BLINKY)) {
				color = Color.RED;
			}  else if (ant.getPheromoneType().equals(PheromoneType.PINKY)) {
				color = Color.PINK;
			}  else if (ant.getPheromoneType().equals(PheromoneType.INKY)) {
				color = Color.CYAN;
			}  else if (ant.getPheromoneType().equals(PheromoneType.SUE)) {
				color = Color.ORANGE;
			} 
			
		} else {
			color = Color.YELLOW;
		}
		
		GameViewExt.addPoints(game, 50, color, ant.getVisitedNodeIndices());
	}

	public static void drawPheromones(Game game, AntMaze maze, PheromoneType pht) {
		if(Parameters.EXPERIMENTAL_MODE){
			return;
		}
		Color color = Color.WHITE;
		if (pht.equals(PheromoneType.EXPLORER)) {
			color = Color.YELLOW;
		} else if (pht.equals(PheromoneType.BLINKY)) {
			color = Color.RED;
		}  else if (pht.equals(PheromoneType.PINKY)) {
			color = Color.PINK;
		}  else if (pht.equals(PheromoneType.INKY)) {
			color = Color.CYAN;
		}  else if (pht.equals(PheromoneType.SUE)) {
			color = Color.ORANGE;
		} 
		
		Set<AntEdge> edgesToDraw = maze.getEdgesWithPheromones(pht);
		
		// get maxPheromones
		double maxPheromone = 0;
		double minPheromone = Double.MAX_VALUE;
		for(AntEdge e : edgesToDraw) {
			double pheromones = e.getPheromone(pht);
			if(pheromones > maxPheromone) {
				maxPheromone = pheromones;
			}	
			if(pheromones < minPheromone) {
				minPheromone = pheromones;
			}	
		}		
		
		// draw pheromones
		if (pht.equals(PheromoneType.EXPLORER) || pht.equals(PheromoneType.BLINKY)) {
			for(AntEdge e : edgesToDraw) {
				double pheromones = e.getPheromone(pht);
				int colorStrength = 1;
				if (pht.equals(PheromoneType.EXPLORER)) {
					colorStrength = new Double((((pheromones - minPheromone) * (50 - 10)) / (maxPheromone - minPheromone)) + 10).intValue();
				} else {
					colorStrength = new Double((((pheromones - minPheromone) * (220 - 10)) / (maxPheromone - minPheromone)) + 10).intValue();
				}

				GameViewExt.addPoints(game, colorStrength, color, e.getNodeA().getNodeIndex(), e.getNodeB().getNodeIndex());				
			}	
		}
		
	}

}
