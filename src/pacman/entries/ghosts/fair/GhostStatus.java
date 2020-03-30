package pacman.entries.ghosts.fair;

import pacman.game.Constants.GHOST;

/**
 * The GhostStatus documents the curent status of one ghost, including its distance to Ms. Pac-Man.
 * 
 * @author Iris Hunkeler
 *
 */
public class GhostStatus implements Comparable<GhostStatus> {
	
	private GHOST ghost;
	
	private int distanceToMsPacMan;
	
	private boolean canChoseWay;
	
	public GhostStatus(GHOST ghost, int distanceToMsPacMan, boolean canChoseWay) {
		this.ghost = ghost;
		this.distanceToMsPacMan = distanceToMsPacMan;
		this.canChoseWay = canChoseWay;				
	}

	public GHOST getGhost() {
		return ghost;
	}

	public void setGhost(GHOST ghost) {
		this.ghost = ghost;
	}

	public int getDistanceToMsPacMan() {
		return distanceToMsPacMan;
	}

	public void setDistanceToMsPacMan(int distanceToMsPacMan) {
		this.distanceToMsPacMan = distanceToMsPacMan;
	}

	public boolean isCanChoseWay() {
		return canChoseWay;
	}

	public void setCanChoseWay(boolean canChoseWay) {
		this.canChoseWay = canChoseWay;
	}
	
	
	public int compareTo(GhostStatus gs) {
		
		if(this.getDistanceToMsPacMan() < gs.distanceToMsPacMan) {
			return -1;
		} else if(this.getDistanceToMsPacMan() > gs.distanceToMsPacMan) {
			return 1;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("GhostStatus[");
		sb.append("ghost:").append(ghost).append(";");
		sb.append("distanceToMsPacMan").append(distanceToMsPacMan).append(";");
		sb.append("canChoseWay").append(canChoseWay).append("");
		sb.append("]\n");
		
		return sb.toString();
	}

}
