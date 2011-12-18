//==============================================================================
// Date Created:		14 December 2011
// Last Updated:		17 December 2011
//
// File Name:			ClydePanel.java
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
//==============================================================================

import javax.swing.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.*;


public class ClydePanel extends JPanel implements Runnable, ImagesPlayerWatcher
{
//==============================================================================
// Constants and external variables.
//==============================================================================
	private static final int NUM_DELAYS_PER_YIELD = 16;
	// Number of frames with a delay of 0 ms before the animation thread yields
	// to other running threads.
     
	private static final int MAX_FRAME_SKIPS = 5;
	// Number of frames that can be skipped in any one animation loop,
	// i.e the games state is updated but not rendered

	// Files to be loaded for the test level.
	private static final String HELP_SCREEN = "../data/help.png";
	private static final String TILE_MAP = "../data/maps/testmap.txt";
	private static final String CLYDE = "../data/sprites/clyde.png";
	private static final String MOUNTAINS = "../data/ribbons/mountains.png";
	private static final String CLOUDS = "../data/ribbons/clouds.png";

	private long period;                		// Period between drawing, in nanosecs.

	private JFrame parent;						// The window that contains this panel.
//==============================================================================


//==============================================================================
// Internal objects and variables.
//==============================================================================
	private Thread animator;					// The thread that performs the animation.
	private volatile boolean running = false;	// Used to stop the animation thread.
	private volatile boolean isPaused = false;	// Used to pause the animation thread.

	private ClydeSprite clyde;					// The hero sprite.
	private ArrayList<Ribbon> ribbons;			// The scrolling backgrounds.
	private TileMap tilemap;					// The tilemap.

	// Used at game termination
	private volatile boolean gameOver = false;

	// For displaying messages
	private Font msgsFont;
	private FontMetrics metrics;

	// off-screen rendering
	private BufferStrategy bufferStrategy;
	private Graphics buffer;

	// to display the title/help screen
	private boolean showHelp;
	private GameImage helpIm;
//==============================================================================


//==============================================================================
// Game initialization.
//==============================================================================
	public ClydePanel(JFrame p, long period)
	// Create a JPanel to display and control Clyde's Adventure.
	{
		parent = p;
		this.period = period;

		setDoubleBuffered(false);
		setBackground(Color.white);
		setPreferredSize(parent.getDimensions());

		setBufferStrategy();

		// Make this panel receive key events.
		setFocusable(true);
		requestFocus();
		
		// Create/add a keylistener.
		addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				processKeysPressed(e);
			}
			
			public void keyReleased(KeyEvent e)
			{
				processKeysReleased(e);
			}
		});

		// Loaders.
     	mapLoader	= TileMapFactory.getInstanceOf();
     	imageLoader	= GameImageFactory.getInstanceOf();
     
		// Initialise the game entities
		tilemap = mapLoader.buildMap(TILE_MAP);
		clyde = new ClydeSprite(new GameImageGrid(CLYDE,4,3),tilemap,
								tilemap.getStartX()*tilemap.getTileSize(),
								tilemap.getStartY()*tilemap.getTileSize(),this);

		// Initialize the ribbons (needs special attention).
		ribbons = new ArrayList<Ribbon>();
		ribbons.add(new Ribbon(new GameImage(CLOUDS),2,true,false,this));
		ribbons.add(new Ribbon(new GameImage(MOUNTAINS),1,true,false,this));
		
		// Align the ribbons to the bottom of the tilemap.
		for (int i = 0; i < ribbons.size(); i++)
			ribbons.get(i).setPosition(0,tilemap.getMapHeight()-ribbons.get(i).getHeight());

		// Prepare/display title/help screen.
		helpIm = new GameImage(HELP_SCREEN);
    	showHelp = true;
    	isPaused = true;

		// Set up message font
		msgsFont = new Font("SansSerif", Font.BOLD, 24);
		metrics = this.getFontMetrics(msgsFont);
	}

	public void addNotify()
	// Wait for the JPanel to be added to the JFrame before starting.
	{
		super.addNotify();   // Creates the peer.
		startGame();         // Start the thread.
	}


	private void startGame()
	// Initialize and start the thread. 
	{ 
		if (animator == null || !running)
		{
			animator = new Thread(this);
			animator.start();
		}
	}
	
	private void setBufferStrategy()
	// Attempt to set the BufferStrategy (for double buffering).
	{
		try
		// Try to create a buffer strategy. Wait until it has been made.
		{
			EventQueue.invokeAndWait(new Runnable()
			{
				public void run()
				{
					createBufferStrategy(2);
				}
			});
		}
		catch (Exception e)
		// Whoops! Something happened and a buffer strategy couldn't be made.
		{
			System.out.println("Error while creating buffer strategy");
			System.exit(0);
		}
		
		try
		// Sleep to give time for the buffer strategy to be carried out.
		{
			Thread.sleep(500); // 0.5 sec
		}
		catch(InterruptedException ex){}
		
		bufferStrategy = getBufferStrategy();
	}

//==============================================================================

    
//==============================================================================
// Game life cycle methods, called by the JFrame's window listener methods.
//==============================================================================
	public void resumeGame()
	// called when the JFrame is activated / deiconified
	{
		if (!showHelp)    // CHANGED
			isPaused = false;  
	} 

	public void pauseGame()
	// called when the JFrame is deactivated / iconified
	{
		isPaused = true;
	} 

	public void stopGame() 
	// called when the JFrame is closing
	{
		running = false;
	}
