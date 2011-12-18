//==============================================================================
// Date Created:		25 November 2011
// Last Updated:		26 November 2011
//
// File Name:			GameImageSequence.java
// File Author:			M Matthew Hydock
//
// File description:	An extension of GameImageStrip, this class deals with a
//						collection of images with the same names and extensions,
//						but vary by a number, indicating their placement in a
//						sequence of images. Images are not required to be the
//						same dimensions, or even loaded in order.
//==============================================================================

import java.util.ArrayList;

public class GameImageSequence extends GameImageStrip
{
	private ArrayList<GameImage> frames;
	
//==============================================================================
// Constructors.
//==============================================================================
	public GameImageSequence()
	// Creates an empty GameImageSequence, ready for GameImages.
	{
		frames = new ArrayList<GameImage>();
	}
	
	public GameImageSequence(String path)
	// Creates the array of GameImages, and creates and loads the first entry.
	{
		GameImage temp = new GameImage(path);
		
		frames = new ArrayList<GameImage>();
		frames.add(temp);
	}
	
	public GameImageSequence(BufferedImage i)
	// Creates the array of GameImages, and creates and loads the first entry,
	// providing it with a BufferedImage instead of a string.
	{
		GameImage temp = new GameImage(i);
		
		frames = new ArrayList<GameImage>();
		frames.add(temp);
	}
	
	public GameImageSequence(GameImage i)
	// Creates the array of GameImages, and loads a premade GameImage into it.
	{
		frames = new ArrayList<GameImage>();
		frames.add(i);
	}
	
	public GameImageSequence(ArrayList<GameImage> a)
	// An array of GameImages has already been provided, so just copy it.
	{
		frames = a;
	}
//==============================================================================


//==============================================================================
// GameImageSequence frame management methods.
//==============================================================================
	public void addFrame(String path)
	// Create a new image and add it to the sequence.
	{
		GameImage temp = new GameImage(path);
		
		frames.add(temp);
	}
	
	public void addFrame(GameImage i)
	// Add a precreated image to the sequence.
	{
		frames.add(i);
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
	
	public GameImage getCurrentFrameImage()
	// Return the current GameImage.
	{
		if (frames != null && frames.size() != 0)
			return frames.get(curr_frame);
			
		return null;
	}
//==============================================================================


//==============================================================================
// GameImageStrip frame management methods.
//==============================================================================
	public void setCurrentFrame(int c)
	// Set the current frame. Will clamp the current frame if the provided value
	// is outside the sequence's range.
	{
		if (frames != null && frames.size() != 0)
		{
			if (c < 0)
				curr_frame = 0;
			else if (c > frames.size()-1)
				curr_frame = frames.size()-1;
			else
				curr_frame = c;
		}
	}
	
	public int getNumberFrames()
	// Return the number of frames.
	{
		if (frames != null)
			return frames.size();
			
		return 0;
	}
	
	public int getFrameWidth()
	// Return the width of the current frame.
	{
		if (frames != null && frames.size() != 0)
			return frames.get(curr_frame).getWidth();
			
		return 0;
	}
	
	public int getFrameHeight()
	// Return the height of the current frame.
	{
		if (frames != null && frames.size() != 0)
			return frames.get(curr_frame).getHeight();
			
		return 0;
	}
	
	public BufferedImage getFrameSubImage()
	// Return a BufferedImage that represents the current frame.
	{
		if (frames != null && frames.size() != 0)
			return frames.get(curr_frame).getImage();
			
		return null;
	}
//==============================================================================


//==============================================================================
// Methods to manage the underlying BufferedImage (of the current frame).
//==============================================================================
	public int getWidth()
	// Return the width of the current frame. (Modified for compatibility, since
	// GameImageSequence doesn't have a single representative image.)
	{
		return getFrameWidth();
	}
	
	public int getHeight()
	// Return the height of the current frame. (Modified for compatibility,
	// since GameImageSequence doesn't have a single representative image.)
	{
		return getFrameHeight();
	}
	
	public void setImage(String path)
	// Since this is a collection of GameImages, the setImage will only affect
	// the current frame.
	{
		if (frames != null && frames.size() != 0)
			frames.get(curr_frame).setImage(path);
	}
	
	public void setImage(BufferedImage i)
	// Since this is a collection of GameImages, the setImage will only affect
	// the current frame.
	{
		if (frames != null && frames.size() != 0)
			frames.get(curr_frame).setImage(i);
	}
	
	public BufferedImage getImage()
	// Since this is a collection of images, it doesn't have one image that
	// represents it, so instead, just the current frame will be returned.
	{
		return getFrameSubImage();
	}
//==============================================================================

	
	public void draw(Graphics2d g, int x, int y)
	{
		if (frames != null && frames.size() != 0)
			frames.get(curr_frame).draw(g,x,y);
	}
}
