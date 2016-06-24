package com.them.clyde;
//==============================================================================
// Date Created:		10 December 2011
// Last Updated:		20 December 2011
//
// File Name:			Sprite.java
// File Author:			M Matthew Hydock
//
// File Description:	A basic Sprite class that contains a GameImageStrip and
//						a GameAnimation object to animate it. Keeps track of the
//						Sprite's location and velocity, along with the offsets
//						to position it with respect to the camera.
//
//						Based on the Sprite class designed by Andrew Davison.
//==============================================================================

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;


public class Sprite extends GameLayer
{
	// Default step sizes (how far to move in each update).
	protected static final int XSTEP = 5;
	protected static final int YSTEP = 5;

	// Default duration (in milliseconds) for a frame in an animation.
	protected static final int FRAME_DUR = 500;
	
	// Default dimensions when there is no image.
	protected static final int SIZE = 12;   

	// Sprite map and animation player.
	protected GameImageStrip image;
	protected GameAnimation animator;

	// A sprite is updated and drawn only when it is active.
	protected boolean isActive = true;
	protected boolean isLooping = true;

	// Position and movement variables.
	protected double xPos, yPos;			// Global location of sprite.
	protected double dx, dy;				// Amount to move for each update.

	public Sprite(GameImageStrip i, int x, int y, Component p)
	// Create a bare-bones sprite with a position, a sprite map, and a parent
	// panel that draws it.
	{
		// Make a GameLayer set in the middleground.
		super(0,p);
		
		xPos = x;
		yPos = y;

		image = i;
		
		// Animate the given image strip, setting it to repeat in normal mode.
		animator = new GameAnimation(i,FRAME_DUR,GameAnimation.Mode.REPEAT,false,false);
		
		// Save a reference to the parent panel.
		parent = p;
		
		dx = XSTEP;
		dy = YSTEP;
	}

//==============================================================================
// Animation control.
//==============================================================================
	public void startLooping()
	// Tell the animator to start. If the animator is currently set to ONCE,
	// then restart the animation at 0.	
	{
		if (isLooping)
			return;
			
		if (image != null && !animator.isBroken())
		{
			if (animator.getAnimationMode() == GameAnimation.Mode.ONCE)
				animator.restartAt(0);
				
			animator.resume();
			isLooping = true;
		}
		else
			isLooping = false;
	}

	public void stopLooping()
	// Tell the animator to stop playing.
	{
		if (isLooping)
		{
			animator.stop();
			isLooping = false;
		}
	}
//==============================================================================


//==============================================================================
// Getters and Setters.
//==============================================================================
	public void setSpriteMap(GameImageStrip i)
	// Set the Sprite's frame map, and tell the animator that the object being
	// animated has changed.
	{
		image = i;
		animator.animateObject(image);
	}

	public GameImageStrip getSpriteMap()
	// Return the sprite's frame map.
	{
		return image;
	}
	
	public int getWidth()
	// Return the width of a frame in the GameImageStrip.
	{
		return image.getFrameWidth();
	}

	public int getHeight()
	// Return the height of a frame in the GameImageStrip.
	{
		return image.getFrameHeight();
	}

	public int getParentWidth()
	// Return the width of the parent panel.
	{
		return parent.getWidth();
	}

	public int getParentHeight()
	// Return the height of the parent panel.
	{
		return parent.getHeight();
	}

	public boolean isActive()
	// Returns whether the sprite is active or not.
	{
		return isActive;
	}

	public void setActive(boolean a)
	// Activate or deactivate the sprite.
	{
		isActive = a;
	}

	public void setPosition(double x, double y)
	// Manually set the x and y locations.
	{
		xPos = x;
		yPos = y;
	}

	public double getXPos()
	// Return the x position.
	{
		return xPos;
	}

	public double getYPos()
	// Return the y position.
	{
		return yPos;
	}

	public void setStep(double dx, double dy)
	// Set the step size.
	{
		this.dx = dx;
		this.dy = dy;
	}

	public double getXStep()
	// Return the step size in the x direction.
	{
		return dx;
	}

 	public double getYStep()
 	// Return the step size in the y direction.
	{
		return dy;
	}

	public void setOffsets(int x, int y)
	// Set the sprite's offsets, to shift it according to the camera.
	{
		xOffset = x;
		yOffset = y;
	}
	
	public int getXOffset()
	// Return the x offset of the sprite.
	{
		return xOffset;
	}
	
	public int getYOffset()
	// Return the y offset of the sprite.
	{
		return yOffset;
	}

	public Rectangle getMyRectangle()
	// Return a rectangle bounding box for the sprite.
	{
		return new Rectangle((int)xPos,(int)yPos,getWidth(),getHeight());
	}
//==============================================================================


//==============================================================================
// Methods to move or update the sprite.
//==============================================================================
	public void translate(double xDist, double yDist)
	// Shift the sprite's location by a certain amount.
	{
		xPos += xDist;
		yPos += yDist;
	}
  
	public void updateSprite()
	// Move the sprite.
	{
		if (isActive())
		{
			xPos += dx;
			yPos += dy;

			if (isLooping)
				animator.updateTick();	// Update the animation.
		}
	}
//==============================================================================


	public void drawSprite(Graphics g)
	// Draw the current frame of the sprite, at its global location, shifted by
	// a predetermined offset.
	{
		// If the sprite is not within the dimensions of the display, don't draw
		// the sprite.
		if ((xPos+xOffset) > parent.getWidth() || (xPos+xOffset+getWidth()) < 0)
			return;
		if ((yPos+yOffset) > parent.getHeight() || (yPos+yOffset+getHeight()) < 0)
			return;
			
		if (isActive())
		{
			if (image == null || image.isBroken())
			// The sprite has no image, so draw a yellow circle instead.
			{
				g.setColor(Color.yellow);
				g.fillOval((int)xPos+xOffset, (int)yPos+yOffset, SIZE, SIZE);
			}
			else
				image.draw(g,(int)xPos+xOffset,(int)yPos+yOffset);
		}
	}
}
