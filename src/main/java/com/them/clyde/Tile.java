package com.them.clyde;
//==============================================================================
// Date Created:		24 November 2011
// Last Updated:		19 December 2011
//
// File Name:			Tile.java
// File Author:			M Matthew Hydock
// 
// File Description:	Based off of the Brick class created by Andrew Davison;
//						represents a tile in a TileMap. Tiles are always square,
//						but their size is not a constant. Tile sizes are set
//						and managed in TileMaps, and thus are not necessary in
//						this class.
//==============================================================================

import java.awt.*;

public class Tile
{
	protected GameImage image;			// Image for the tile.
	
	protected boolean isCollidable;		// Whether the tile should be included in
										// collision calculations or not.

	protected boolean isRemoved;		// Is this tile scheduled for removal?

	public Tile(GameImage i, boolean c)
	// Creates a basic tile, that may or may not be collidable.
	{
		image	= i;
		
		isCollidable	= c;
		isRemoved		= false;
	}

//==============================================================================
// Getters and setters.
//==============================================================================		
	public void setImage(GameImage i)
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
	// For compatability with animated tiles, and descendents thereof.
	{
		System.out.println("Can't animate basic tile. Convert to AnimatedTile first.");
	}
	
	public GameAnimation getAnimation()
	// For compatability with animated tiles, and descendents thereof.
	{
		return null;
	}
	
	public boolean isCollidable()
	// Return whether the tile should be included in collision calculations or
	// not.
	{
		return isCollidable;
	}
	
	public void setCollidable(boolean c)
	// Set whether the tile should be included in collision calculations or not.
	{
		isCollidable = c;
	}
	
	public boolean isRemovalScheduled()
	// Whether this tile should be removed on the tilemap's next update.
	{
		return isRemoved;
	}
	
	public void scheduleRemoval()
	// Schedule this tile for removal from the tilemap.
	{
		isRemoved = true;
	}
//==============================================================================

	
//==============================================================================
// Empty methods. Included for compatability with descendent classes.
//==============================================================================
	public void passiveAction(Sprite s)
	// Something to do if a sprite encounters this tile.
	{
	}

	public void doAction()
	// What to do if the hero used the action button on this block.
	{
	}
	
	public void doMagic()
	// What to do if the hero used the magic button on this block.
	{
	}

	public void update()
	// If something in the tile is supposed to change, change it.
	{
	}
//==============================================================================

	public void draw(Graphics g, int x, int y)
	// If the tile has an image, draw it at location (x,y).
	{
		if (image != null && !image.isBroken())
			image.draw(g,x,y);
	}
}
