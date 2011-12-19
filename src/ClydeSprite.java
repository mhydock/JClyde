//==============================================================================
// Date Created:		14 December 2011
// Last Updated:		18 December 2011
//
// File Name:			ClydeSprite.java
// File Author:			M Matthew Hydock
//
// File Description:	Hero sprite for the Clyde's Adventure sidescroller.
//						Clyde can move left or right, jump, activate objects,
//						and use a magic wand (which has varying effects on the
//						environment). Supports basic physics and collision
//						detection. A higher class should monitor his health,
//						and end the game if it reaches 0.
//==============================================================================

import java.awt.*;


public class ClydeSprite extends Sprite
{
	// The following are used in physics calulculations, to generate the next
	// position. Velocities are measured in meters per second. The results will
	// be scaled, so as to fit reasonably within the game's resolution.
	private static final double PIXELS_PER_METER = 64;
	private static final double GRAVITY = -9.8;
	private static final double HORZ_VELOCITY = 100;
	private static final double VERT_VELOCITY = 200;
	private static final double TERMINAL_VELOCITY = -1500;
	
	// Static health definitions. Falling damage is incurred when landing at
	// terminal velocity.
	private static final int FALLING_DAMAGE = 10;
	private static final int DEFAULT_HEALTH = 100;
	private static final int MAX_HEALTH = 500;
		
	// Sprite states.
	private boolean isStill;
	private boolean isFalling;
	private boolean isRising;
	private boolean isSitting;
	private boolean isFacingRight;
	private boolean hasWandOut;

	// How long has Clyde been in the air. Used to calculate vertical velocity.
	private long startTime;
	private double timeAirborn;

	// The TileMap that Clyde is interacting with.
	private TileMap tilemap;
	
	// Variables pertaining to Clyde's progress through the level.
	private int gemsCollected;
	private int currHealth;
	private boolean hasTreasure;

	public ClydeSprite(GameImageGrid i, TileMap t, int x, int y, JPanel p)
	// Make a ClydeSprite, a sprite with an associated tilemap, different
	// states, and some basic physics rules.	
	{
		super(x,y,i,p);
		
		tilemap = t;
		
		timeAirborn = 0;
		
		isStill = true;
		isFalling = false;
		isFacingRight = true;
		hasWandOut = false;
		
		animator = new GameAnimation(i,FRAME_DUR,GameAnimation.Mode.PINGPONG,false);
		resetLevel();
	}

//==============================================================================
// Getters for level stats.
//==============================================================================
	public int getHealth()
	{
		return currHealth;
	}
	
	public int getGems()
	{
		return gemsCollected;
	}
	
	public boolean hasTreasure();
	{
		return hasTreasure;
	}
//==============================================================================


//==============================================================================
// Methods relating to movement.
//==============================================================================
	public void moveLeft()
	// Start the animation (if it wasn't already) and move in a negative
	// horizontal direction.
	{
		startLooping();
		
		dx = -HORZ_VELOCITY;
		isFacingRight = false;
		isSitting = false;
		isStill = false;
	}

	public void moveRight()
	// Start the animation (if it wasn't already) and move in a positive
	// horizontal direction.
	{
		startLooping();
		
		dx = HORZ_VELOCITY()
		isFacingRight = true;
		isSitting = false;
		isStill = false;
	}

	public void stayStill()
	// Stop moving left or right. If not falling, stop animation.
	{
		if (!isFalling)
		{
			stopLooping();
			image.setCurrentFrame(1);
		}
			
		dx = 0;
		isStill = true;
	}


	public void jump()
	// If not falling, make Clyde rise up until he's run out of vertical thrust.
	{
		if (!isFalling)
		{
			if (!isRising)
			{
				isRising = true;
				startTime = System.nanoTime();
				image.setCurrentFrame(0);
			}
	
			timeAirborn = (System.nanoTime() - startTime)/100000000L;
			dy = GRAVITY*2*timeAirborn + VERT_VELOCITY;
			
			if (dy == 0)
				image.setCurrentFrame(1);
			
			if (dy < 0)
			// Change in y is negative, switch to falling mode.
			{
				image.setCurrentFrame(2);
				startFalling();
			}
		}
	}
	
	public void startFalling()
	// Trigger to tell Clyde to start falling. Needed when no longer jumping,
	// but hasn't hit the limit.
	{
		if (!isFalling)
		{
			dy = 0;
			isFalling = true;
			isRising = false;
		}
	}
	
