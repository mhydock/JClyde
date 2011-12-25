//==============================================================================
// Date Created:		25 November 2011
// Last Updated:		24 December 2011
//
// File Name:			GameAnimation.java
// File Author:			M Matthew Hydock
//
// File description:	Controls the animation of an image with multiple frames.
//						Heavily modified from Andrew Davison's ImagesPlayer
//						class. Supports three playback modes, and the animation
//						can be played backwards.
//==============================================================================

/*------------------------------------------------------------------------------
	GameAnimation is designed to allow animation of any object that implements
	the AnimatedInterface interface. With respect to the code this was based on,
	this means images of type 'n' (GameImageSequence), 's' (GameImageStrip), 'g'
	(GameImageGroup), plus the new type, 'd' (GameImageGrid). All of these types
	are loaded directly into the animation, instead of being accessed through an
	intermediary, as they had been.

	Unlike the original code, the new GameAnimation's constructor requires the
	AnimatedInterface object that the user wants to animate, and the duration of
	each frame, whereas ImagesPlayer wanted the total length of the animation
	and scaled the time per frame based on how many frames were in the image.
	This would have caused problems if the underlying image object swapped
	between sequences of varying length (such as in a group), as the animation
	would be sped up or slowed down to accomodate the new number of frames. The
	way it is done now expects such problems.

	The current animation time is calculated when updateTick() is called, which
	should be during every update cycle. If it is not time to update, then the
	call will be ignored. The animation time is used to calculate the current
	frame, which is passed back to the encapsulated GameImage/AnimatedInterface
	object.

	A GameAnimation can be set to run once, repeat (basic cycle), ping-pong
	(reverse direction upon hitting the beginning or end), stop, resume, or
	restart	at a given image position. Additionally, it can be made to play the
	animation regularly, or sporatically, to make environments feel more random.
	It can also be told to arbitrarily change the direction of the animation
	(forward advance or backward advance).
------------------------------------------------------------------------------*/

import java.awt.image.*;

public class GameAnimation implements AnimationInterface, Cloneable
{
	public static enum Mode {ONCE, REPEAT, PINGPONG};
	
	// The default chance the animation will be run, if in sporadic mode.
	private static final double DEFAULT_FREQ = .1;	
	
	// Default time per frame, in milliseconds.
	private static final int DEFAULT_FRAME_TIME_MILLIS = 100;

	private Mode anim_mode;					// What playback mode the animation is in.
	private boolean playbackStopped;		// Is the animation currently stopped?
	private boolean playbackReversed;		// Is the animation running in reverse?
	private boolean playbackSporadic;		// Is the animation being run sporadically?
	private double playbackFrequency;		// How often the animation should be played
											// while in sporadic mode.
		
	private AnimationInterface anim_image;	// Animatable image to control.

	private long frameDuration;				// Length of time to display the current frame (in nanosecs).
	private long seqDuration;				// Total duration of the entire image sequence (in nanosecs).
	private long animTotalTime;				// Total accumulated time. Used to determine current frame.
	
	private long prevTime;					// Last time recorded.
	private long currTime;					// Current time.
	private long timeDiff;					// Difference between current time and last time.
	
	private int curr_frame;					// Current frame of the animation.

//==============================================================================
// Constructor.
//==============================================================================	
	public GameAnimation(AnimationInterface ai, int frame_time, Mode mode, boolean isRev, boolean isSpo)
	// Constructor for the heavily modified ImagePlayer, now refered to as
	// GameAnimation. Big differences are that it asks directly for a GameImage
	// (in the form of an AnimatedInterface), the duration of a frame (instead
	// of the sequence), and modes are now represented as an enum, and supports
	// more than looping/not looping. Playback can also be done in reverse, or
	// the animation can be perfomed sporadically (it runs to completion, but it
	// starts randomly).
	{
		anim_mode		= mode;
		anim_image		= ai;
		
		// Set the duration of a frame (given in ms, recorded as ns). Also
		// refreshes the data, setting the sequence duration and turning on the
		// animation.
		setFrameDuration(frame_time);
		animTotalTime	= 0L;

		prevTime = System.nanoTime();

		playbackReversed	= isRev;
		playbackSporadic	= isSpo;
		playbackFrequency	= DEFAULT_FREQ;
	}
	
	public GameAnimation clone()
	// Completely duplicate the data in the object. The image is an object
	// though, so it is not duplicated, only its reference is.
	{
		GameAnimation temp = new GameAnimation(	anim_image,getFrameDuration(),anim_mode,
												playbackReversed,playbackSporadic);
		
		if (playbackSporadic)
			temp.setSporadicMode(playbackFrequency);
		
		return temp;
	}
//==============================================================================
	
	
//==============================================================================
// Getters and Setters.	If you want to get, set, or "know" something about this
// object, you can do it with these.
//==============================================================================
//------------------------------------------------------------------------------
// AnimationInterface methods.
//------------------------------------------------------------------------------
	public void setCurrentFrame(int c)
	// The interface requires this. Just a wrapper to restartAt().
	{
		restartAt(c);
		playbackStopped = true;
	}

