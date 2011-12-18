//==============================================================================
// Date Created:		24 November 2011
// Last Updated:		10 December 2011
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

public class GameImageGrid extends GameImageStrip
{
	private int num_rows;	
	private int curr_row;
	
//==============================================================================
// Constructors.
//==============================================================================
	public GameImageGrid()
	// Create a null GameImageGrid. Making one of these is not advisable, as
	// much work must be done to make it usable.
	{
		super();
		
		num_rows = -1;
		curr_row = -1;
		
		frame_height = -1;
	}
	
	public GameImageGrid(String path, int rows, int columns)
	// Creates a new image grid, with rows and columns. Essentially an image
	// strip, but with rows in addition to frames.
	{
		super(path,columns);
		
		num_rows = (rows > 1)?rows:1;
		curr_row = 0;
		
		frame_height = height/num_rows;
	}
	
	public GameImageGrid(BufferedImage i, int rows, int columns)
	// Creates a new image grid using a pregenerated BufferedImage.
	{
		super(i,columns);
		
		num_rows = (rows > 1)?rows:1;
		curr_row = 0;
		
		frame_height = height/num_rows;
	}
	
	public GameImageGrid(GameImage i, int rows, int columns)
	// Creates a new image grid using a previously defined GameImage.
	{
		super(i,columns);
		
		num_rows = (rows > 1)?rows:1;
		curr_row = 0;
		
		frame_height = height/num_rows;
	}
	
	public GameImageGrid(GameImageStrip s, int rows)
	// Copies the BufferedImage from the provided GameImageStrip, notes how many
	// frames it has, and creates a new GameImageGrid with the data.
	{
		super(s.getImage(),s.getNumberFrames());
		
		num_rows = (rows > 1)?rows:1;
		curr_row = 0;
		
		frame_height = height/num_rows;
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
		if (image != null && num_rows != 0)
		{
			if (c < 0)
				curr_row = 0;
			else if (c > num_rows-1)
				curr_row = num_rows-1;
			else
				curr_row = c;
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
			(rows > 1)?rows:1;	
			refreshData();
		}
	}
	
	public int getNumberRows()
	// Return the total number of rows.
	{
		return num_rows;
	}
	
	public BufferedImage getFrameSubImage()
	// Return a BufferedImage that represents the current frame.
	{
		return image.getSubimage(curr_frame*frame_width,curr_row*frame_height,frame_width,frame_height);
	}
//==============================================================================

	public void refreshData()
	// In case the underlying BufferedImage has been changed, recalculate the
	// dimensions of a frame. If the BufferedImage has been made null, the
	// GameImageGrid's properties will be set to impossible values.
	{
		if (image != null)
		{
			frame_width		= width/num_frames;
			frame_height	= height/num_rows;
		}
		else
		{
			num_frames		= -1;
			curr_frame		= -1;
			frame_width		= -1;
			frame_height	= -1;
		}
	}
	
	public void draw (Graphics2D g, int x, int y)
	// Draw the current frame at location (x,y).
	{
		if (image != null)
			g.drawImage	(image,x,y,x+frame_width,y+frame_height,
						curr_frame*frame_width,curr_row*frame_height,
						(curr_frame+1)*frame_width,(curr_row+1)*frame_height,null);
	}
}