	public void fall()
	// Make Clyde move in a negative vertical direction. Essentially the same
	// as jump, just without the initial vertical thrust. The speed of Clyde's
	// fall is capped at TERMINAL_VELOCITY, defined above.
	{
		if (isFalling)
		{
			if (dy == 0)
			{
				stopLooping();
				startTime = System.nanoTime();
			}
				
			timeAirborn = (System.nanoTime() - startTime)/100000000L;
			dy = Math.max(TERMINAL_VELOCITY, GRAVITY*2*timeAirborn);
		}
	}
//==============================================================================


//==============================================================================
// Actions and effects.
//==============================================================================
	public void resetLevel()
	// Reset Clyde's level progress variables.
	{
		gemsCollected = 0;
		currHealth = DEFAULT_HEALTH;
		hasTreasure = false;
	}
	
	public void collectTreasure()
	// Clyde has obtained the hidden treasure.
	{
		hasTreasure = true;
	}
	
	public void collectGem()
	// Increase the number of gems collected.
	{
		gemsCollected++;
	}
	
	public void takeDamage(int d)
	// Inflict damage upon Clyde. If d is negative, has a healing affect.
	{
		currHealth -= d;
		
		if (currHealth > MAX_HEALTH)
			currHealth = MAX_HEALTH;
		if (currHealth < 0)
			currHealth = 0;
	}

	public void doAction()
	// Perform a simple action on a nearby object (flip switch/read note/etc).
	{
		int x = xPos/tilemap.getTileSize();
		int y = yPos/tilemap.getTileSize();
		
		Tile t = tilemap.getTileAt(x*tilemap.getTileSize(),y*tilemap.getTileSize());
		
		if (t != null)
			t.doAction();
		
		y++;
		
		t = tilemap.getTileAt(x*tilemap.getTileSize(),y*tilemap.getTileSize());
		
		if (t != null)
			t.doAction();
	}

	public void doMagic()
	// Use Clyde's magic wand on nearby objects.
	{
		hasWandOut = true;
		
		int x = xPos/tilemap.getTileSize();
		int y = yPos/tilemap.getTileSize();
		
		if (isFacingRight)
		{
			// To get the tile in front of Clyde, at his feet.
			x++;
			y++;
			
			Tile t = tilemap.getTileAt(x*tilemap.getTileSize(),y*tilemap.getTileSize());
			
			if (t != null)
				t.doMagic();
			
			// To get the tile in front of Clyde, underneath his feet.
			y++;
			t = tilemap.getTileAt(x*tilemap.getTileSize(),y*tilemap.getTileSize());
			
			if (t != null)
				t.doMagic();
		}
		else
		{
			// To get the tile in front of Clyde, at his feet.
			x--;
			y++;
			
			Tile t = tilemap.getTileAt(x*tilemap.getTileSize(),y*tilemap.getTileSize());
			
			if (t != null)
				t.doMagic();
			
			// To get the tile in front of Clyde, underneath his feet.
			y++;
			t = tilemap.getTileAt(x*tilemap.getTileSize(),y*tilemap.getTileSize());
			
			if (t != null)
				t.doMagic();
		}
	}
	
	public void stopMagic()
	// Put wand away.
	{
		hasWandOut = false;
	}
//==============================================================================


//==============================================================================
// Collision detection and position updating.
//==============================================================================
	private boolean willHitTile(int x, int y)
	// Check whether a given point is inside an obstruction.
	{
		return tilemap.insideSolidTile(x,y);  
	}
	
	private boolean collisionCheck()
	// Check a number of points around Clyde to see if they are within solid
	// tiles. If so, shift Clyde's position until he isn't colliding.
	{		
		// Check a point in the middle of the bottom of the sprite to see if it
		// is over a solid tile. If not, Clyde is now falling. Not a collision,
		// but it does result in a change (though not immediate).
		if (!isFalling && !willHitTile(xPos+getWidth()/2,yPos+getHeight()))
		{
			isStill = false;
			isFalling = true;
			isSitting = false;
			
			return false;
		}
		
		// If Clyde is falling, but there is a tile beneath him, stop him. If he
		// was falling at terminal velocity, inflict falling damage.
		if (isFalling && willHitTile(xPos+getWidth()/2,yPos+getHeight()))
		{
			isStill = true;
			isFalling = false;
			
			if (dy == TERMINAL_VELOCITY)
			{
				takeDamage(FALLING_DAMAGE);
				isSitting = true;
			}
			
			dy = 0;
			yPos = (yPos/tilemap.getTileSize() - 1)*tileMap.getTileSize();
			
			return true;
		}
			
		// If there is a collision on one lower corner, but not the other, shift
		// Clyde a little to the side opposite the collision.
		if (willHitTile(xPos,yPos+getHeight()) && !willHitTile(xPos+getWidth(),yPos+getHeight()))
		{
			xPos = (xPos/tilemap.getTileSize() + 1)*tileMap.getTileSize();
			return true;
		}
		else if (!willHitTile(xPos,yPos+getHeight()) && willHitTile(xPos+getWidth(),yPos+getHeight()))
		{
			xPos = (xPos/tilemap.getTileSize())*tileMap.getTileSize();
			return true;
		}
		
		// If the middle of the top of the sprite is in a tile on this step,
		// and he is rising vertically, then set the vertical velocity to 0 and
		// align his head with the bottom of the tile.
		if (dy > 0 && !isFalling && willHitTile(xPos+getWidth()/2,yPos))
		{
			dy = 0;
			isFalling = true;
			
			yPos = (yPos/tilemap.getTileSize() + 1)*tileMap.getTileSize();
			return true;
		}
		
		// If the sprite hit a wall while moving laterally. Set horizontal
		// velocity to 0, and shift until not hitting a wall.
		if (willHitTile(xPos,yPos+(getHeight()/4)) || willHitTile(xPos,yPos+(getHeight()*3/4)))
		{
			dx = 0;
			xPos = (xPos/tilemap.getTileSize() + 1)*tileMap.getTileSize();
			return true;
		}
		else if (willHitTile(xPos+getWidth(),yPos+(getHeight()/4)) || 
				 willHitTile(xPos+getWidth(),yPos+(getHeight() * 3/4)))
		{
			dx = 0;
			xPos = (xPos/tilemap.getTileSize())*tileMap.getTileSize();
			return true;
		}
		
		return false;
	}