	public int getCurrentFrame()
	// Returns the numerical value of the current frame.
	{
		return curr_frame;
	}

	public int getNumberFrames()
	// Return the max number of frames in the animatable image.
	{
		if (anim_image != null)
			return anim_image.getNumberFrames();
			
		return 0;
	}
	
	public int getFrameWidth()
	// Wrapper to access the internal image's frame width.
	{
		if (anim_image != null)
			return anim_image.getFrameWidth();
			
		return 0;
	}
	
	public int getFrameHeight()
	// Wrapper to access the internal image's frame height.
	{
		if (anim_image != null)
			return anim_image.getFrameHeight();
			
		return 0;
	}
	
	public BufferedImage getFrameSubImage()
	// Returns a BufferedImage of the current frame, relying on the enclosed
	// image to return/generate the appropriate image.
	{
		if (anim_image != null)
			return anim_image.getFrameSubImage(); 

		return null; 
	}
//------------------------------------------------------------------------------


//------------------------------------------------------------------------------
// Methods specific to GameAnimation.
//------------------------------------------------------------------------------
	public boolean isBroken()
	// Returns whether the animation actually has something to animate or not.
	{
		return (anim_image == null);
	}
	
	public void animateObject(AnimationInterface ai)
	// Change the object being animated, and recalculate its data.
	{
		anim_image = ai;
		
		refreshData();
	}
	
	public void setFrameDuration(int f)
	// Set the duration of a single frame, and recalculate internal variables.
	// If the given frame duration is 0 or smaller, then default to 100ms.
	{
		frameDuration = (f > 0)?f:DEFAULT_FRAME_TIME_MILLIS;
		frameDuration *= 1000000L;
		
		refreshData();
	}
	
	public int getFrameDuration()
	// Return the duration of a single frame.
	{
		return (int)(frameDuration/1000000L);
	}
	
	public long getSequenceDuration()
	// Get the amount of time for the full animation.
	{
		return seqDuration;
	}
	
	public boolean atSequenceEnd()
	// Has the animation reached the end, and is it not repeating?
	{
		if (anim_image != null)
			return ((curr_frame >= anim_image.getNumberFrames()-1) || 
					(playbackReversed && curr_frame <= 0)) && (anim_mode == Mode.ONCE);
		
		return true;
	}
	
	public void setAnimationMode(Mode m)
	// Set the animation mode, using an enumerated type.
	{
		anim_mode = m;
	}
	
	public Mode getAnimationMode()
	// Obtain the animation mode, as an enumerated type.
	{
		return anim_mode;
	}
	
	public boolean isStopped()
	// Returns whether the animation is playing or not.
	{
		return playbackStopped;
	}
	
	public boolean isReversed()
	// Is the playback reversed?
	{
		return playbackReversed;
	}
	
	public boolean isSporadic()
	// Has sporadic mode been activated?
	{
		return playbackSporadic;
	}
	
	public double getFrequency()
	// Return the percent chance that the animation will start during
	// Sporadic mode. If in Normal mode, this chance is 100%, or 1.
	{
		if (playbackSporadic)
			return playbackFrequency;
			
		return 1;
	}			
	
	public void refreshData()
	// Something important has changed, recalculate the animation's data.
	{
		// If the animatable object is null, create a broken animation.
		if (anim_image == null || anim_image.getNumberFrames() < 0)
		{
//			System.out.println("Animateable Game Image not provided");
			curr_frame = -1;
			seqDuration = 0;
			playbackStopped = true;
		}

		// Otherwise, initialize the animation's times and frame limits.
		else
		{
//			System.out.println("Animateable Game Image has been loaded.");
			curr_frame = 0;
			seqDuration = frameDuration*anim_image.getNumberFrames();
			playbackStopped = false;
		}
	}
//------------------------------------------------------------------------------
//==============================================================================


//==============================================================================
// Playback control methods.
//==============================================================================
	public void stop()
	// Future updateTick() calls are essentially ignored.
	{
		playbackStopped = true;
	}

	public void resume()
	// Start at previous image position
	{ 
		if (anim_image != null)
			playbackStopped = false;
	}
	
	public void animateForward()
	// Make playback move forward.
	{
		playbackReversed = false;
	}
	
	public void animateBackward()
	// Make playback move backward.
	{
		playbackReversed = true;
	}
	
	public void reversePlayback()
	// Switch the direction of animation playback.
	{
		playbackReversed = !playbackReversed;
	}
	
	public void setNormalMode()
	// Turns on normal playback mode.
	{
		playbackSporadic = false;
	}
	
