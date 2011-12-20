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
	protected int num_frames;		// Number of frames in this strip.
	protected int curr_frame;		// Current frame of the strip.
	protected int frame_width;		// Width of a frame (image width/#frames)
	protected int frame_height;		// Height of a frame (image height).

//==============================================================================
// Constructors.
//==============================================================================
	public GameImageStrip()
	// Create a null GameImageStrip. Making one of these is not advisable, as
	// much work must be done to make it usable.
	{
		super();
		
		num_frames		= -1;
		curr_frame		= -1;
		frame_width		= -1;
		frame_height	= -1;
	}
	
	public GameImageStrip(String path, int frames)
	// Create an image strip, with a set number of frames.
	{
		super(path);
				
		num_frames		= (frames > 1)?frames:1;
		curr_frame		= 0;
		frame_width		= width/num_frames;
		frame_height	= height;
	}
	
	public GameImageStrip(BufferedImage i, int frames)
	// Create an image strip, using a pregenerated BufferedImage.
	{
		super(i);
		
		num_frames		= (frames > 1)?frames:1;
		curr_frame		= 0;
		frame_width		= width/num_frames;
		frame_height	= height;
	}
	
	public GameImageStrip(GameImage i, int frames)
	// Turns a GameImage into a GameImageStrip (does not transform the original
	// object, only duplicates its BufferedImage). 
	{
		super(i.getImage());
		
		num_frames		= (frames > 1)?frames:1;
		curr_frame		= 0;
		frame_width		= width/num_frames;
		frame_height	= height;
	}
//==============================================================================


//==============================================================================
// Frame management methods.
//==============================================================================
	public void setCurrentFrame(int c)
	// Set the current frame. Will clamp the current frame if the provided value
	// is outside the strip's range.
	{
		if (image != null && num_frames != 0)
		{
			if (c < 0)
				curr_frame = 0;
			else if (c > num_frames-1)
				curr_frame = num_frames-1;
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
			num_frames = (f > 1)?f:1;
			refreshData();
		}
	}
	
	public int getNumberFrames()
	// Returns the total number of frames.
	{
		return num_frames;
	}
	
	public int getFrameWidth()
	// Returns the width of a single frame.
	{
		return frame_width;
	}
	
	public int getFrameHeight()
	// Returns the height of a single frame. Kind of silly, as it's always the
	// height of the base image, but it's part of the AnimatedInterface, for
	// upwards compatibility.
	{
		return frame_height;
	}
	
	public BufferedImage getFrameSubImage()
	// Return a BufferedImage that represents the current frame.
	{
		return image.getSubimage(curr_frame*frame_width,0,frame_width,frame_height);
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
		if (image != null)
		{
			frame_width		= width/num_frames;
			frame_height	= height;
		}
		else
		{
			num_frames		= -1;
			curr_frame		= -1;
			frame_width		= -1;
			frame_height	= -1;
		}
	}
//==============================================================================


	public void draw (Graphics g, int x, int y)
	// Draw the current frame at location (x,y).
	{
		if (image != null)
			g.drawImage	(image,x,y,x+frame_width,y+height,
						curr_frame*frame_width,0,(curr_frame+1)*frame_width,height,null);
	}
}
