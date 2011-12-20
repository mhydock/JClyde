//==============================================================================
// Date Created:		25 November 2011
// Last Updated:		19 December 2011
//
// File Name:			GameImageGroup.java
// File Author:			M Matthew Hydock
//
// File description:	An extension of GameImageStrip, this class deals with
//						groups of image strips (or grids). As an extension of
//						GameImageStrip, it technically supports animations, but
//						as the strips may not all be of the same length, caution
//						is advised when using this for animations.
//
//						None of this class's inherited animation variables are
//						actually used; instead, it returns the animation
//						variables of the current strip (or grid). A waste, but
//						it ensures safety and compatibility.
//
//						While this class can technically be simulated with the
//						GameImageSequence class, as it contains GameImages and
//						GameImageStrips are descendents of GameImage, this class
//						is safer, as it's designed with GameImageStrip (and its
//						descendents) in mind.
//==============================================================================

import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;

public class GameImageGroup extends GameImageStrip
{
	private ArrayList<GameImageStrip> strips;
	private int curr_strip;

//==============================================================================
// Constructors.
//==============================================================================	
	public GameImageGroup()
	// Creates and empty GameImageGroup, ready for GameImageStrips.
	{
		strips = new ArrayList<GameImageStrip>();
		
		curr_strip = 0;
	}
	
	public GameImageGroup(String path, int frames)
	// Creates the array of GameImageStrips, and creates and loads the first
	// entry.
	{
		GameImageStrip temp = new GameImageStrip(path,frames);
		
		strips = new ArrayList<GameImageStrip>();
		strips.add(temp);
		
		curr_strip = 0;
	}
	
	public GameImageGroup(GameImageStrip s)
	// Creates the array of GameImageStrips, and loads a premade GameImageStrip
	// (or GameImageStrip descendent) into it.
	{
		strips = new ArrayList<GameImageStrip>();
		strips.add(s);
		
		curr_strip = 0;
	}
	
	public GameImageGroup(String path, int rows, int columns)
	// Creates the array of GameImageStrips, and creates and loads a
	// GameImageGrid (a grid-based descendent of GameImageStrip) into it.
	{
		GameImageGrid temp = new GameImageGrid(path,rows,columns);
		
		strips = new ArrayList<GameImageStrip>();
		strips.add(temp);
		
		curr_strip = 0;
	}
	
	public GameImageGroup(ArrayList<GameImageStrip> a)
	// An array of GameImageStrips has already been provided, so just copy it.
	{
		strips = a;
		
		curr_strip = 0;
	}
//==============================================================================


//==============================================================================
// GameImageGroup strip management methods.
//==============================================================================
	public void setCurrentStrip(int s)
	// Set the current strip. Will clamp the current strip if the provided value
	// is outside the group's range.
	{
		if (strips != null && curr_strip < strips.size())
		{
			if (s < 0)
				curr_strip = 0;
			else if (s > strips.size()-1)
				curr_strip = strips.size()-1;
			else
				curr_strip = s;
		}
	}
	
	public int getCurrentStrip()
	// Return the numerical value of the current strip.
	{
		return curr_strip;
	}
	
	public int getNumberStrips()
	// Return the total number of strips.
	{
		if (strips != null)
			return strips.size();
	
		return 0;
	}
	
	public void addStrip(String path, int frames)
	// Create a new strip and add it to the group.
	{
		GameImageStrip temp = new GameImageStrip(path,frames);
		
		strips.add(temp);
	}
	
	public void addGrid(String path, int row, int columns)
	// Create a new grid and add it to the group
	{
		GameImageGrid temp = new GameImageGrid(path,row,columns);
		
		strips.add(temp);
	}
	
	public void addStrip(GameImageStrip i)
	// Add a precreated strip to the group. THIS IS THE PREFERRED METHOD.
	{
		strips.add(i);
	}
	
