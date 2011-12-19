
// BricksManager.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Loads and manages a wraparound bricks map. 
   It also deals with various collision detection tests from
   JumperSprite.

   A 'bricks map' is read in from a configuration file, and used
   to make an ArrayList of Brick objects, bricksList. 

   The collection of bricks defines a bricks map, which is moved
   and drawn in the same way as an image ribbon in a Ribbon object.

   The bricks map movement step is fixed by moveSize, which
   is some fraction of the width of a brick image.

   ----
   JumperSprite uses BricksManager for collision detection.
   As it rises/falls it must curtail the movement if it will
   enter a brick; checkBrickBase() and checkBrickTop() are used
   for testing this.

   When JumperSprite moves left/right, it must first check that it will
   not move into a brick. It uses insideBrick() for this test.

   When JumperSprite does move, it may move off into space -- there
   is no brick below it. It tests this using insideBrick() also.
*/

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;


public class TileMapFactory
{
	private static String imageDir;				// Directory to look for images.
	private static BufferedReader input;		// Reads in the input file.
	private static boolean inputEnd;			// Whether the reader has reached the end.
	private static String curr_line;			// Current line from input.
	
	private static int tile_size;				// Length of a side of a tile.
	private static int numRows;					// Number of rows in the tilemap.
	private static int numCols;					// Number of columns in the tilemap.

	private static ArrayList<Tile> tileList;	// Tiles used in the tilemap.
	private static Tile[][] tilemap;			// The arrangement of tiles.

	private static GameImageFactory imgLoader;	// Factory to load images used by tiles.
	private static TileMapFactory factory;		// Reference to this object (Singleton).
	
	private static JPanel parent;				// What shall be the parent of whatever
												// tilemap is produced.

	private TileMapFactory()
	{
		imgLoader = GameImageFactory.getInstance();
		
		reset();
	}
	
	public static TileMapFactory getInstance()
	{
		if (factory == null)
			factory = new TileMapFactory();
			
		return factory;
	}

	private void reset()
	{
		imageDir = null;
		tileList = null;
		tilemap = null;
		
		numRows = 0;
		numColumns = 0;
		
		parent = null;
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
		
		// So they are reading from the same file.
		imgLoader.setInputFile(path);
	}
	
	public void setParent(JPanel p)
	{
		parent = p;
	}
	
	public TileMap produceTileMap()
	{
		createTiles();
		
		if (tileList.size() == 0)
		// No tiles were loaded, so no map can be built.
			return null;
		
		// Skip lines until the tilemap section begins.
		while ((curr_line = input.nextLine()) != null && !curr_line.equals("TILEMAP"));
		
		if (curr_line == null)
		// No tilemap section was found, so no map can be built.
			return null;

		while 


	private void createTiles()
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
				tileList.add(new AnimatedTile(temp,anim,true));
		}
	}
}
