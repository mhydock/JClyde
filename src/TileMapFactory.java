//==============================================================================
// Date Created:		17 December 2011
// Last Updated:		20 December 2011
//
// File Name:			TileMapFactory.java
// File Author:			M Matthew Hydock
//
// File Description:	Reads in a file defining tiles and where they should be
//						arranged on a grid. It outputs a TileMap object. Very
//						loosely based on the BricksManager class written by
//						Andrew Davison ad@fivedots.coe.psu.ac.th
//==============================================================================

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;

public class TileMapFactory
{
//------------------------------------------------------------------------------
// Factory variables.
//------------------------------------------------------------------------------
	private String imageDir;				// Directory to look for images.
	private String filePath;				// File to read in to build map.
	private BufferedReader input;			// Reads in the input file.
	private boolean inputEnd;				// Whether the reader has reached the end.
	private String curr_line;				// Current line from input.

	private static GameImageFactory imgLoader;	// Factory to load images used by tiles.
	private static TileMapFactory factory;		// Reference to this object (Singleton).
//------------------------------------------------------------------------------


//------------------------------------------------------------------------------
// Tilemap variables. The only reason I'm not extending TileMap is because I
// only need the variables, not the methods.
//------------------------------------------------------------------------------	
	private int numRows;					// Number of rows in the tileMap.
	private int numCols;					// Number of columns in the tileMap.
	private int tile_size;					// Length of a side of a tile.

	private ArrayList<Tile> tileList;		// Tiles used in the tileMap.
	private Tile[][] tileMap;				// The arrangement of tiles.

	private int startX;						// Where the hero sprite starts.
	private int startY;

	private int exitX;						// Location of the exit.
	private int exitY;
	
	private int numGems;					// Number of gems at initialization of
											// the level.			
	
	private Component parent;				// What shall be the parent of whatever
											// tileMap is produced.
//------------------------------------------------------------------------------


//==============================================================================
// Factory initialization methods.
//==============================================================================
	private TileMapFactory()
	// Grab an instance of the GameImageFactory, and then initialize/nullify all
	// of the variables.
	{
		imgLoader = GameImageFactory.getInstanceOf();
		
		resetFactory();
	}
	
	public static TileMapFactory getInstanceOf()
	// Return a reference to this object.
	{
		if (factory == null)
			factory = new TileMapFactory();
			
		return factory;
	}

	private void resetFactory()
	// Completely reset the factory.
	{
		imageDir = null;
		filePath = null;
		
		input		= null;
		inputEnd	= true;
		curr_line	= null;
		
		parent = null;
		
		softReset();
	}
	
	private void softReset()
	// Only reset variables directly related to constructing the tileMap. Also,
	// reset the file reader (using the same file, if it had been set before).
	{
		if (filePath != null && curr_line != null)
			setInputFile(filePath);
			
		curr_line = null;
		tileList = null;
		tileMap = null;
		
		numRows = 0;
		numCols = 0;
		tile_size = 0;
		
		startX = -1;
		startY = -1;
		
		exitX = -1;
		exitY = -1;
		
		numGems = 0;
	}

	public void setInputFile(String path)
	// Sets the file to read from. If the path is wrong or there is no readable
	// file, then the whole system will quit.
	{
		System.out.println("Reading file: " + path);
		
		try
		{
			input = new BufferedReader(new FileReader(path));
			inputEnd = false;
			filePath = path;
		}
		catch (Exception e) 
		{
			System.out.println("Error reading file: " + path);
			System.out.println(e.toString());
			System.exit(1);
		}
		
		// So they are reading from the same file.
		imgLoader.setInputFile(path);
	}
	
	public void setParent(Component p)
	// Set the panel that will be controlling/drawing the tileMap produced.
	{
		parent = p;
	}
	
	public Component getParent()
	// Return the panel object that will be used as the parent of all tileMaps
	// produced at this time.
	{
		return parent;
	}
//==============================================================================


//==============================================================================
// File parsing methods.
//==============================================================================	
	public TileMap produceTileMap()
	// Parse an input file and produce a tile map.
	{
		// So that we're working with a mostly clean factory.
		softReset();
		
		// Build the tile list.
		buildTileList();
		
		if (tileList.size() == 0)
		// No tiles were loaded, so no map can be built.
			return null;
		
		try
		{
			// Skip lines until the tileMap section begins.
			while ((curr_line = input.readLine()) != null && !curr_line.contains("TILEMAP"));
		}
		catch (IOException e)
		{
			System.out.println("TileMapFactory was interrupted: " + e.toString());
			System.exit(1);
		}
			
		if (curr_line == null)
		// No tileMap section was found, so no map can be built.
			return null;

		if (curr_line.contains("TILEMAP"))
		// Phrase has been found that defines the dimensions of the tileMap.
			initTileMap();
		
		// Build the tile map.
		buildTileMap();
		
		// Initialize the tileMap, set the start and finish locations, and if
		// any gems were found, set the number of gems found.
		TileMap map = new TileMap(tileMap,tileList,1,tile_size,parent);
		map.setStartLoc(startX,startY);
		map.setExitLoc(exitX,exitY);
		map.setNumGems(numGems);
		
		return map;
	}

