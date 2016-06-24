package com.them.clyde;
//==============================================================================
// Date Created:		26 November 2011
// Last Updated:		19 December 2011
//
// File Name:			GameImageFactory.java
// File Author:			M Matthew Hydock
//
// File description:	A Singleton Factory designed to read in a file, and
//						spit out GameImage objects. It can produce simple
//						GameImages, GameImageStrips, GameImageGrids,
//						GameImageSequences, and GameImageGroups.
//
//						Additionally, if the generated GameImage supports
//						animation, and the line that generated it contains
//						animation information, it can be asked to generate a
//						companion GameAnimation object for it.
//==============================================================================

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;


public class GameImageFactory
{
	private String imageDir;
	private BufferedReader input;
	private boolean inputEnd;
	private String curr_line;
	
	private static GameImageFactory factory;

//==============================================================================
// Set up the GameImageFactory.
//==============================================================================
	private GameImageFactory()
	// Singleton constructor for the GameImageFactory.
	{		
		input = null;
		inputEnd = true;
		
		imageDir = null;
		curr_line = null;
		
		System.out.println("GameImageFactory initialized.");
	}
	
	public static GameImageFactory getInstanceOf()
	// Return an instance of the GameImageFactory, or make one if it doesn't
	// exist yet.
	{
		if (factory == null)
			factory = new GameImageFactory();
			
		return factory;
	}
	
	public void setInputFile(String path)
	// Sets the file to read from. If the path is wrong or there is no readable
	// file, then the whole system will quit.
	{
		imageDir = null;
		curr_line = null;
		
		System.out.println("Reading file: " + path);
		
		try
		{
			input = new BufferedReader(new FileReader(path));
			inputEnd = false;
			
			System.out.println("Images file " + path + " set up for reading.");
		}
		catch (Exception e) 
		{
			System.out.println("Error reading file: " + path);
			System.exit(1);
		}
	}
//==============================================================================


//==============================================================================
// High-level methods to parse input.
//==============================================================================	
	public GameImage produceGameImage()
	// Parses input until a GameImage is generated.
	// Skips blank lines and comment lines.
	{
		try
		{			
			if (curr_line == null)
			// The factory hasn't started to parse this file yet, so reset the
				while (((curr_line = input.readLine()) != null) && !curr_line.contains("BEGIN_IMAGES"));
			
			while ((curr_line = input.readLine()) != null)
			{				
				// Work on a duplicate of the current line.
				String line = new String(curr_line);				
			
				if (line.contains("dir"))
				// A path to the image files has been defined.
				{
					line = line.substring(3,line.length());
					
					if (line.indexOf("//") != -1)
					// Remove comments from the line.
						line = line.substring(0,line.indexOf("//"));
					
					line = line.trim();
					imageDir = line;
					
					System.out.println("Image directory set: " + imageDir);
					continue;
				}
			
				if (line.indexOf("//") != -1)
				// Remove comments from the line.
					line = line.substring(0,line.indexOf("//"));
					
				if (line.indexOf('|') != -1)
				// Remove animation info from the line.
					line = line.substring(0,line.indexOf('|'));
					
				line = line.trim();				// Remove white space.
				
				if (line.isEmpty())				// Blank line.
					continue;
				
				if (line.equals("END_IMAGES"))	// End of images block in file.
					break;
				
				System.out.println("\n" + line);
				
    	    	return parseLine(line);			// Trying to load image.
			}
		}
		catch (Exception e)
		{
			System.out.println("GameImageFactory interrupted: " + e.toString());
			e.printStackTrace();
			System.exit(1);
		}
			
		inputEnd = true;
		return null;
	}
	
