//==============================================================================
// Date Created:		18 December 2011
// Last Updated:		20 December 2011
//
// File Name:			ItemTile.java
// File Author:			M Matthew Hydock
//
// File Description:	An abstract class to facilitate the creation of items.
//						A proper item will ask to be destroyed after contact
//						with the hero sprite.
//==============================================================================

public abstract class ItemTile extends AnimatedTile
{
	public ItemTile(GameImageStrip i, GameAnimation a)
	// Create an ItemTile, which is just a special AnimatedTile.
	{
		super(i,a,false);
	}
	
	public void passiveAction(Sprite s)
	// Something to do if a sprite encounters this tile.
	{
		scheduleRemoval();
	}
}
