package pacman.game;

import static pacman.game.Constants.EDIBLE_ALERT;
import static pacman.game.Constants.GV_HEIGHT;
import static pacman.game.Constants.GV_WIDTH;
import static pacman.game.Constants.MAG;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Vector;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/**
 * 
 * @author Fabian Schaer
 * 
 * WHAT THIS CLASS DOES:
 * It extends the basic "GameView" by the following functionalities:
 * - The game can be up-scaled to make the game look bigger.
 * - The "addPoints" and "addLines" methods support transparency (alpha: 0-255)
 *
 * PREPARATION:
 * Please make the following changes in the GameView.java class either manually, or by
 * applying the "setUpGameViewExt.patch" which can be found in the "game" package:
 * - line 26: remove 'final' => "public final class GameView extends JComponent".
 * - line 28: change to 'protected' => "private final Game game;"
 * - line 29: change to 'protected' => "private Images images;"
 * - line 30: change to 'protected' => "private MOVE lastPacManMove;"
 * - line 31: change to 'protected' => "private int time;"
 * - line 33: change to 'protected' => "private Graphics bufferGraphics;"
 * - line 38: change to 'protected' => "private static boolean isVisible=true;"
 * - line 108: change to 'protected' => "private void drawDebugInfo()"
 * - line 194: change to 'protected' => "private void drawMaze()"
 * - line 205: change to 'protected' => "private void drawPills()"
 * - line 219: change to 'protected' => "private void drawPowerPills()"
 * - line 233: change to 'protected' => "private void drawPacMan()"
 * - line 248: change to 'protected' => "private void drawGhosts()"
 * - line 279: change to 'protected' => "private void drawLives()"
 * - line 288: change to 'protected' => "private void drawGameInfo()"
 * - line 302: change to 'protected' => "private void drawGameOver()"
 * - line 364: change to 'protected' => "private static class DebugPointer"
 * - line 377: change to 'protected' => "private static class DebugLine"
 * 
 * HOW TO USE:
 * Please make the following changes in the Executor.java class for the "game-mode" method you want to run, e.g. "runGameTimed":
 * - Replace "gv=new GameView(game).showGame();" with e.g. "gv=new GameViewExt(game, 2).showGame();", for a scale-factor of 2.

 * To draw points, you then can simply use:
 * - "GameViewExt.addPoints(...);
 * - "GameViewExt.addLines(...); <-- currently buggy, somehow... :-(
 * 
 * To use the predefined colors from the different units, you can use the enum UNITS, e.g.:
 * - "UNITS.PINKY.color;"
 * - "UNITS.PACMAN.color;"
 * - etc.
 * 
 * That's it! Have fun! :-)
 *
 */
@SuppressWarnings("serial")
public final class GameViewExt extends GameView {

	private static int scaleFactor;
	private static int MAG_SCALE;
	private static Font scalableFont;
	public static Vector<DebugPointerExt> debugPointersExt = new Vector<DebugPointerExt>();
	public static Vector<DebugLineExt> debugLinesExt = new Vector<DebugLineExt>();

	/**
	 * A little helper enumeration with pre-defined (matching) colours for each ghost and ms-pacman.
	 */
	public enum UNITS {
		PACMAN(Color.YELLOW), BLINKY(Color.RED), INKY(Color.CYAN), PINKY(Color.PINK), SUE(Color.ORANGE);

		public final Color color;

		UNITS(Color color) {
			this.color = color;
		}
	}

	/**
	 * The new constructor class with an additional argument, the scaleFactor.
	 * This will scale the whole game and all objects in it.
	 * 
	 * @param game The reference to the Game itself
	 * @param scaleFactor The factor with which the game shall be scaled. Has to be larger than 0!
	 */
	@SuppressWarnings("static-access")
	public GameViewExt(Game game, int scaleFactor) {
		super(game);
		this.isVisible = super.isVisible;
		this.scaleFactor = scaleFactor;
		this.MAG_SCALE = MAG * scaleFactor;
		this.scalableFont = new Font("Arial", Font.PLAIN, scaleFactor * 12);
	}

	/**
	 * Adds a new point to be drawn with an additional argument (alpha) to support transparency
	 * 
	 * @param game The reference to the Game itself
	 * @param alpha The alpha level, used for changing the transparency
	 * @param color The Color with which the object shall be drawn
	 * @param nodeIndices All nodes on which a point shall be drawn
	 */
	public synchronized static void addPoints(Game game, int alpha, Color color, int... nodeIndices) {
		if (isVisible) {
			for (int nodeIndice : nodeIndices) {
				debugPointersExt.add(new DebugPointerExt(game.getNodeXCood(nodeIndice), game.getNodeYCood(nodeIndice), alpha, color));
			}
		}
	}