	private void buildTileList()
	// Create the tile list used in the tile map.
	{
		tileList = new ArrayList<Tile>();
		
		while (!imgLoader.atEnd())
		{
			GameImage temp = imgLoader.produceGameImage();
			GameAnimation anim = null;
			
			if (temp == null)
			// Either end of file or malformed expression.
				continue;
				
			if (imgLoader.canBeAnimated())
			// Image claims it can be animated, attemp to build the animation.
				anim = imgLoader.produceAnimation();
				
			if (anim == null)
			// Image is not animated, make basic tile.
				tileList.add(new Tile(temp,true));
			else
			// Image is animated, make animated tile.
				tileList.add(new AnimatedTile((GameImageStrip)temp,anim,true));
		}
	}
	
	private void initTileMap()
	// Initialize the tile map.
	{
		String line = new String(curr_line);
			
		if (line.contains("//"))
		// Remove any comments from this line.
			line = line.substring(0,line.indexOf("//"));
			
		StringTokenizer tokens = new StringTokenizer(line);
			
		if (tokens.countTokens() != 4)
		// Not enough tokens to set the dimension of the tileMap.
		{
			System.out.println("Malformed tileMap expression: " + line);
			System.exit(1);
		}
			
		tokens.nextToken();			// Get rid of 'TILEMAP'
			
		try
		// Attempt to record the dimensions of the tileMap.
		{
			numRows = Integer.parseInt(tokens.nextToken());
			numCols = Integer.parseInt(tokens.nextToken());
			tile_size = Integer.parseInt(tokens.nextToken());
		}
		catch(Exception e)
		// Next token wasn't an integer.
		{
			System.out.println("Incorrect formatting for tileMap dimensions: " + line);
			System.exit(1);
		}
	}
	
	private void buildTileMap()
	// Parse the tileMap section of the file. If a brick has no animation, or
	// it is animated but in normal mode, just use the reference from the tile
	// list. If a brick is animated, but is in sporadic mode, duplicate the
	// animation object, to ensure the sporadic animations are unsynchronized.
	{
		for (int i = 0; i < numRows && curr_line != null; i++)
		{
			for (int j = 0; j < numCols && j < curr_line.length(); j++)
			{
				char ch = curr_line.charAt(j);
				
				if (Character.isDigit(ch))
				// If the current character is a digit, use the tile list.
				{
					int index = Integer.parseInt(""+ch);
					
					if (index < tileList.size())
					// If the index is within bounds, set the tile in the 
					// tileMap.
					{	
						Tile tile = tileList.get(index);
						GameAnimation anim = tile.getAnimation();
						
						if (anim != null && anim.isSporadic())
						// Tile is animated and sporadic; duplicate the tile,
						// and randomize the animation.
						{
							tileMap[i][j] = new AnimatedTile((GameImageStrip)tile.getImage(),anim.clone(),tile.isCollidable());
							tileMap[i][j].getAnimation().restartAt((int)(Math.random() * (anim.getNumberFrames()-1)));
						}
						else
						// Tile is either a basic tile, or animating under
						// normal mode. Just copy the reference.
							tileMap[i][j] = tile;
					}
				}
				else if (ch == 'c')
				// Found the start location.
				{
					startX = j;
					startY = i;
				}
				else if (ch == 'e')
				// Found the exit location.
				{
					exitX = j;
					exitX = i;
				}
			}
		
			try
			{
				// Read in the next line of the map.	
				curr_line = input.readLine();
			}
			catch (IOException e)
			{
				System.out.println("TileMapFactory was interrupted: " + e.toString());
				System.exit(1);
			}
		}
			
		if (startX == -1 || startY == -1 || exitX == -1 || exitY == -1)
		// Either start or exit wasn't found. This is a bad map.
		{
			System.out.println("Bad tileMap: start or exit not set in tileMap.");
			System.exit(1);
		}
	}					
}