	public GameImage parseLine(String line)
	// Parse a single line, returning a GameImage object. Separated from the
	// produceGameImage method because it could come in handy elsewhere.
	//
	// Recognizes the following formats:
	//
	//	o <fnm>							>> a single image
	//	s <fnm> <number>				>> a strip of images
	//	d <fnm> <rows> <columns>		>> a grid of images
	//	n <fnm*.ext> <number>			>> a numbered sequence of images
	//	g <name> <fnm> [ <fnm> ]*		>> a group of images
	//
	// Formats that support animations can have the following after the initial
	// image loading:
	//	<loading phrase> <anim mode> <time/frame> <playback mode> 
	//
	{
		char ch = Character.toLowerCase(line.charAt(0));

		switch (ch)
		{
			case 'o':	return getGameImage(line);
			case 's':	return getGameImageStrip(line);
			case 'd':	return getGameImageGrid(line);
//			case 'n':	return getGameImageSequence(line);
//			case 'g':	return getGameImageGroup(line);
			default	:	System.out.println("Do not recognize line: " + line);
		}
		
		return null;
	}		
//==============================================================================


//==============================================================================
// Factory status methods.
//==============================================================================
	public boolean atEnd()
	// Whether the parser has reached the end of the file or not.
	{
		return inputEnd;
	}
	
	public String getCurrLine()
	// Return the last parsed line.
	{
		return curr_line;
	}
//==============================================================================


//==============================================================================
// String manipulation methods.
//==============================================================================	
	public String getPrefix(String path)
	// Extract the path before the file extension.
	{
		int posn;
		if ((posn = path.lastIndexOf(".")) == -1)
		{
			System.out.println("No prefix found for filename: " + path);
			return path;
		}
		else
			return path.substring(0, posn);
	}
	
	public String getExtension(String path)
	// Extract the file extension, including the '.'.
	{
		int posn;
		if ((posn = path.lastIndexOf(".")) == -1)
		{
			System.out.println("No extension found for filename: " + path);
			return "";
		}
		else
			return path.substring(posn-1);
	}
//==============================================================================

	
//==============================================================================
// Animation methods.
//==============================================================================
	public boolean canBeAnimated()
	// Whether the image can be animated or not. If the image is a basic
	// GameImage or there isn't a pipe in the line, then it cannot be animated.
	{
		if (curr_line != null)
			return (curr_line.charAt(0) != 'o') && (curr_line.indexOf('|') != -1);
			
		return false;
	}
	
	public GameAnimation produceAnimation()
	// Try to build a GameAnimation using the current line.
	{
		// Work on a duplicate of the current line.
		String line = new String(curr_line);
			
		if (line.indexOf("//") != -1)
		// Remove comments from the line.
			line = line.substring(0,line.indexOf("//"));
			
		// Only keep what's after the pipe.
		line = line.substring(line.indexOf('|')+1, line.length());
				
		line = line.trim();				// Remove white space.
			
		if (line.isEmpty())				// Blank line.
			return null;
		
		StringTokenizer tokens = new StringTokenizer(line);
		
		if (tokens.countTokens() < 2)
		{
			System.out.println("Malformed animation expression: " + line);
			return null;
		}
		
//------------------------------------------------------------------------------
		String curr = tokens.nextToken();
		
		// Attempt to set the mode. Default is repeat.
		GameAnimation.Mode mode;
		if (curr.equals("o"))
			mode = GameAnimation.Mode.ONCE;
		else if(curr.equals("r"))
			mode = GameAnimation.Mode.REPEAT;
		else if(curr.equals("p"))
			mode = GameAnimation.Mode.PINGPONG;
		else
			mode = GameAnimation.Mode.REPEAT;
//------------------------------------------------------------------------------		
		// Attempt to record the time per frame. Default is -1 to invoke the
		// default known by GameAnimation.
		int frameDuration = -1;
		try
		{
			frameDuration = Integer.parseInt(tokens.nextToken());
		}
		catch(Exception e)
		// Next token wasn't an integer.
		{
			System.out.println("Incorrect formatting for " + line);
			System.out.println("Using default settings...");
		}
//------------------------------------------------------------------------------
		// Set whether the animation should be reversed and/or sporadic.
		boolean isReversed = false;
		boolean isSporadic = false;
		while (tokens.hasMoreTokens())
		{
			curr = tokens.nextToken();
			if (curr.equals("r"))
				isReversed = true;
				
			if (curr.equals("s"))
				isSporadic = true;
		}
		
		return new GameAnimation(null,frameDuration,mode,isReversed,isSporadic);
	}
//==============================================================================
	
	
//==============================================================================
// GameImage producer methods.
//==============================================================================
//------------------------------------------------------------------------------
// Basic GameImage methods.
//------------------------------------------------------------------------------
	private GameImage getGameImage(String line)
	// Create and return a basic GameImage.
	{
		StringTokenizer tokens = new StringTokenizer(line);

		if (tokens.countTokens() != 2)
		// Too many or two few arguments.
		{
			System.out.println("Wrong no. of arguments for " + line);
			return new GameImage();
		}
		else
		{
			tokens.nextToken();					// Skip command label.
			String path = tokens.nextToken();	// Record the file path.
			
			System.out.println("Generating GameImage using file " + path);
			return new GameImage(imageDir+path);
		}
	}
	
