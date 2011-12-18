//==============================================================================
// Date Created:		17 December 2011
// Last Updated:		17 December 2011
//
// File Name:			ClydesAdventure.java
// File Author:			M Matthew Hydock
//
// File Description:	JFrame to display and manage Clyde's Adventure,	a
//						sidescroller originally programmed for DOS.
//
//						The JFrame supports display in both windowed and full
//						screen exclusive mode. If the display doesn't support
//						full screen exclusive mode, it the frame will default to
//						windowed mode.
//
//						A gameplay description can be found in ClydePanel.java
//						Controls can be viewed on the help screen.
//
//						This class was largely based on the JumpingJack class,
//						written by Andrew Davison, ad@fivedots.coe.psu.ac.th
//==============================================================================

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class ClydesAdventure extends JFrame implements WindowListener
{
	private static int DEFAULT_FPS = 30;		// 40 is too fast! 
	private ClydePane cp;						// Panel to draw and control the game.

	public ClydesAdventure(long period, boolean windowed)
	{
		super("Clyde's Adventure");

		if (!windowed)
		{
			if (!gd.isFullScreenSupported())
			// If the display doesn't support full screen, display a warning,
			// and then start the game in windowed mode.
			{
				System.out.println("Full-screen exclusive mode not supported");
				initWindowed()
			}
			else
				initFullScreen();
		}
		else
			initWindowed();
		
		// Add the panel after the frame has been initialized, so it can get
		// the proper width and height values.
		Container c = getContentPane();			// Default BorderLayout used.
		cp = new ClydePanel(this, period);
		c.add(jp, "Center");
	}

	private void initWindowed()
	// Set up the frame for windowed mode.
	{
		addWindowListener(this);
		pack();
		setSize(640,480);
		setIgnoreRepaint(true);					// Turn off all paint events.
		setResizable(false);					// Prevent frame resizing.
		setVisible(true);
	}
	
	private void initFullScreen()
	// Set up the frame to be full screen.
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gd = ge.getDefaultScreenDevice();
		
		setUndecorated(true);					// No menu bar, borders, etc.
		setIgnoreRepaint(true);					// Turn off all paint events.
		setResizable(false);					// Prevent frame resizing.
		
		gd.setFullScreenWindow(this);			// Switch on full-screen exclusive mode
	}


//==============================================================================
// Window listener methods.
//==============================================================================
	public void windowActivated(WindowEvent e)
	// What to do when the window is made active (was previously unfocused, but
	// the user has clicked on it).
	{
		cp.resumeGame();
	}

	public void windowDeactivated(WindowEvent e)
	// What to do when the window is made inactive (had focus, but the user has
	// changed tasks).
	{
		cp.pauseGame();
	}

	public void windowDeiconified(WindowEvent e) 
	// What to do if the window is restored from being minimized.
	{
		cp.resumeGame();
	}

	public void windowIconified(WindowEvent e)
	// What to do if the window has been minimized.
	{
		cp.pauseGame();
	}

	public void windowClosing(WindowEvent e)
	// What to do when the window has been asked to close.
	{
		cp.stopGame();
	}

	// Here to complete the interface.
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
//==============================================================================


//==============================================================================
// Main method.
//==============================================================================
	public static void main(String args[])
	// Set the period (time per frame update) and whether the game should be in
	// windowed mode or full screen mode.
	{ 
		long period = (long)1000.0/DEFAULT_FPS;	// Time per frame, in msecs
		boolean isWindowed = !(args.length == 1 && args[0].equals("fullscreen"));
		
		period *= 1000000L;						// ms --> nanosecs 
		new ClydesAdventure(period, isWindowed);
	}
//==============================================================================
}
