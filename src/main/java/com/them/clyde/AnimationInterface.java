package com.them.clyde;
//==============================================================================
// Date Created:		24 November 2011
// Last Updated:		19 December 2011
//
// File Name:			AnimationInterface.java
// File Author:			M Matthew Hydock
//
// File description:	An interface that allows for things to be animated,
//						by providing frame access and manipulation.
//==============================================================================

import java.awt.image.*;

public interface AnimationInterface
{	
	public void setCurrentFrame(int c);
	
	public int getCurrentFrame();
	
	public int getNumberFrames();
	
	public int getFrameWidth();
	
	public int getFrameHeight();
	
	public BufferedImage getFrameSubImage();
}
