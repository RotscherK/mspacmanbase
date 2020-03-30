package pacman.entries.ghosts.fair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Timer;

//import org.apache.commons.lang3.time.StopWatch;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import pacman.controllers.Controller;
import pacman.entries.ghosts.fair.environment.AntMaze;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getActions() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.ghosts.mypackage)
 */
/**
 * ACO controlled ghosts based on the proposal from Recio et al.
 * 
 * @author Iris Hunkeler
 */
public class FairGhosts extends Controller<EnumMap<GHOST, MOVE>> {	

	//private static final Logger LOG = LogManager.getLogger(FairGhosts.class);
	
	private EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
	

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		//StopWatch s = new StopWatch();
		//s.start();
		
		long startTime = System.nanoTime();
		 		
		myMoves.clear();

		AntMaze maze = new AntMaze(game);
		
		// Run Explorer Ants
		ExplorerRunner eRunner = new ExplorerRunner(game, maze);
		maze = eRunner.runExplorerAnts();
		
		HunterRunner hRunner = new HunterRunner(game, maze);

		// Find Path for every ghost, ghosts closest to Ms. Pac-Man chose first
		List<GhostStatus> ghosts = new ArrayList<GhostStatus>();
		for (GHOST ghost : GHOST.values()) {	
			
			// skip ghosts that are in lair
			if(game.getGhostLairTime(ghost) > 0) {
				continue;
			}			
			
			boolean canChoseWay = false;
			if (game.doesGhostRequireAction(ghost)) {		
				canChoseWay = true;
			}
			
			int distance = new Double(game.getDistance(game.getGhostCurrentNodeIndex(ghost), game.getPacmanCurrentNodeIndex(), DM.PATH)).intValue();
			ghosts.add(new GhostStatus(ghost, distance, canChoseWay));	
		}
		
		// sort ghosts and calculate move for them based on their distance to Ms. Pac-Man
		Collections.sort(ghosts);
		for(GhostStatus ghostStatus : ghosts) {
			MOVE move = hRunner.getMoveForGhost(ghostStatus.getGhost());
			if(ghostStatus.isCanChoseWay()) {
				myMoves.put(ghostStatus.getGhost(), move);
			}
		}

		//s.stop();
		//long msUsed = s.getTime();
		long msUsed = (System.nanoTime() - startTime)/1000000;
		if(msUsed > 40) {		
//			LOG.fatal("ACOghosts exeeded the time limit: {} ", s.getTime());
		}
		
		return myMoves;
	
	}
}