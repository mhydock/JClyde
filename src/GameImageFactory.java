//==============================================================================
// Date Created:		26 November 2011
// Last Updated:		15 December 2011
//
// File Name:			GameImageFactory.java
// File Author:			M Matthew Hydock
//
// File description:	A Singleton Factory designed to read in a file, and
//						spit out GameImage objects. It can produce simple
//						GameImages, GameImageStrips, GameImageGrids,
//						GameImageSequences, and GameImageGroups.
//==============================================================================

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;


public class GameImageFactory
{
	private static String directory = "Images/";
	private static BufferedReader input;
	private static boolean inputEnd;
	private static String curr_line;
	
	private static GameImagesFactory factory;

	private GameImageFactory()
	// Singleton constructor for the GameImageFactory.
	{		
		input = null;
		inputEnd = true;
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
		System.out.println("Reading file: " + path);
		
		try
		{
			InputStream in = this.getClass().getResourceAsStream(path);
			input = new BufferedReader(new InputStreamReader(in));
			inputEnd = false;
		}
		catch (IOException e) 
		{
			System.out.println("Error reading file: " + path);
			System.exit(1);
		}
	}
	
	public GameImage produceGameImage()
	// Parses input until a GameImage is generated.
	// Recognizes the following formats:
	//
	//	o <fnm>							>> a single image
	//	s <fnm> <number>				>> a strip of images
	//	d <fnm> <rows> <columns>		>> a grid of images
	//	n <fnm*.ext> <number>			>> a numbered sequence of images
	//	g <name> <fnm> [ <fnm> ]*		>> a group of images 
	//
	// Skips blank lines and comment lines.
	{
		char ch;
		while (curr_line = input.nextLine() && curr_line != null)
		{
			if (line.length() == 0)			// Blank line
				continue;
			if (line.startsWith("//"))		// Comment line
				continue;
			
			if (line.equals("TILEMAP"))		// The Tilemap has begun, stop
				break;						// trying to load images.
			
        	parseLine(line);
		}
		
		inputEnd = true;
		return null;
	}
	
	public static GameImage parseLine(String line)
	// Parse a single line, returning a GameImage object. Separated from the
	// produceGameImage method because it could come in handy elsewhere.
	{
		ch = Character.toLowerCase(curr_line.charAt(0));

		switch (ch)
		{
			case 'o':	return getGameImage(curr_line);
			case 's':	return getGameImageStrip(curr_line);
			case 'd':	return getGameImageGrid(curr_line);
			case 'n':	return getGameImageSequence(curr_line);
			case 'g':	return getGameImageGroup(curr_line);
			default	:	System.out.println("Do not recognize line: " + curr_line);
		}
		
		return null;
	}

	private String getPrefix(String path)
	// Extract the path before the file extension.
	{
		int posn;
		if ((posn = fnm.lastIndexOf(".")) == -1)
		{
			System.out.println("No prefix found for filename: " + fnm);
			return path;
		}
		else
			return path.substring(0, posn);
	}
	
	private String getExtension(String path)
	// Extract the file extension, including the '.'.
	{
		int posn;
		if ((posn = fnm.lastIndexOf(".")) == -1)
		{
			System.out.println("No extension found for filename: " + path);
			return "";
		}
		else
			return path.substring(posn-1);
	}
//============================================================================//
	
	
//==============================================================================
// GameImage producer methods.
//==============================================================================
//------------------------------------------------------------------------------
// Basic GameImage methods.
//------------------------------------------------------------------------------
	private static GameImage getGameImage(String line)
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
			return new GameImage(path);
		}
	}
	
	private static GameImage getGameImageStrip(String line)
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
			return new GameImageStrip(path,frames);
		}
	}
	
	private static GameImage getGameImageGrid(String line)
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
			return new GameImageStrip(path,frames,rows);
		}
	}
//------------------------------------------------------------------------------

//==============================================================================
//==============================================================================
/* Don't need these right now.
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
