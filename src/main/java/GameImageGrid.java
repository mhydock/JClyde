//==============================================================================
// Date Created:		24 November 2011
// Last Updated:		19 December 2011
//
// File Name:			GameImageGrid.java
// File Author:			M Matthew Hydock
//
// File description:	An extension of GameImageStrip, this class extends the
//						basic strip into a series of strips (a grid of frames),
//						contained in a single image.
//==============================================================================

import java.io.*;
import java.awt.*;
import java.awt.image.*;

public class GameImageGrid extends GameImageStrip
{
	private int numRows;	
	private int curr_row;
	
//==============================================================================
// Constructors.
//==============================================================================
	public GameImageGrid()
	// Create a null GameImageGrid. Making one of these is not advisable, as
	// much work must be done to make it usable.
	{
		super();
		
		numRows = -1;
		curr_row = -1;
		
		frameHeight = -1;
	}
	
	public GameImageGrid(String path, int rows, int columns)
	// Creates a new image grid, with rows and columns. Essentially an image
	// strip, but with rows in addition to frames.
	{
		super(path,columns);
		
		numRows = (rows > 1)?rows:1;
		curr_row = 0;
		
		frameHeight = height/numRows;
	}
	
	public GameImageGrid(BufferedImage i, int rows, int columns)
	// Creates a new image grid using a pregenerated BufferedImage.
	{
		super(i,columns);
		
		numRows = (rows > 1)?rows:1;
		curr_row = 0;
		
		frameHeight = height/numRows;
	}
	
	public GameImageGrid(GameImage i, int rows, int columns)
	// Creates a new image grid using a previously defined GameImage.
	{
		super(i,columns);
		
		numRows = (rows > 1)?rows:1;
		curr_row = 0;
		
		frameHeight = height/numRows;
	}
	
	public GameImageGrid(GameImageStrip s, int rows)
	// Copies the BufferedImage from the provided GameImageStrip, notes how many
	// frames it has, and creates a new GameImageGrid with the data.
	{
		super(s.getImage(),s.getNumberFrames());
		
		numRows = (rows > 1)?rows:1;
		curr_row = 0;
		
		frameHeight = height/numRows;
	}
//==============================================================================


//==============================================================================
// Row management methods. Frame managment is identical to GameImageStrip, so
// the methods don't need to be overridden, with the exception of getting a
// BufferedImage of the current frame.
//==============================================================================	
	public void setCurrentRow(int r)
	// Sets the current row. Will clamp the current row if the provided value is
	// outside the grid's range.
	{
		if (image != null && numRows != 0)
		{
			if (r < 0)
				curr_row = 0;
			else if (r > numRows-1)
				curr_row = numRows-1;
			else
				curr_row = r;
		}
	}
	
	public int getCurrentRow()
	// Return the number of the current row.
	{
		return curr_row;
	}
	
	public void setNumberRows(int r)
	{
		if (image != null)
		{
			numRows = (r > 1)?r:1;	
			refreshData();
		}
	}
	
	public int getNumberRows()
	// Return the total number of rows.
	{
		return numRows;
	}
	
	public BufferedImage getFrameSubImage()
	// Return a BufferedImage that represents the current frame.
	{
		return image.getSubimage(curr_frame*frameWidth,curr_row*frameHeight,frameWidth,frameHeight);
	}
//==============================================================================

	public void refreshData()
	// In case the underlying BufferedImage has been changed, recalculate the
	// dimensions of a frame. If the BufferedImage has been made null, the
	// GameImageGrid's properties will be set to impossible values.
	{
		if (image != null)
		{
			frameWidth		= width/numFrames;
			frameHeight	= height/numRows;
		}
		else
		{
			numFrames		= -1;
			curr_frame		= -1;
			frameWidth		= -1;
			frameHeight	= -1;
		}
	}
	
	public void draw (Graphics2D g, int x, int y)
	// Draw the current frame at location (x,y).
	{
		if (image != null)
			g.drawImage	(image,x,y,x+frameWidth,y+frameHeight,
						curr_frame*frameWidth,curr_row*frameHeight,
						(curr_frame+1)*frameWidth,(curr_row+1)*frameHeight,null);
	}
}