	public boolean removeFrame(int i)
	// Remove a frame from the sequence. Adjust the current frame as necessary.
	// Returns a boolean, to check if a frame was successfully removed or not.
	{
		// Index out of bounds, no frame removed.
		if (i < 0 || i > frames.size()-1)
			return false;
		
		// Index within bounds, frame removed.	
		frames.remove(i);
		
		// Make sure the current frame is still within bounds.
		if (curr_frame == i)
			if (curr_frame == frames.size())
				curr_frame--;
			
		return true;
	}
	
	public GameImageStrip getCurrentImageStrip()
	// Return the current GameImageStrip.
	{
		if (strips != null && curr_strip < strips.size())
			return strips.get(curr_strip);
	
		return null;
	}
//==============================================================================


//==============================================================================
// Modified frame methods, to work with a collection of GameImageStrips.
//==============================================================================
	public void setCurrentFrame(int c)
	// Set the current frame of the current strip. Makes sure it is within the
	// range of the current strip.
	{
		if (strips != null && curr_strip < strips.size())
		{
			GameImageStrip curr = strips.get(curr_strip);
			if (c < 0)
				curr.setCurrentFrame(0);
			else if (c > curr.getNumFrames()-1)
				curr.setCurrentFrame(curr.getNumFrames()-1);
			else
				curr.setCurrentFrame(c);
		}
	}
	
	public int getCurrentFrame()
	// Gets the current frame of the current image strip.
	{
		if (strips != null && curr_strip < strips.size())
			return strips.get(curr_strip).getCurrentFrame();
			
		return 0;
	}
	
	public int getNumberFrames()
	// Gets the number of frames of the current image strip.
	{
		if (strips != null && curr_strip < strips.size())
			return strips.get(curr_strip).getNumFrames();
			
		return 0;
	}
	
	public int getFrameWidth()
	// Gets the width of frames of the current image strip.
	{
		if (strips != null && curr_strip < strips.size())
			return strips.get(curr_strip).getFrameWidth();
			
		return 0;
	}
	
	public int getFrameHeight()
	// Gets the height of frames of the current image strip.
	{
		if (strips != null && curr_strip < strips.size())
			return strips.get(curr_strip).getFrameHeight();
			
		return 0;
	}
	
	public BufferedImage getFrameSubImage()
	// Return a BufferedImage that represents the current frame of the current
	// image strip.
	{
		if (strips != null && curr_strip < strips.size())
			return strips.get(curr_strip).getFrameSubImage();
			
		return null;
	}
//==============================================================================


//==============================================================================
// Methods to manage the underlying BufferedImage, modified to work on the
// current frame of the current GameImageStrip.
//==============================================================================
	public int getWidth()
	// Gets the width the underlying BufferedImage of the current image strip.
	{
		if (strips != null && curr_strip < strips.size())
			return strips.get(curr_strip).getWidth();
			
		return 0;
	}
	
	public int getHeight()
	// Gets the height the underlying BufferedImage of the current image strip.
	{
		if (strips != null && curr_strip < strips.size())
			return strips.get(curr_strip).getHeight();
			
		return 0;
	}
	
	public void setImage(String path)
	// Change the underlying image of the current image strip.
	{
		if (strips != null && strips.size() != 0)
			strips.get(curr_strip).setImage(path);
	}
	
	public void setImage(BufferedImage i)
	// Change the underlying image of the current image strip.
	{
		if (strips != null && strips.size() != 0)
			strips.get(curr_strip).setImage(i);
	}
		
	public BufferedImage getImage()
	// Like the GameImageSequence, this class is a little odd. As it is also a
	// collection, it does not have a single image that defines it. Instead,
	// this method returns the defining image of the current strip.
	{
		if (strips != null && strips.size() != 0)
			return strips.get(curr_strip).getImage();
			
		return null;
	}
//==============================================================================	
	
	public void draw(Graphics g, int x, int y)
	// Draw the current image strip.
	{
		if (strips != null && curr_strip < strips.size())
			strips.get(curr_strip).draw(g,x,y);
	}
}