	private GameImage getGameImageStrip(String line)
	// Create and return a GameImageStrip.
	{
		StringTokenizer tokens = new StringTokenizer(line);

		if (tokens.countTokens() != 3)
		// Wrong number of arguments, return an empty GameImageStrip.
		{
			System.out.println("Wrong no. of arguments for " + line);
			return new GameImageStrip();
		}
		else
		// Line appears to be formatted correctly, attempt to create a
		// GameImageStrip.
		{
			tokens.nextToken();					// Skip command label
			String path = tokens.nextToken();	// Record the file path.
			int frames = 1;						// Default number of frames.
			
			try
			// Attempt to record the number of frames.
			{
				frames = Integer.parseInt(tokens.nextToken());
			}
			catch(Exception e)
			// Next token wasn't an integer.
			{
				System.out.println("Incorrect formatting for " + line);
				System.out.println("Using default settings...");
			}
			
			System.out.println(	"Generating GameImageStrip using file " + path +
								" with " + frames + " frames...");
			return new GameImageStrip(imageDir+path,frames);
		}
	}
	
	private GameImage getGameImageGrid(String line)
	// Create and return a GameImageStrip.
	{
		StringTokenizer tokens = new StringTokenizer(line);

		if (tokens.countTokens() != 4)
		// Wrong number of arguments, return an empty GameImageStrip.
		{
			System.out.println("Wrong no. of arguments for " + line);
			return new GameImageGrid();
		}
		else
		// Line appears to be formatted correctly, attempt to create a
		// GameImageStrip.
		{
			tokens.nextToken();					// Skip command label
			String path = tokens.nextToken();	// Record the file path.
			int frames = 1;						// Default number of frames.
			int rows = 1;						// Default number of rows.
			
			try
			// Attempt to record the number of frames.
			{
				frames	= Integer.parseInt(tokens.nextToken());
				rows	= Integer.parseInt(tokens.nextToken());
			}
			catch(Exception e)
			// Next token wasn't an integer.
			{
				System.out.println("Incorrect formatting for " + line);
				System.out.println("Using default settings...");
			}
			
			System.out.println(	"Generating GameImageGrid using file " + path +
								" with " + frames + " frames and " + rows + " rows.");
			return new GameImageGrid(imageDir+path,frames,rows);
		}
	}
//------------------------------------------------------------------------------
//==============================================================================


//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/* Don't need these right now.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

//------------------------------------------------------------------------------
// Methods to create and return a GameImageSequence.
//------------------------------------------------------------------------------
	private static GameImageSequence getGameImageSequence(String line)
	// Return a GameImageSequence. Creation is delegated to another method.
	{
		StringTokenizer tokens = new StringTokenizer(line);

		if (tokens.countTokens() != 3)
		{
			System.out.println("Wrong no. of arguments for " + line);
			return null;
		}
		else
		{
			tokens.nextToken();    // skip command label
			System.out.print("n Line: ");

			String path = tokens.nextToken();
			int number = -1;

			try
			{
				number = Integer.parseInt(tokens.nextToken());
			}
			catch(Exception e)
			{
				System.out.println("Number is incorrect for " + line);
			}

			return createGameImageSequence(path,number);
		}
	}