//==============================================================================


//==============================================================================
// Panel control methods.
//==============================================================================
	public void run()
	// The frames of the animation are drawn inside the while loop.
	{
		// Initialize the timers and counters.
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;

		beforeTime = gameStartTime;

		running = true;

		while(running)
		// Updating and rendering loop.
		{
			gameUpdate();					// Update the game data.
			paintScreen();					// Render/Display the frame.

			afterTime	= System.nanoTime();
			timeDiff	= afterTime - beforeTime;
			sleepTime	= (period - timeDiff) - overSleepTime;  

			if (sleepTime > 0)
			// Some time left in this cycle, sleep for a bit.
			{
				try 
				{
					Thread.sleep(sleepTime/1000000L);  // nano -> ms
				}
				catch(InterruptedException ex){}

				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			}
			else
			// The frame took longer than desired to render. 
			{
				excess -= sleepTime;  // store excess time value
				overSleepTime = 0L;

				if (++noDelays >= NUM_DELAYS_PER_YIELD)
				{
					Thread.yield();   // give another thread a chance to run
					noDelays = 0;
				}
			}

			beforeTime = System.nanoTime();

			// If frame animation is taking too long, update the game state
			// without rendering it, to get the updates/sec nearer to the
			// required FPS.
			int skips = 0;
			while((excess > period) && (skips < MAX_FRAME_SKIPS))
			{
				excess -= period;
				gameUpdate();
				skips++;
			}
		}
		
		// I really don't like this here, as it doesn't feel thread safe, but in
		// the off chance the game is being run in full screen, the JFrame won't
		// have the capability to close itself...
		System.exit(0);
	}

	private void gameUpdate()
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
			int distanceToExit = Math.sqrt(Math.pow(x-tilemap.getExitX(),2)+Math.pow(y-tilemap.getExitY(),2));
			if (clyde.getHealth() == 0 || distanceToExit < 5)
				gameOver = true;
		}
	}
	
	private void generateOffsets()
	// Create and apply offsets, making the panel act as a sort of camera.
	{
		// Find the middle of the screen.
		int xOffset = getWidth()/2-clyde.getXPos();
		int yOffset = getHeight()/2-clyde.getYPos();
		
		// Try to shift the character and the environment to the middle. If the
		// offsets move the tilemap away from the edges, force the offsets to
		// align to the edges.
		if (xOffset > 0)
			xOffset = 0;
		else if (xOffset < -(tilemap.getMapWidth()-getWidth()))
			xOffset = -(tilemap.getMapWidth()-getWidth());
			
		if (yOffset > 0)
			yOffset = 0;
		else if (yOffset < -(tilemap.getMapHeight()-getHeight()))
			yOffset = -(tilemap.getMapWidth()-getWidth());
		
		// Apply the offsets to all of the visible game objects.	
		clyde.setOffsets(xOffset,yOffset);
		tilemap.setOffsets(xOffset,yOffset);
		for (int i = 0; i < ribbons.size(); i++)
			ribbons.get(i).setOffsets(xOffset,yOffset);
	}
//==============================================================================


//==============================================================================
// Drawing methods.
//==============================================================================
	private void paintScreen()
	// Use active rendering to draw to a back buffer and then place the buffer
	// on-screen.
	{ 
		try
		{
			buffer = bs.getDrawGraphics();
			gameRender(buffer);
			buffer.dispose();
			
			if (!bufferStrategy.contentsLost())
				bufferStrategy.show();
			else
				System.out.println("Contents Lost");

			// Sync the display on some systems.
			// (on Linux, this fixes event queue problems)
			Toolkit.getDefaultToolkit().sync();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			running = false;
		}
	}
	
	private void gameRender(Graphics g)
	// Render the game graphics.
	{
		// Draw a white background
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		// Draw the game elements; order is important.
		for (int i = 0; i < ribbons.size(); i++)
			ribbons.get(i).display(g);
		tilemap.display(g);
		clyde.drawSprite(g);

		drawStatus(g);

		if (gameOver && clyde.getHealth() > 0)
			victoryScreen(g);
		if (gameOver)
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
		g.fillRect(0,getHeight()-45,getWidth(),getHeight());
		g.setColor(Color.blue);
		g.fillRect(5,getHeight()-40,getWidth()-5,getHeight()-5);
  	
		g.setFont(msgsFont);
		g.setColor(Color.green);
		g.drawString("Gems: " + clyde.getGems() + "/" + tilemap.getNumGems(), 15, getHeight()-25);
		g.setColor(Color.gray);
		g.drawString("Health: " + clyde.getHeath(), 215, getHeight()-25);
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
// Player controls.
//==============================================================================
	private void processKeysPressed(KeyEvent e)
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
			else if (e.isAltDown())
				clyde.doMagic();
			else if (e.isControlDown())
				clyde.jump();
		}
	}
	
	private void processKeysReleased(KeyEvent e)
	// What to do when the player stops holding down action buttons.
	{
		if (!isPaused && !gameOver)
		{
			// move the sprite and ribbons based on the arrow key pressed
			if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT)
				clyde.stayStill();
			else if (keyCode == KeyEvent.VK_ALT)
				clyde.stopMagic();
			else if (keyCode == KeyEvent.VK_CONTROL)
				clyde.startFalling();
		}
	}
//==============================================================================
}
