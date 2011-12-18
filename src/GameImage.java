//==============================================================================
// Date Created:		24 November 2011
// Last Updated:		15 December 2011
//
// File Name:			GameImage.java
// File Author:			M Matthew Hydock
//
// File description:	A wrapper for the BufferedImage class, to make it easier
//						to load and draw images. Designed to be extended.
//==============================================================================

import java.awt.*;
import java.imageio.*;

public class GameImage
{
	protected static GraphicsConfiguration gc;

	protected String name;
	protected BufferedImage image;
	protected int width;
	protected int height;
	
//==============================================================================
// Constructors.
//==============================================================================
	public GameImage()
	// Create a null GameImage.
	{
		if (gc == null)
			gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
			
		name	= "";
		image	= null;
		width	= -1;
		height	= -1;
	}
	
	public GameImage(String path)
	// Create a new BufferedImage, and record its dimensions.
	{
		if (gc == null)
			gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

		setImage(path);
	}
	
	public GameImage(BufferedImage i)
	// Wraps a given BufferedImage, and records its dimensions. The resulting
	// GameImage will have no name.
	{
		if (gc == null)
			gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

		setImage(i);
	}
//==============================================================================


//==============================================================================
// Getters and setters.
//==============================================================================
	public void setName(String n)
	// Set/Change the name of the GameImage.
	{
		name = n;
	}
	
	public String getName()
	// Return the name of the GameImage.
	{
		return name;
	}
	
	public int getWidth()
	// Return the width of the GameImage (width of the BufferedImage).
	{
		return width;
	}
	
	public int getHeight()
	// Return the height of the GameImage (height of the BufferedImage).
	{
		return height;
	}
//==============================================================================


//==============================================================================
// BufferedImage management.
//==============================================================================
	public void setImage(String path)
	// Given a path to an image, if it is valid, a new BufferedImage will be
	// made and its dimensions will be recorded. If the path is invalid, then
	// a null GameImage will be made.
	{
		image = null;
		
		try
		{
			// Attempt to load the image, and record its transparency.
			BufferedImage im = ImageIO.read(getClass().getResource(path));
			int transparency = im.getColorModel().getTransparency();
			
			// Create a new BufferedImage that is compatible with the display.
			image	= gc.createCompatibleImage(im.getWidth(),im.getHeight(),transparency);
			
			// Try to find the last directory separator.
			int i = path.lastInstanceOf("/");
			if (i == -1)
				i = path.lastInstanceOf("\\");
			if (i == -1)
				i = 0;
			
			// Save the name of the file.
			name	= path.substring(i,path.length());
			
			// Copy the contents of the loaded image into the GameImage's
			// BufferedImage.
			Graphics2D g2d = image.createGraphics();
			g2d.drawImage(im,0,0,null);
			g2d.dispose();
			
			// Save the dimensions of the loaded image.
			width = image.getWidth();
			height = image.getHeight();
		}
		catch(IOException e)
		// There was an error somewhere, nullify the GameImage's properties.
		{
			System.out.println("Load Image error for " + path":\n" + e);
			name = null;
			width = -1;
			height = -1;
		}
	}
	
	public void setImage(BufferedImage i)
	// Set the internal BufferedImage to this new BufferedImage, and change
	// the width and height to match. The old name will be erased.
	{
		name = null;
		image = i;
		
		if (image != null)
		{
			width = image.getWidth();
			height = image.getHeight();
		}
		else
		{
			width = 0;
			height = 0;
		}
	}
	
	public BufferedImage getImage()
	// Returns the underlying BufferedImage.
	{
		return image;
	}
	
	public boolean isBroken()
	// Checks whether the internal BufferedImage is null.
	{
		return image == null;
	}
//==============================================================================

	
	public void draw(Graphics2D g, int x, int y)
	// Draw the entire image at (x,y)
	{
		if (image != null)
			g.drawImage(image,x,y,null);
	}
	
	public void draw(Graphics2D g, int dx,  int dy, int sx, int sy, int w, int h)
	// Draw only a part of the image.
	// (dx,dy)	= destination coordinates
	// (sx,sy)	= internal image coordinates
	// (w,h)	= how much of the image to draw
	{
		if (image != null)
		{
			// Ensure it doesn't draw garbage, or stretch a slice to fill a
			// large space.
			w2 = Math.min(w, width-sx);
			y2 = Math.min(h, height-sy);
			
			g.drawImage(image,dx,dy,dx+w2,dy+h2,sx,sy,sx+w2,sy+h2,null);
		}
	}
}
