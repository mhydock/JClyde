//==============================================================================
// Date Created:		24 November 2011
// Last Updated:		26 November 2011
//
// File Name:			AnimatedInterface.java
// File Author:			M Matthew Hydock
//
// File description:	An interface that allows for things to be animated, by
//						providing frame access and manipulation.
//==============================================================================

public interface AnimatedInterface
{
	protected int num_frames;
	protected int curr_frame;
	protected int frame_width;
	protected int frame_height;
	
	public void setCurrentFrame(int c);
	
	public int getCurrentFrame();
	
	public int getNumberFrames();
	
	public int getFrameWidth();
	
	public int getFrameHeight();
	
	public BufferedImage getFrameSubImage();
}
