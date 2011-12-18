
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
	private String imageDir;				// Directory to look for images.
	
	private int numRows;					// Number of rows in the tilemap.
	private int numCols;					// Number of columns in the tilemap.

	private ArrayList<Tile> tileList;		// Tiles used in the tilemap.
	private Tile[][] tilemap;				// The arrangement of tiles.

	private GameImageFactory imgLoader;		// Factory to load images used by tiles.
	private static TileMapFactory factory;	// Reference to this object (Singleton).
	
	private JPanel parent;					// What shall be the parent of whatever
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

  // ----------- load the bricks information -------------------

  private void loadBricksFile(String fnm)
  /* Load the bricks map from a configuration file, fnm.
     The map starts with an image strip which contains the 
     images referred to in the map. Format:
        s <fnm> <number> 

     This means that the map can use images numbered 0 to
     <number-1>. We assume number is less than 10.

     The bricks map follows. Each line is processed by
     storeBricks(). There can only be at most
     MAX_BRICKS_LINES lines.

     The configuration file can contain empty lines and
     comment lines (those starting with //), which are ignored.
  */
  { 
    String imsFNm = IMAGE_DIR + fnm;
    System.out.println("Reading bricks file: " + imsFNm);

    int numStripImages = -1;
    int numBricksLines = 0;
    try {
      BufferedReader br = new BufferedReader( new FileReader(imsFNm));
      String line;
      char ch;
      while((line = br.readLine()) != null) {
        if (line.length() == 0)  // ignore a blank line
          continue;
        if (line.startsWith("//"))   // ignore a comment line
          continue;
        ch = Character.toLowerCase( line.charAt(0) );
        if (ch == 's')  // an images strip
          numStripImages = getStripImages(line);
        else {  // a bricks map line
          if (numBricksLines > MAX_BRICKS_LINES) 
            System.out.println("Max reached, skipping bricks line: " + line);
          else if (numStripImages == -1) 
            System.out.println("No strip image, skipping bricks line: " + line);
          else {
            storeBricks(line, numBricksLines, numStripImages);
            numBricksLines++;
          }
        }
      }
      br.close();
    } 
    catch (IOException e) 
    { System.out.println("Error reading file: " + imsFNm);
      System.exit(1);
    }
  }  // end of loadBricksFile()


  private int getStripImages(String line)
  /* format:
        s <fnm> <number>

     The strip images are used to represent the bricks when
     they are drawn. A number in the bricks map is mapped
     to the image at that position in the strip image.

     The strip images are loaded with an ImagesLoader object, and then
     retrieved to the bricksImages[] array.
  */
  { StringTokenizer tokens = new StringTokenizer(line);
    if (tokens.countTokens() != 3) {
      System.out.println("Wrong no. of arguments for " + line);
      return -1;
    }
    else {
      tokens.nextToken();    // skip command label
      System.out.print("Bricks strip: ");

      String fnm = tokens.nextToken();
      int number = -1;
      try {
        number = Integer.parseInt( tokens.nextToken() );
        imsLoader.loadStripImages(fnm, number);   // store strip image
        brickImages = imsLoader.getImages( getPrefix(fnm) ); 
            // store all the images in a global array
      }
      catch(Exception e)
      { System.out.println("Number is incorrect for " + line);  }

      return number;
    }
  }  // end of getStripImages()


  private String getPrefix(String fnm)
  // extract name before '.' of filename
  {
    int posn;
    if ((posn = fnm.lastIndexOf(".")) == -1) {
      System.out.println("No prefix found for filename: " + fnm);
      return fnm;
    }
    else
      return fnm.substring(0, posn);
  } // end of getPrefix()



  private void storeBricks(String line, int lineNo, int numImages)
  /* Read a single bricks line, and create Brick objects.
     A line contains digits and spaces (which are ignored). Each
     digit becomes a Brick object.
     The collection of Brick objects are stored in the bricksList
     ArrayList.
  */
  {
    int imageID;
    for(int x=0; x < line.length(); x++) {
      char ch = line.charAt(x);
      if (ch == ' ')   // ignore a space
        continue;
      if (Character.isDigit(ch)) {
        imageID = ch - '0';    // we assume a digit is 0-9
        if (imageID >= numImages)
          System.out.println("Image ID " + imageID + " out of range");
        else   // make a Brick object
          bricksList.add( new Brick(imageID, x, lineNo) );
      }
      else
        System.out.println("Brick char " + ch + " is not a digit");
    }
  }  // end of storeBricks()


  // --------------- initialise bricks data structures -----------------

  private void initBricksInfo()
  /* The collection of bricks in bricksList are examined to
     extract various global data, and to check if certain
     criteria are met (e.g. the maximum width of the bricks is
     greater than the width of the panel (width >= pWidth).
     Also each Brick object is assigned its image.
  */
  {
    if (brickImages == null) {
      System.out.println("No bricks images were loaded");
      System.exit(1);
    }
    if (bricksList.size() == 0) {
      System.out.println("No bricks map were loaded");
      System.exit(1);
    }

    // store brick image dimensions (assuming they're all the same)
    BufferedImage im = (BufferedImage) brickImages.get(0);
    imWidth = im.getWidth();
    imHeight = im.getHeight(); 

    findNumBricks();
    calcMapDimensions();
    checkForGaps();

    addBrickDetails();
  }  // end of initBricksInfo();

  
  private void findNumBricks()
  // find maximum number of bricks along the x-axis and y-axis
  {
    Brick b;
    numCols = 0;
    numRows = 0;
    for (int i=0; i < bricksList.size(); i++) {
      b = (Brick) bricksList.get(i);
      if (numCols < b.getMapX())
        numCols = b.getMapX();
      if (numRows < b.getMapY())
        numRows = b.getMapY();
    }
    numCols++;    // since we want the max no., not the max coord
    numRows++;
  }  // end of findNumBricks()


  private void calcMapDimensions()
  // convert max number of bricks into max pixel dimensions
  {
    width = imWidth * numCols;
    height = imHeight * numRows;

    // exit if the width isn't greater than the panel width
    if (width < pWidth) {
      System.out.println("Bricks map is less wide than the panel");
      System.exit(0);
    }
  }  // end of calcmapDimensions()


  private void checkForGaps()
  /* Check that the bottom map line (numRows-1) has a brick in every 
     x position from 0 to numCols-1.
     This prevents 'jack' from falling down a hole at the bottom 
     of the panel. 
  */
  {
    boolean[] hasBrick = new boolean[numCols];
    for(int j=0; j < numCols; j++)
      hasBrick[j] = false;

    Brick b;
    for (int i=0; i < bricksList.size(); i++) {
      b = (Brick) bricksList.get(i);
      if (b.getMapY() == numRows-1)
        hasBrick[b.getMapX()] = true;   
    }

    for(int j=0; j < numCols; j++)
      if (!hasBrick[j]) {
        System.out.println("Gap found in bricks map bottom line at position " + j);
        System.exit(0);
      }
  }  // end of checkForGaps()


  private void addBrickDetails()
  /* Add image refs to the Bricks, and calculate their
     y-coordinates inside the brick map. */
  {
    Brick b;
    BufferedImage im;
    for (int i=0; i < bricksList.size(); i++) {
      b = (Brick) bricksList.get(i);
      im = (BufferedImage) brickImages.get( b.getImageID());
      b.setImage(im);
      b.setLocY(pHeight, numRows);
    }
  }  // end of addBrickDetails()



  private void createColumns()
  // store bricks info by column for faster search/accessing
  {
    columnBricks = new ArrayList[numCols];
    for (int i=0; i < numCols; i++)
      columnBricks[i] = new ArrayList();

    Brick b;
    for (int j=0; j < bricksList.size(); j++) {
      b = (Brick) bricksList.get(j);
      columnBricks[ b.getMapX() ].add(b);    // bricks not stored in any order
    }
  }  // end of createColumns()
}
