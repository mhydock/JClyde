//==============================================================================
// Date Created:		14 December 2011
// Last Updated:		16 December 2011
//
// File Name:			Ribbon.java
// File Author:			M Matthew Hydock
//
// File Description:	Creates a scrollable image background. An extension of
//						the GameLayer class, it scrolls faster or slower based
//						on its depth.
//==============================================================================

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;


public class Ribbon extends GameLayer
{
	private GameImage image;
	private boolean horz_repeat;
	private boolean vert_repeat;
	private boolean isVisible = true;
	
	private int xPos;
	private int yPos;
	
	public Ribbon(GameImage i, int d, boolean h, boolean v, JPanel p)
	// Create a "ribbon" (scrollable background layer) at depth d. May be
	// repeatable in the horizontal or vertical directions.
	{
		super(d,p);
		
		image = i;
		horz_repeat = h;
		vert_repeat = v;
		
		// Draw ribbon starting in upper left corner.
		xPos = 0;
		yPos = 0;
	}
	
	public void setPosition(int x, int y)
	// Set the origin of the ribbon (location of upper left corner).
	{
		xPos = x;
		yPos = y;
	}
	
	public int getXPos()
	// Get the x coordinate of the upper left corner.
	{
		return xPos;
	}
	
	public int getYPos()
	// Get the y coordinate of the upper left corner.
	{
		return yPos;
	}

	public int getWidth()
	// Return the width of the ribbon's image.
	{
		return image.getWidth();
	}
	
	public int getHeight()
	// Return the height of the ribbon's image.
	{
		return image.getHeight();
	}

	public void setOffsets(int x, int y)
	// Slightly different offset command. If the ribbon is repeating in the x
	// or y directions, modulo the offsets with the panel's dimensions.
	{
		super.setOffsets(x,y);
		
		if (horz_repeat)
			xOffset %= image.getWidth();
		if (vert_repeat)
			yOffset %= image.getHeight();
		
		// Did the offset move the image offscreen? (If horz_repeat and
		// vert_repeat are on, this should always be true).
		isVisible = (xOffset < parent.getWidth() && xOffset+image.getWidth() > 0) &&
					(yOffset < parent.getHeight() && yOffset+image.getHeight() > 0);
	}
	
	public void display(Graphics g)
	// Display the ribbon. If it is repeating in both directions, it needs to be
	// drawn at most 4 times. If it is only repeating in one direction, then it
	// needs to be drawn at most 2 times. Can probably be cleaned up so that
	// clipped regions aren't drawn, but for now, whatever.
	{
		// If the ribbon isn't visible, don't even try drawing it.
		if (!isVisible)
			return;
		
		// It's going to be on screen at least once...
		image.draw(g,xOffset,yOffset);
		
		if (horz_repeat)
		// If horizontal repeat is on, check if the ribbon needs to be drawn
		// again, along the x axis.
		{
			if (xOffset+image.getWidth() < parent.getWidth())
			// If the ribbon is too far left, draw it again to the right.
			{
				int xOffset2 = xOffset+image.getWidth();
				image.draw(g,xOffset2,yOffset);
			}
			else if (xOffset > 0)
			// If the ribbon is too far right, draw it again to the left.
			{
				int xOffset2 = xOffset-image.getWidth();
				image.draw(g,xOffset2,yOffset);
			}
		}
		
		if (vert_repeat)
		// If vertical repeat is on, check if the ribbon needs to be drawn
		// again, along the y axis.
		{
			if (yOffset+image.getHeight() < parent.getHeight())
			// If the ribbon is too far above, draw it again lower.
			{
				int yOffset2 = yOffset+image.getHeight();
				image.draw(g,xOffset,yOffset2);
			}
			else if (yOffset > 0)
			// If the ribbon is too far down, draw it again above.
			{
				int yOffset2 = yOffset-image.getHeight();
				image.draw(g,xOffset,yOffset2);
			}
		}
		
		if (horz_repeat && vert_repeat)
		// If both horizontal and vertical repeat are on, a corner somewhere
		// might need to be filled.
		{
			if (xOffset+image.getWidth() < parent.getWidth())
			// If the ribbon is too far left, draw it again to the right.
			{				
				if (yOffset+image.getHeight() < parent.getHeight())
				// If the ribbon is too far above, draw it again lower.
				{
					int xOffset2 = xOffset+image.getWidth();
					int yOffset2 = yOffset+image.getHeight();
					image.draw(g,xOffset2,yOffset2);
				}
				else if (yOffset > 0)
				// If the ribbon is too far down, draw it again above.
				{
					int xOffset2 = xOffset+image.getWidth();
					int yOffset2 = yOffset-image.getHeight();
					image.draw(g,xOffset2,yOffset2);
				}
			}
			
			else if (xOffset > 0)
			// If the ribbon is too far right, draw it again to the left.
			{
				if (yOffset+image.getHeight() < parent.getHeight())
				// If the ribbon is too far above, draw it again lower.
				{
					int xOffset2 = xOffset-image.getWidth();
					int yOffset2 = yOffset+image.getHeight();
					image.draw(g,xOffset2,yOffset2);
				}
				else if (yOffset > 0)
				// If the ribbon is too far down, draw it again above.
				{
					int xOffset2 = xOffset-image.getWidth();
					int yOffset2 = yOffset-image.getHeight();
					image.draw(g,xOffset2,yOffset2);
				}
			}
		}
	}
}
