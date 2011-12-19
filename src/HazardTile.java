//==============================================================================
// Date Created:		18 December 2011
// Last Updated:		18 December 2011
//
// File Name:			HazardTile.java
// File Author:			M Matthew Hydock
//
// File Description:	An abstract class to facilitate the creation of hazards.
//						Hazards inflict pain on the hero sprite, and don't
//						normally ask to be removed from the tilemap.
//==============================================================================

public abstract class HazardTile extends AnimatedTile
{
	protected int damage;				// How much damage this hazard will inflict.
	
	public HazardTile(GameImageStrip i, GameAnimation a, int d)
	// Create an animated tile that can inflict damage.
	{
		super(i,a);
		
		damage = d;
		
		// Need to be able to stand within the tile to cause damage.
		isCollidable = false.
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

	public void passiveAction(ClydeSprite s)
	// Something to do if a sprite encounters this tile.
	{
		s.takeDamage(damage);
	}