	/**
	 * Adds several new line to be drawn with an additional argument (alpha) to support transparency
	 * 
	 * @param game The reference to the Game itself
	 * @param alpha The alpha level, used for changing the transparency
	 * @param color The Color with which the object shall be drawn
	 * @param fromNnodeIndices The starting nodes for the line
	 * @param toNodeIndices The ending nodes for the line
	 */
	public synchronized static void addLines(Game game, int alpha, Color color, int[] fromNnodeIndices, int[] toNodeIndices) {
		if (isVisible) {
			for (int i = 0; i < fromNnodeIndices.length; i++) {
				debugLinesExt.add(new DebugLineExt(game.getNodeXCood(fromNnodeIndices[i]), game.getNodeYCood(fromNnodeIndices[i]), game
						.getNodeXCood(toNodeIndices[i]), game.getNodeYCood(toNodeIndices[i]), alpha, color));
			}
		}
	}

	/**
	 * Adds a new line to be drawn with an additional argument (alpha) to support transparency
	 * 
	 * @param game The reference to the Game itself
	 * @param alpha The alpha level, used for changing the transparency
	 * @param color The Color with which the object shall be drawn
	 * @param fromNnodeIndex The starting node for the line
	 * @param toNodeIndex The ending node for the line
	 */
	public synchronized static void addLines(Game game, int alpha, Color color, int fromNnodeIndex, int toNodeIndex) {
		if (isVisible) {
			debugLinesExt.add(new DebugLineExt(game.getNodeXCood(fromNnodeIndex), game.getNodeYCood(fromNnodeIndex), game.getNodeXCood(toNodeIndex), game
					.getNodeYCood(toNodeIndex), alpha, color));
		}
	}

	/**
	 * This will go through the list debugPoints and debugLines to be drawn. Once this happened, all
	 * entries are removed from the vector again.
	 */
	protected void drawDebugInfo() {
		for (int i = 0; i < debugPointersExt.size(); i++) {
			DebugPointerExt dp = debugPointersExt.get(i);
			Color col = new Color(dp.color.getRed(), dp.color.getGreen(), dp.color.getBlue(), dp.alpha);
			bufferGraphics.setColor(col);
			bufferGraphics.fillRect(dp.x * MAG_SCALE + 1, dp.y * MAG_SCALE + 5, 10 * scaleFactor, 10 * scaleFactor);
		}

		for (int i = 0; i < debugLinesExt.size(); i++) {
			DebugLineExt dl = debugLinesExt.get(i);
			Color col = new Color(dl.color.getRed(), dl.color.getGreen(), dl.color.getBlue());
			Graphics2D g2 = (Graphics2D) bufferGraphics;
			g2.setStroke(new BasicStroke(3 * scaleFactor));
			g2.setColor(col);
			g2.drawLine(dl.x1 * MAG_SCALE + 5, dl.y1 * MAG_SCALE + 10, dl.x2 * MAG_SCALE + 5, dl.y2 * MAG_SCALE + 10);
		}
		debugPointersExt.clear();
		debugLinesExt.clear();
	}

	/**
	 * Scales the image of the actual mase based on the scaleFactor
	 */
	protected void drawMaze() {
		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.fillRect(0, 0, GV_WIDTH * MAG_SCALE, GV_HEIGHT * MAG_SCALE + 20);

		bufferGraphics.drawImage(scaleImage(images.getMaze(game.getMazeIndex())), 2, 6, null);
	}

	/**
	 * Draws all regular pills on the game field by utilizing the scaleFactor
	 */
	protected void drawPills() {
		int[] pillIndices = game.getPillIndices();

		bufferGraphics.setColor(Color.white);

		for (int i = 0; i < pillIndices.length; i++) {
			if (game.isPillStillAvailable(i)) {
				bufferGraphics.fillOval(game.getNodeXCood(pillIndices[i]) * MAG_SCALE + (4 * scaleFactor), game.getNodeYCood(pillIndices[i]) * MAG_SCALE
						+ (8 * scaleFactor), 3 * scaleFactor, 3 * scaleFactor);
			}
		}
	}

	/**
	 * Draws all power pills on the game field by utilizing the scaleFactor
	 */
	protected void drawPowerPills() {
		int[] powerPillIndices = game.getPowerPillIndices();

		bufferGraphics.setColor(Color.white);

		for (int i = 0; i < powerPillIndices.length; i++) {
			if (game.isPowerPillStillAvailable(i)) {
				bufferGraphics.fillOval(game.getNodeXCood(powerPillIndices[i]) * MAG_SCALE + (1 * scaleFactor), game.getNodeYCood(powerPillIndices[i])
						* MAG_SCALE + (5 * scaleFactor), 8 * scaleFactor, 8 * scaleFactor);
			}
		}
	}

	/**
	 * Draws Ms PacMan on the game field by utilizing the scaleImage method
	 */
	protected void drawPacMan() {
		int pacLoc = game.getPacmanCurrentNodeIndex();

		MOVE tmpLastPacManMove = game.getPacmanLastMoveMade();

		if (tmpLastPacManMove != MOVE.NEUTRAL) {
			lastPacManMove = tmpLastPacManMove;
		}

		bufferGraphics.drawImage(scaleImage(images.getPacMan(lastPacManMove, time)), game.getNodeXCood(pacLoc) * MAG_SCALE - 1, game.getNodeYCood(pacLoc)
				* MAG_SCALE + 3, null);
	}

