package com.them.clyde;
//==============================================================================
// Date Created:		24 November 2011
// Last Updated:		19 December 2011
//
// File Name:			GameImageStrip.java
// File Author:			M Matthew Hydock
//
// File description:	An extension of GameImage, this class deals with images
//						organized as a strip of subimages (simple animation).
//==============================================================================

import java.awt.*;
import java.awt.image.*;

public class GameImageStrip extends GameImage implements AnimationInterface
{
	protected int numFrames = 0;	// Number of frames in this strip.
	protected int curr_frame;		// Current frame of the strip.
	protected int frameWidth;		// Width of a frame (image width/#frames)
	protected int frameHeight;		// Height of a frame (image height).

//==============================================================================
// Constructors.
//==============================================================================
	public GameImageStrip()
	// Create a null GameImageStrip. Making one of these is not advisable, as
	// much work must be done to make it usable.
	{
		super();
		
		numFrames		= -1;
		curr_frame		= -1;
		frameWidth		= -1;
		frameHeight		= -1;
	}
	
	public GameImageStrip(String path, int frames)
	// Create an image strip, with a set number of frames.
	{
		super(path);
				
		numFrames		= (frames > 1)?frames:1;
		curr_frame		= 0;
		
		refreshData();
	}
	
	public GameImageStrip(BufferedImage i, int frames)
	// Create an image strip, using a pregenerated BufferedImage.
	{
		super(i);
		
		numFrames		= (frames > 1)?frames:1;
		curr_frame		= 0;
		
		refreshData();
	}
	
	public GameImageStrip(GameImage i, int frames)
	// Turns a GameImage into a GameImageStrip (does not transform the original
	// object, only duplicates its BufferedImage). 
	{
		super(i.getImage());
		
		numFrames		= (frames > 1)?frames:1;
		curr_frame		= 0;
		frameWidth		= width/numFrames;
		frameHeight		= height;
	}
//==============================================================================


//==============================================================================
// Frame management methods.
//==============================================================================
	public void setCurrentFrame(int c)
	// Set the current frame. Will clamp the current frame if the provided value
	// is outside the strip's range.
	{
		if (image != null && numFrames != 0)
		{
			if (c < 0)
				curr_frame = 0;
			else if (c > numFrames-1)
				curr_frame = numFrames-1;
			else
				curr_frame = c;
		}
	}
	
	public int getCurrentFrame()
	// Returns the numerical value of the current frame.
	{
		return curr_frame;
	}
	
	public void setNumberFrames(int f)
	// Set the number of frames in the strip. If the image is null, the number
	// of frames can't be changed (will always be 0).
	{
		if (image != null)
		{
			numFrames = (f > 1)?f:1;
			refreshData();
		}
	}
	
	public int getNumberFrames()
	// Returns the total number of frames.
	{
		return numFrames;
	}
	
	public int getFrameWidth()
	// Returns the width of a single frame.
	{
		return frameWidth;
	}
	
	public int getFrameHeight()
	// Returns the height of a single frame. Kind of silly, as it's always the
	// height of the base image, but it's part of the AnimatedInterface, for
	// upwards compatibility.
	{
		return frameHeight;
	}
	
	public BufferedImage getFrameSubImage()
	// Return a BufferedImage that represents the current frame.
	{
		return image.getSubimage(curr_frame*frameWidth,0,frameWidth,frameHeight);
	}
//==============================================================================


//==============================================================================
// Methods to manage the underlying BufferedImage.
//==============================================================================
	public void setImage(String path)
	// Create a new BufferedImage, and make it the new image strip. The number
	// of frames is not changed though, so it is advised that the replacement
	// have the same number of frames. Frame size will be recalculated though.
	{
		super.setImage(path);
		
		refreshData();
	}
	
	public void setImage(BufferedImage i)
	// Swap out the old image strip with a new BufferedImage. Again, the number
	// of frames is not changed, so the new image should have the same number of
	// frames, to preserve compatibility. Frame sizes will be recalculated.
	{
		super.setImage(i);
		
		refreshData();
	}		
	
	public void refreshData()
	// In case the underlying BufferedImage has been changed, recalculate the
	// dimensions of a frame. If the BufferedImage has been made null, the
	// GameImageStrip's properties will be set to impossible values.
	{
		if (image != null && numFrames != 0)
		{
			frameWidth	= width/numFrames;
			frameHeight	= height;
		}
		else
		{
			numFrames	= -1;
			curr_frame	= -1;
			frameWidth	= -1;
			frameHeight	= -1;
		}
	}
//==============================================================================


	public void draw (Graphics g, int x, int y)
	// Draw the current frame at location (x,y).
	{
		if (image != null)
			g.drawImage	(image,x,y,x+frameWidth,y+height,
						curr_frame*frameWidth,0,(curr_frame+1)*frameWidth,height,null);
	}
}