	public GameImageSequence createGameImageSequence(String path, int number)
	// Creates a GameImageSequence, given a file name with an asterisk, and a
	// number of files to be loaded.
	{
		GameImageSequence sequence = new GameImageSequence();
		
		String filename = null;						// Path/Name of file.
		String extension = null;					// File extension.
		int starPosn = path.lastIndexOf("*");		// Find the asterisk.
		
		if (starPosn == -1)
		// There is no asterisk, get the filename anyway.
		{
			System.out.println("No '*' in filename: " + path);
			filename = getPrefix(path);
		}
		else
		// There is an asterisk, split into filename + "*" + extension.
		{
			filename = path.substring(0,starPosn);
			extension = path.substring(starPosn+1);
		}

		for (int i = 0; i < number; i++)
		{
			GameImage temp = new GameImage(filename + i + extension);
			
			if (temp.getImage() != null)
				sequence.addFrame(temp);
		}
	}



  private int loadNumImages(String prefix, String postfix, int number)
	// Load a series of image files with the filename format
	//		prefix + <i> + postfix
	// where i ranges from 0 to number-1
  { 
    String imFnm;
    BufferedImage bi;
    ArrayList imsList = new ArrayList();
    int loadCount = 0;

    if (number <= 0) {
      System.out.println("Error: Number <= 0: " + number);
      imFnm = prefix + postfix;
      if ((bi = loadImage(imFnm)) != null) {
        loadCount++;
        imsList.add(bi);
        System.out.println("  Stored " + prefix + "/" + imFnm);
      }
    }
    else {   // load prefix + <i> + postfix, where i = 0 to <number-1>
      System.out.print("  Adding " + prefix + "/" + 
                           prefix + "*" + postfix + "... ");
      for(int i=0; i < number; i++) {
        imFnm = prefix + i + postfix;
        if ((bi = loadImage(imFnm)) != null) {
          loadCount++;
          imsList.add(bi);
          System.out.print(i + " ");
        }
      }
      System.out.println();
    }

    if (loadCount == 0)
      System.out.println("No images loaded for " + prefix);
    else 
      imagesMap.put(prefix, imsList);

    return loadCount;
  }  // end of loadNumImages()
//------------------------------------------------------------------------------


  // ------ grouped filename seq. of images ---------


  private void getGroupImages(String line)
	// format:
	//		g <name> <fnm>  [ <fnm> ]*
  { StringTokenizer tokens = new StringTokenizer(line);

    if (tokens.countTokens() < 3)
      System.out.println("Wrong no. of arguments for " + line);
    else {
      tokens.nextToken();    // skip command label
      System.out.print("g Line: ");

      String name = tokens.nextToken();

      ArrayList fnms = new ArrayList();
      fnms.add( tokens.nextToken() );  // read filenames
      while (tokens.hasMoreTokens())
        fnms.add( tokens.nextToken() );

      loadGroupImages(name, fnms);
    }
  }  // end of getGroupImages()



  public int loadGroupImages(String name, ArrayList fnms)
	// Can be called directly to load a group of images, whose filenames are
	// stored in the ArrayList <fnms>. They will be stored under the 'g' name
	// <name>.
  {
    if (imagesMap.containsKey(name)) {
      System.out.println( "Error: " + name + "already used");
      return 0;
    }

    if (fnms.size() == 0) {
      System.out.println("List of filenames is empty");
      return 0;
    }

    BufferedImage bi;
    ArrayList nms = new ArrayList();
    ArrayList imsList = new ArrayList();
    String nm, fnm;
    int loadCount = 0;

    System.out.println("  Adding to " + name + "...");
    System.out.print("  ");
    for (int i=0; i < fnms.size(); i++) {    // load the files
      fnm = (String) fnms.get(i);
      nm = getPrefix(fnm);
      if ((bi = loadImage(fnm)) != null) {
        loadCount++;
        imsList.add(bi);
        nms.add( nm );
        System.out.print(nm + "/" + fnm + " ");
      }
    }
    System.out.println();

    if (loadCount == 0)
      System.out.println("No images loaded for " + name);
    else {
      imagesMap.put(name, imsList);
      gNamesMap.put(name, nms);
    }

    return loadCount;
  }  // end of loadGroupImages()


  public int loadGroupImages(String name, String[] fnms)
  // supply the group filenames in an array
  {  
    ArrayList al = new ArrayList( Arrays.asList(fnms) );
    return loadGroupImages(name, al);  
  }
*/
}
