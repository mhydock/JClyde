//==============================================================================
// Date Created:		11 December 2011
// Last Updated:		14 December 2011
//
// File Name:			GameLayer.java
// File Authhor:		M Matthew Hydock
//
// File Description:	An abstract class that implements various functions of
//						a movable layer in a game. The movement rate is a
//						function of the depth of the layer (further == move
//						slower, nearer == move faster).
//==============================================================================

import javax.swing.*;

public abstract class GameLayer
{
	protected JPanel parent;			// JPanel that displays this layer.

	protected int xOffset;				// Camera offset; where to draw the layer.
	protected int yOffset;				// Camera offset; where to draw the layer.

	protected double depth;				// How far away from the camera this
										// layer is. 0 is camera level.
	protected double move_factor;		// How fast or slow the layer scrolls.
										// A function of the layer depth.
										
	public GameLayer(int d, JPanel p)
	// Create a generic game layer, with the depth of the layer, and the panel
	// responsible for displaying it.
	{
		parent = p;
		
		setDepth(d);
	}
	
	public double getDepth()
	// Get the depth of the layer.
	{
		return depth;
	}
	
	public void setDepth(double d)
	// Set the depth of the layer and recalculate the move_factor.
	{
		depth = d;
		move_factor = Math.pow(2.0,d)/Math.pow(3.0,d);
	}
	
	public double getMoveFactor()
	// Get the movement factor, a scalar that affects how fast or slow the layer
	// moves.
	{
		return move_factor;
	}
	
	public JPanel getParent()
	// Get the object responsible for displaying the layer.
	{
		return parent;
	}
	
	public void setParent(JPanel p)
	// Set the object responsible for displaying the layer.
	{
		parent = p;
	}
	
	public void setOffsets(int x, int y)
	// Set the layer offsets. They will be multiplied by the movement factor to
	// simulate real depth.
	{
		xOffset = (int)(x*move_factor);
		yOffset = (int)(y*move_factor);
	}
	
	public int getXOffset()
	// Return the x offset of this layer.
	{
		return xOffset;
	}
	
	public int getYOffset()
	// Return the y offset of this layer.
	{
		return yOffset;
	}
}
