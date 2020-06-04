package pacman.entries.ghosts.fair.calcutil;


/**
 * The Parameters define the given variables needed for the ACO algorithm.
 *  
 * @author Iris Hunkeler
 */
public class Parameters {
	
	public static boolean EXPERIMENTAL_MODE = false;
		
	/** Minimum Pheromone Level f�r Edges. Wert zuf�llig gew�hlt */
	public static double MINIMUM_PHEROMONE_LEVEL = 1.758680921;
	
	
	
	/* HUNTER ANTS */

	/** Maximum Number of HunterAnts created (maxAnt ^h) */
	public static int MAX_HUNTER = 30; // original: 7
	
	/** Maximum distance a HunterAnt is allowed to travel before stopping   (maxDist ^h) */
	public static int MAX_DISTANCE_PER_HUNTER= 75;

	/** (q0 ^h) */
//	public final static double EXPLORATION_RATE_HUNTER = 0.16;

	/** (p ^h) */
//	public final static double EVAPORATION_RATE_HUNTER = 0.3;

	/** (alpha ^h) */
	public static double UPDATING_RATE_HUNTER = 0.01835368245;
	
	
	
	
	/* EXPLORER ANTS */
	
	/** Consider Powerpills on Path Tim & Roger **/
	public static boolean POWERPILLS_EXPLORER = true;
	
	/** Activate Distance Threshold to PowerPill Tim & Roger **/
	public static boolean POWERPILL_THRESHOLD = true;
	
	/** Distance Threshold to Powerpills Tim & Roger **/
	public static int DISTANCE_THRESHOLD_POWERPILL = 10;
	
	/**  Maximum Number of ExplorerAnts created (maxAnt ^e) */
	public static int MAX_EXPLORER = 25;
	
	/** Maximum distance a ExplorerAnt is allowed to travel before stopping (maxDist ^e) */
	public static int MAX_DISTANCE_PER_EXPLORER = 74; 

	/** (q0 ^e) */
	public static double EXPLORATION_RATE_EXPLORER = 0.3514364733;

	/** (p ^e) */
//	public final static double EVAPORATION_RATE_EXPLORER = 0.87;

	/** (alpha ^e) */
//	public final static double UPDATING_RATE_EXPLORER = 0.84;
	
}
