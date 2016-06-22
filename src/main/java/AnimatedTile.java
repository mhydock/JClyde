//==============================================================================
// Date Created:		18 November 2011
// Last Updated:		24 December 2011
//
// File Name:			AnimatedTile.java
// File Author:			M Matthew Hydock
// 
// File Description:	A class that creates a tile that is animated. Parts of
//						this were originally in Tile, but were removed into this
//						for the sake of clarity.
//==============================================================================

import java.awt.*;

public class AnimatedTile extends Tile
{
	private GameImageStrip image;		// To override the Tile class's image (which is private).
	private GameAnimation animation;	// Object to animate this tile's image.
	
	public AnimatedTile(GameImageStrip i, GameAnimation a, boolean c)
	// Create tile that is animated. It may or may not be solid.
	{
		super(null,c);
		
		setImage(i);
		setAnimation(a);
	}

	public void setImage(GameImageStrip i)
	// Set the image used to draw the tile.
	{
		image = i;
	}

	public GameImage getImage()
	// Obtain the image used to draw the tile.
	{
		return image;
	}
	
	public void setAnimation(GameAnimation a)
	// Set the GameAnimation responsible for animating the image strip.
	{
		animation = a;
		
		if (animation != null)
			animation.animateObject(image);
	}
	
	public GameAnimation getAnimation()
	// Get the GameAnimation responsible for animating the image strip.
	{
		return animation;
	}

	public void update()
	// Update the animation.
	{
		if (animation != null)
			animation.updateTick();
	}
	
	public void draw(Graphics g, int x, int y)
	// If the tile has an image, draw it at location (x,y). Included to ensure
	// that the image drawn is the one contained in this class, and not in Tile.
	{
		if (image != null && !image.isBroken())
		{
			image.setCurrentFrame(animation.getCurrentFrame());
			image.draw(g,x,y);
		}
	}
}