	public void updateSprite()
	{
		if (!isStill)
		// If the sprite is moving, update its position.
		{
			// The sprite is always falling, even when it's not.
			fall();
			
			int oldX = xPos;
			int oldY = yPos;
			
			dx /= PIXELS_PER_METER;
			dy /= -PIXELS_PER_METER;		// Inverting change in y, to match
											// flipped y axis of display.
		
		    super.updateSprite();			// Apply dx and dy and update the 
		
			// If collisionCheck() needs to be run more than 30 times, we got a
			// problem somewhere...
			int i;
			for (i = 0; i < 30 && collisionCheck(); i++);
			
			// Whatever. The new position was in an impossible location, so keep
			// to the old position.
			if (i == 30)
			{
				System.out.println("New position is impossible. Staying put.");
				xPos = oldX;
				yPos = oldY;
			}	
		}
		
		// Apply passive actions to upper half. If an item, remove it from the
		// tilemap.
		int x = xPos+getWidth()/2;
		int y = yPos+getHeight()/4;
		Tile t = tilemap.getTileAt(x,y);
		if (t != null && !t.isObstacle())
		{
			t.passiveAction(this);
			if (t.isItem())
				t.scheduleRemoval();
		}
			
		// Apply passive actions to lower half. If an item, remove it from the
		// tilemap.
		t = tilemap.getTileAt(x,y+1);
		if (t != null && !t.isObstacle())
		{
			t.passiveAction(this);
			if (t.isItem())
				t.scheduleRemoval();
		}
	}
//==============================================================================


//==============================================================================
// Appearance methods.
//==============================================================================
	private void setAnimation()
	// Set the current row of the image grid to the appropriate animation.
	{
		if (isRising || isFalling)
			((GameImageGrid)image).setCurrentRow(2);
		else if (hasWandOut)
			((GameImageGrid)image).setCurrentRow(1);
		else if (isSitting)
			((GameImageGrid)image).setCurrentRow(3);
		else
			((GameImageGrid)image).setCurrentRow(0);
	}
	
	public void drawSprite(Graphics g)
	// Draw the current frame of the sprite, at its global location, shifted by
	// a predetermined offset. Needed to duplicate this from Sprite, as the
	// class (and my entire chain of GameImage classes) don't have a native
	// method for flipping...
	{
		// If the sprite is not within the dimensions of the display, don't draw
		// the sprite.
		if ((xPos+xOffset) > parent.getWidth() || (xPos+xOffset+getWidth()) < 0)
			return;
		if ((yPos+yOffset) > parent.getHeight() || (yPos+yOffset+getHeight()) < 0)
			return;
		
		setAnimation();
		
		if (image == null || image.isBroken())
		// The sprite has no image, so draw a yellow circle instead.
		{
			g.setColor(Color.yellow);
			g.fillOval(locx+xOffset, locy+yOffset, SIZE, SIZE);
		}
		else
		{
			if (isFacingRight)
			// Sprite is facing right, draw normally.
				image.draw(g,xPos+xOffset,yPos+yOffset);
			else
			// Manually flip the sprite, since I didn't build a flip method
			// into ALL OF MY IMAGE CLASSES.
			{
				int x = xPos+xOffset;
				int y = xPos+yOffset;
				int w = image.frameWidth();
				int h = image.frameHeight();
				int r = ((GameImageGrid)image).getCurrentRow();
				int c = image.getCurrentFrame();
					
				g.draw(image.getImage(),x,y,x+w,y+h,
						(c+1)*w,(r+1)*h,c*w,r*h,null);
			}
		}
	}
}