	/**
	 * Draws the ghosts on the game field by utilizing the scaleImage method
	 */
	protected void drawGhosts() {
		for (GHOST ghostType : GHOST.values()) {
			int currentNodeIndex = game.getGhostCurrentNodeIndex(ghostType);
			int nodeXCood = game.getNodeXCood(currentNodeIndex);
			int nodeYCood = game.getNodeYCood(currentNodeIndex);

			if (game.getGhostEdibleTime(ghostType) > 0) {
				// what is the second clause for????
				if (game.getGhostEdibleTime(ghostType) < EDIBLE_ALERT && ((time % 6) / 3) == 0) {
					bufferGraphics.drawImage(scaleImage(images.getEdibleGhost(true, time)), nodeXCood * MAG_SCALE - 1, nodeYCood * MAG_SCALE + 3, null);
				} else {
					bufferGraphics.drawImage(scaleImage(images.getEdibleGhost(false, time)), nodeXCood * MAG_SCALE - 1, nodeYCood * MAG_SCALE + 3, null);
				}
			} else {
				int index = ghostType.ordinal();

				if (game.getGhostLairTime(ghostType) > 0) {
					bufferGraphics.drawImage(scaleImage(images.getGhost(ghostType, game.getGhostLastMoveMade(ghostType), time)), nodeXCood * MAG_SCALE - 1
							+ (index * 5), nodeYCood * MAG_SCALE + 3, null);
				} else {
					bufferGraphics.drawImage(scaleImage(images.getGhost(ghostType, game.getGhostLastMoveMade(ghostType), time)), nodeXCood * MAG_SCALE - 1,
							nodeYCood * MAG_SCALE + 3, null);
				}
			}
		}
	}

	/**
	 * Draws the Ms PacMan lives by utilizing the scaling method
	 */
	protected void drawLives() {
		int verticalCorrection = (scaleFactor * 2);
		for (int i = 0; i < game.getPacmanNumberOfLivesRemaining() - 1; i++) {
			bufferGraphics
			.drawImage(scaleImage(images.getPacManForExtraLives()), 210 * scaleFactor - (30 * i * scaleFactor) / 2, (260 - verticalCorrection) * scaleFactor, null);
		}
	}

	/**
	 * Draws all the game information such as time, level and score with a font size
	 * depending on the scaleFactor 
	 */
	protected void drawGameInfo() {
		int verticalCorrection = (scaleFactor * 3);
		bufferGraphics.setFont(scalableFont);
		bufferGraphics.setColor(Color.WHITE);
		bufferGraphics.drawString("S: ", 4 * scaleFactor, (271 - verticalCorrection) * scaleFactor);
		bufferGraphics.drawString("" + game.getScore(), 16 * scaleFactor, (271 - verticalCorrection) * scaleFactor);
		bufferGraphics.drawString("L: ", 78 * scaleFactor, (271 - verticalCorrection) * scaleFactor);
		bufferGraphics.drawString("" + (game.getCurrentLevel() + 1), 90 * scaleFactor, (271 - verticalCorrection) * scaleFactor);
		bufferGraphics.drawString("T: ", 116 * scaleFactor, (271 - verticalCorrection) * scaleFactor);
		bufferGraphics.drawString("" + game.getCurrentLevelTime(), 129 * scaleFactor, (271 - verticalCorrection) * scaleFactor);
	}

	/**
	 * Draw the text "game over", but scaled
	 */
	protected void drawGameOver() {
		bufferGraphics.setFont(scalableFont);
		bufferGraphics.setColor(Color.WHITE);
		bufferGraphics.drawString("Game Over", 80 * scaleFactor, 150 * scaleFactor);
	}

	/**
	 * Overrides the frame-size method to take the scaling into account
	 */
	public Dimension getPreferredSize() {
		return new Dimension(GV_WIDTH * MAG_SCALE, GV_HEIGHT * MAG_SCALE + 20);
	}

	/**
	 * Extends the pre-defined DebugPointer with an additional attribute tu support transparency
	 */
	static class DebugPointerExt extends DebugPointer {
		public int alpha;

		public DebugPointerExt(int x, int y, int alpha, Color color) {
			super(x, y, color);
			this.alpha = alpha;
		}
	}

	/**
	 * Extends the pre-defined DebugLine with an additional attribute to support transparency
	 */
	static class DebugLineExt extends DebugLine {
		public int alpha;

		public DebugLineExt(int x1, int y1, int x2, int y2, int alpha, Color color) {
			super(x1, y1, y2, y2, color);
			this.alpha = alpha;
		}
	}


	/**
	 * This is a new function with the only purpose to up- or down scale a provided {BufferedImage}
	 * with a factorthat is provided in the Constructor of this class.
	 * 
	 * @param before The BufferedImage that shall be up- or down scaled
	 * @return The scaled BufferedImage
	 */
	private BufferedImage scaleImage(BufferedImage before) {
		int w = before.getWidth();
		int h = before.getHeight();
		BufferedImage after = new BufferedImage(w * scaleFactor, h * scaleFactor, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(scaleFactor, scaleFactor);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(before, after);
		return after;
	}
}