	public void setSporadicMode(double f)
	// Turns on sporadic playback mode (animations start randomly).
	{
		if (f >= 1)
		// No different than normal mode.
		{
			setNormalMode();
			return;
		}
		
		if (f <= 0)
		// Playback will never happen, same as being stopped.
		{
			stop();
			return;
		}
		
		playbackFrequency = f;
		playbackSporadic = true;
	}
	
	public void restartAt(int start)
	// Start showing the images again, starting with frame 'start'. This
	// requires a resetting of the animation time as well.
	{
		if (anim_image != null)
		{
			if ((start < 0) || (start > anim_image.getNumberFrames()-1))
			{
				System.out.println("Out of range, starting at 0");
				start = 0;
			}

			curr_frame = start;
			
			// Calculate a suitable animation time.
			animTotalTime = (long) curr_frame * frameDuration;
			playbackStopped = false;
		}
	}
//==============================================================================


//==============================================================================
// The updateTick method. This is what makes image animation possible.
//==============================================================================
	public void updateTick()
	// I MAKE NO ASSUMPTIONS. This class keeps track of time by itself, and
	// doesn't expect a higher level to manage its time.
	//
	// There are two ways the playback can be managed, either in a normal mode,
	// where playback is linear and smooth, or sporadic, which will play the
	// animation as expected, but start it at random times. These are performed
	// in private methods, seen below.
	//
	// Also, the numFrames provided by the AnimatedInterface is largely 
	// ignored; instead, the getNumberFrames() method of its enclosed image is
	// used, in case the image is a complex sequence with animations of varying 
	// lengths.
	//
	// A separate curr_frame from its animated image is maintained, to allow for
	// multiple animations on the same image. Since the current frame is
	// calculated entirely off of time, there is no need to know the frame that
	// the image thinks it should be on.
	{
		if (playbackSporadic && playbackStopped)
		// If in sporadic mode, try to start the animation.
		{
			if (playbackStopped = Math.random() < playbackFrequency)
			// If the random number is within the frequency range, reset the
			// total time (to start the animation at 0) and decrease the
			// previous recorded time by the show period, to ensure the
			// animation begins immediately.
			{
				animTotalTime = 0;
				prevTime -= frameDuration;
			}
		}
		
		// Calculate the change in time since this method was last called.
		currTime = System.nanoTime();
		timeDiff = currTime-prevTime;
		
		// If it hasn't been long enough, return.
		if (timeDiff < frameDuration)
			return;
		
		if (!playbackStopped)
		{
			// Update total animation time.
			prevTime = currTime;
			animTotalTime += timeDiff;

//			System.out.println("Total time: " + animTotalTime + "  Frame duration: " + frameDuration);

			// Calculate current frame. May be outside range (fixed further down).
			curr_frame = (int)(animTotalTime/frameDuration);
			
//			System.out.println("current frame: " + curr_frame);
			
			// End of animation has been reached, what do?
			if	(curr_frame > anim_image.getNumberFrames()-1 ||
				(playbackReversed && ((anim_image.getNumberFrames()-1)-curr_frame) < 0))
			{
//				System.out.println("End of animation, fix current frame");
				
				if (playbackSporadic)
				// Deal with animation ending while in sporadic mode.
					sporadicPlayback();
				else
				// Deal with animation ending while in normal mode.
					normalPlayback();
			}
			
			// If playback is reversed, count from the end of the animation.
			if (playbackReversed)
				curr_frame = (anim_image.getNumberFrames()-1)-curr_frame;
				
			// This class is pretty meta. This makes sure the underlying image
			// knows what frame to display, if it's ever asked directly.
			anim_image.setCurrentFrame(curr_frame);
			
//			System.out.println("current frame: " + curr_frame);
		}
	}
	
	private void normalPlayback()
	// Update and manage the animation in a normal manner.
	{
		// If the animation is not on repeat or ping-pong, stop.
		if (anim_mode == Mode.ONCE)
		{
			playbackStopped = true;
			curr_frame = anim_image.getNumberFrames()-1;
		}
				
		// If the animation is on ping-pong, reverse direction.
		else if (anim_mode == Mode.PINGPONG)
		{
			reversePlayback();
					
			curr_frame %= anim_image.getNumberFrames();
		}
				
		// If the animation is on repeat, modulo the current frame.
		else if (anim_mode == Mode.REPEAT)
			curr_frame %= anim_image.getNumberFrames();
	}
	
	private void sporadicPlayback()
	// Update current frame in sporadic mode. This makes repeat appear to act
	// identical to once, while ping pong will have an odd delay before
	// repeating backwards.
	{				
		// If the animation is on ping-pong, reverse direction.
		if (anim_mode == Mode.PINGPONG)
			reversePlayback();
		
		// Reset the animation, and stop playback.		
		curr_frame = 0;
		playbackStopped = true;
	}
//==============================================================================
}
