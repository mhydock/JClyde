//==============================================================================
// Date Created:		14 December 2011
// Last Updated:		31 December 2011
//
// File Name:			JClyde.java
// File Author:			M Matthew Hydock
//
// File Description:	The drawing surface/input manager for Clyde's Adventure,
//						a sidescroller originally programmed for DOS.
//
//						The goal of Clyde's Adventure is to guide Clyde through
//						a maze-like castle in his search for a hidden treasure,
//						and to collect gems along the way. Clyde's health is
//						depleted with every step, so there is a need to find the
//						treasure and reach the exit in the most efficient manner
//						possible. He must also avoid traps and hazards, and
//						solve numerous puzzles, to succeed in his quest.
//
//						This class is largely based on the JackPanel class
//						written by Andrew Davison, ad@fivedots.coe.psu.ac.th
//
//						It was partially merged with a modified JumpingJack
//						class, (also written by Andrew Davison), but all of the
//						generic game control code has been isolated and placed
//						into another class, which this class inherits from.
//==============================================================================

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class JClyde extends GameFrame implements KeyListener
{
//==============================================================================
// Constants and external variables.
//==============================================================================
	// Files to be loaded for the test level.
	private static final String HELP_SCREEN = "../data/help.png";
	private static final String TILE_MAP = "../data/maps/testmap.txt";
	private static final String CLYDE = "../data/sprites/clyde.png";
	private static final String MOUNTAINS = "../data/ribbons/mountains.png";
	private static final String CLOUDS = "../data/ribbons/clouds.png";
//==============================================================================


//==============================================================================
// Internal objects and variables.
//==============================================================================
	private ClydeSprite clyde;					// The hero sprite.
	private ArrayList<Ribbon> ribbons;			// The scrolling backgrounds.
	private TileMap tilemap;					// The tilemap.

	// For displaying messages
	private Font msgsFont;
	private FontMetrics metrics;

	// to display the title/help screen
	private boolean showHelp;
	private GameImage helpIm;
	
	// Loads the tilemap.
	private TileMapFactory mapLoader;
//==============================================================================


//==============================================================================
// Game initialization.
//==============================================================================
	public JClyde(int fps, boolean windowed)
	// Create a JPanel to display and control Clyde's Adventure.
	{
		super("Clyde's Adventure",fps,windowed);
		
		// Add this object a keylistener.
		addKeyListener(this);

		initGameObjects();
		
		startGame();
	}
		
	private void initGameObjects()
	// Initialize the sprite, tilemap, and background layers, along with game
	// state variables and font settings.
	{
		// Map loader.
     	mapLoader = TileMapFactory.getInstanceOf();
		mapLoader.setInputFile(TILE_MAP);
		mapLoader.setParent(this);
     
		// Initialize the game entities.
		tilemap = mapLoader.produceTileMap();
		System.out.println();
		clyde = new ClydeSprite(new GameImageGrid(CLYDE,4,3),tilemap,
								tilemap.getStartX()*tilemap.getTileSize(),
								tilemap.getStartY()*tilemap.getTileSize(),this);

//		System.out.println();

		// Initialize the ribbons (needs special attention).
//		ribbons = new ArrayList<Ribbon>();
//		ribbons.add(new Ribbon(new GameImage(CLOUDS),2,true,false,this));
//		ribbons.add(new Ribbon(new GameImage(MOUNTAINS),1,true,false,this));
		
		// Align the ribbons to the bottom left of the tilemap.
//		for (int i = 0; i < ribbons.size(); i++)
//			ribbons.get(i).setPosition(0,tilemap.getMapHeight()-ribbons.get(i).getHeight());

		generateOffsets();

		System.out.println();

		// Prepare/display title/help screen.
		helpIm = new GameImage(HELP_SCREEN);
    	showHelp = true;
    	isPaused = true;

		// Set up message font
		msgsFont = new Font("SansSerif", Font.BOLD, 24);
		metrics = this.getFontMetrics(msgsFont);
	}
//==============================================================================


//==============================================================================
// Panel control methods.
//==============================================================================
	public void gameUpdate()
	// Update game objects and adjust the viewport. 
	{ 
		if (!isPaused && !gameOver)
		{
			// Update the environment, and the hero sprite.
			tilemap.update();
			clyde.updateSprite();
			
			// Shift the view.
			generateOffsets();
			
			// Check to see if an end-game scenario has been reached.
			double xPos = clyde.getXPos()/tilemap.getTileSize();
			double yPos = clyde.getYPos()/tilemap.getTileSize();
			double distanceToExit = Math.sqrt(Math.pow(xPos-tilemap.getExitX(),2)+Math.pow(yPos-tilemap.getExitY(),2));
			System.out.println("Distance to exit (in tiles): " + distanceToExit);
			if (clyde.getHealth() == 0 || distanceToExit < 5)
				gameOver = true;
		}
	}
	
	private void generateOffsets()
	// Create and apply offsets, making the panel act as a sort of camera.
	{
		// Find the middle of the screen.
		int xOffset = (int)(getWidth()/2-(clyde.getXPos()+clyde.getWidth()/2));
		int yOffset = (int)(getHeight()/2-(clyde.getYPos()+clyde.getHeight()/2));
		
		// Try to shift the character and the environment to the middle. If the
		// offsets move the tilemap away from the edges, force the offsets to
		// align to the edges.
		if (xOffset > 0)
			xOffset = 0;
		else if (xOffset < -(tilemap.getMapWidth()-getWidth()-1))
			xOffset = -(tilemap.getMapWidth()-getWidth()-1);
			
		if (yOffset > 0)
			yOffset = 0;
		else if (yOffset < -(tilemap.getMapHeight()-getHeight()-1))
			yOffset = -(tilemap.getMapHeight()-getHeight()-1);
		
		// Apply the offsets to all of the visible game objects.	
		clyde.setOffsets(xOffset,yOffset);
		tilemap.setOffsets(xOffset,yOffset);
//		for (int i = 0; i < ribbons.size(); i++)
//			ribbons.get(i).setOffsets(xOffset,yOffset);
	}
//==============================================================================


//==============================================================================
// Drawing methods.
//==============================================================================	
	public void gameRender(Graphics g)
	// Render the game graphics.
	{
		// Draw a white background
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		// Draw the game elements; order is important.
//		for (int i = 0; i < ribbons.size(); i++)
//			ribbons.get(i).display(g);
		tilemap.display(g);
		clyde.drawSprite(g);

//		drawStatus(g);

		if (gameOver && clyde.getHealth() > 0)
			victoryScreen(g);
		if (gameOver && clyde.getHealth() == 0)
			gameOverScreen(g);
			
		if (showHelp)		// Draw the help at the very front (if switched on).
			helpIm.draw(g,	(getWidth()-helpIm.getWidth())/2, 
							(getHeight()-helpIm.getHeight())/2);		
	}

	private void drawStatus(Graphics g)
	// Status bar across the bottom of the screen. Displays the number of gems
	// collected out of how many are in the level, along with how much health
	// Clyde currently has.
	{
		g.setColor(Color.gray);
		g.fillRect(0,getHeight()-60-getInsets().bottom,getWidth(),getHeight());
		g.setColor(Color.blue);
		g.fillRect(	5+getInsets().left,getHeight()-55-getInsets().bottom,
					getWidth()-10-getInsets().right-getInsets().left,50);
  	
		g.setFont(msgsFont);
		g.setColor(Color.green);
		g.drawString("Gems: " + clyde.getGems() + "/" + tilemap.getNumGems(), 15+getInsets().left, getHeight()-25-getInsets().bottom);
		g.setColor(Color.gray);
		g.drawString("Health: " + clyde.getHealth(), 215+getInsets().left, getHeight()-25-getInsets().bottom);
	}

	private void gameOverScreen(Graphics g)
	// Display the game over screen.
	{
		String msg = "Don't give up!";

		int x = (getWidth() - metrics.stringWidth(msg))/2; 
		int y = (getHeight() - metrics.getHeight())/2;
		
		g.setColor(Color.black);
		g.fillRect(0,0,getWidth(),getHeight());
		g.setColor(Color.yellow);
		g.setFont(msgsFont);
		g.drawString(msg, x, y);
	}

	private void victoryScreen(Graphics g)
	// Display the victory screen.
	{
		String msg = "Great Job!";

		int x = (getWidth() - metrics.stringWidth(msg))/2; 
		int y = (getHeight() - metrics.getHeight())/2;
		
		g.setColor(Color.black);
		g.fillRect(0,0,getWidth(),getHeight());
		g.setColor(Color.yellow);
		g.setFont(msgsFont);
		g.drawString(msg, x, y);
	}
//==============================================================================


//==============================================================================
// KeyListener methods (Player controls)
//==============================================================================
	public void keyPressed(KeyEvent e)
	// handles termination, help, and game-play keys
	{
		int keyCode = e.getKeyCode();

		// termination keys
		// listen for esc, q, end, ctrl-c on the canvas to
		// allow a convenient exit from the full screen configuration
		if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
			(keyCode == KeyEvent.VK_END) || ((keyCode == KeyEvent.VK_C) && e.isControlDown()))
			running = false;

		// help controls
		if (keyCode == KeyEvent.VK_H)
		{
			if (showHelp)
			{  // help being shown
				showHelp = false;  // switch off
				isPaused = false;
			}
			else
			{  // help not being shown
				showHelp = true;    // show it
				isPaused = true;    // isPaused may already be true
			}
		}

		// game-play keys
		if (!isPaused && !gameOver)
		{
			// move the sprite and ribbons based on the arrow key pressed
			if (keyCode == KeyEvent.VK_LEFT)
				clyde.moveLeft();
			else if (keyCode == KeyEvent.VK_RIGHT)
				clyde.moveRight();
			else if (keyCode == KeyEvent.VK_UP)
				clyde.doAction();
				
			if (e.isAltDown())
				clyde.doMagic();
			if (keyCode == KeyEvent.VK_CONTROL)
				clyde.jump();
		}
	}
	
	public void keyReleased(KeyEvent e)
	// What to do when the player stops holding down action buttons.
	{
		int keyCode = e.getKeyCode();
		
		if (!isPaused && !gameOver)
		{
			// move the sprite and ribbons based on the arrow key pressed
			if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT)
				clyde.stayStill();
			else if (keyCode == KeyEvent.VK_ALT)
				clyde.stopMagic();
//			else if (keyCode == KeyEvent.VK_CONTROL)
//				clyde.startFalling();
		}
	}
	
	public void keyTyped(KeyEvent e) {}
//==============================================================================


//==============================================================================
// Main method.
//==============================================================================
	public static void main(String args[])
	// Set the period (time per frame update) and whether the game should be in
	// windowed mode or full screen mode.
	{ 
		boolean isWindowed = !(args.length == 1 && args[0].equals("fullscreen"));
		
		new JClyde(30, isWindowed);
	}
//==============================================================================
}
