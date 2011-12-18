//==============================================================================
// Date Created:		24 November 2011
// Last Updated:		15 December 2011
//
// File Name:			Tile.java
// File Author:			M Matthew Hydock
// 
// File Description:	Based off of the Brick class created by Andrew Davison;
//						represents a tile in a TileMap. Tiles are always square,
//						but their size is not a constant.
//==============================================================================

import java.awt.*;

public class Tile
{
	protected GameImage image;			// Image for the tile.
	protected GameAnimation animator;	// For animating the tile.
	protected int side;					// Length of a side.
	
	protected boolean isObstacle;		// Whether the tile should be included in
										// collision calculations or not.
	protected boolean isItem;			// Whether the tile is an item or not. If it
										// is, it can be removed from the tilemap.
	protected boolean isHazard;			// Whether it's a hazard tile or not.
	protected int damage;				// How much damage this hazard will inflict.

	protected boolean isRemoved;		// Is this tile scheduled for removal?

	public Tile(GameImage i, int s)
	// Creates a basic tile with sides of length s. It is not a hazard, and it
	// inflicts no damage.
	{
		image	= i;
		side	= s;
		
		isObstacle	= true;
		isItem		= false;
		isHazard	= false;
		damage		= 0;
		
		isRemoved	= false;
	}
	
	public Tile(GameImage i, int s, int d)
	// Creates a basic tile with sides of length s. This tile is a hazard, and
	// will inflict damage d upon collision with the hero.
	{
		image	= i;
		side	= s;
		
		isObstacle	= false;
		isItem		= false;
		isHazard	= true;
		damage		= d;
		
		isRemoved = 0;
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

	public void setLengthOfSide(int s)
	// Set the size of the tile.
	{
		side = s;
	}
	
	public int getLengthOfSide()
	// Get the size of the tile.
	{
		return side;
	}

	public boolean isObstacle()
	// Return whether the tile should be included in collision calculations or
	// not.
	{
		return isObstacle;
	}
	
	public void setObstacle(boolean o)
	// Set whether the tile should be included in collision calculations or not.
	{
		isObstacle = o;
	}

	public boolean isHazard()
	// Return whether this tile can inflict damage or not.
	{
		return isHazard;
	}

	public void setHazard(boolean h)
	// Set whether this tile can inflict damage or not.
	{
		isHazard = h;
	}
	
	public boolean isItem()
	// Return whether this tile is an item or not.
	{
		return isItem;
	}
	
	public void setItem(boolean i)
	// Set whether this tile is an item or not.
	{
		isItem = i;
	}
	
	public void setDamage(int d)
	// Set how much damage the tile can inflict, if it is a hazard.
	{
		damage = d;
	}
	
	public int getDamage()
	// Return how much damage the tile can inflict, if it is a hazard.
	{
		return damage;
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
// Methods that affect sprites.
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
//==============================================================================


	public void draw(Graphics g, int x, int y)
	// If the tile has an image, draw it at location (x,y).
	{
		if (image != null && !image.isBroken())
			image.draw(g,x,y,0,0,side,side);
	}
}
