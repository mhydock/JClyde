//==============================================================================
// Date Created:		11 December 2011
// Last Updated:		24 December 2011
//
// File Name:			TileMap.java
// File Author:			M Matthew Hydock
//
// File Description:	A simple tile map. Contains an array of all the tiles,
//						along with a list of the different kinds of tiles used.
//						Handles sprite collision detection.
//
//						Partially adapted from Andrew Davison's BricksManager
//						class. The tilemap loading has been separated into
//						TileMapFactory.
//==============================================================================

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;

public class TileMap extends GameLayer
{
	private int tile_size;				// Length of a side of a (square) tile.
	private int numCols;				// Number of columns.
	private int numRows;				// Number of rows.
	
	private ArrayList<Tile> tile_list;	// List of tiles to be used in the tilemap.
	private Tile[][] tilemap;			// Grid of tiles that make up the tilemap.

	private int startX;					// Where the hero sprite starts.
	private int startY;

	private int exitX;					// Location of the exit.
	private int exitY;
	
	private int numGems;				// Number of gems at initialization of
										// the level.
	
	public TileMap(Tile[][] map, ArrayList<Tile> list, int d, int s, Component p)
	// Create a new TileMap, using a preloaded list of tiles and a 2D array of
	// tiles representing the tilemap itself. Also asks for the depth of the
	// layer and the size of a tile, which will be the on-screen space reserved
	// when drawing a single tile.
	{
		super(d,p);
		
		numRows = map.length;
		numCols = map[0].length;
		
		tile_size = s;
		
		tile_list	= list;
		tilemap		= map;
	}

//==============================================================================
// Getters and Setters.
//==============================================================================
	public void setNumGems(int g)
	// Set the total number of gems in the tilemap.
	{
		numGems = g;
	}
	
	public int getNumGems()
	// Get the total number of gems in the tilemap.
	{
		return numGems;
	}
		
	public void setStartLoc(int x, int y)
	// Set the start location for the hero.
	{
		startX = x;
		startY = y;
	}

	public int getStartX()
	// Get x coord of start location.
	{
		return startX;
	}
	
	public int getStartY()
	// Get y coord of start location.
	{
		return startY;
	}
	
	public void setExitLoc(int x, int y)
	// Set the exit location for the hero.
	{
		exitX = x;
		exitY = y;
	}

	public int getExitX()
	// Get x coord of exit location.
	{
		return exitX;
	}
	
	public int getExitY()
	// Get y coord of exit location.
	{
		return exitY;
	}
	
	public int getTileSize()
	// Get the size of a tile (the length of a side).
	{
		return tile_size;
	}
	
	public void setTileSize(int s)
	// Set the size of a tile (the length of a side).
	{
		tile_size = s;
	}

	public int getMapWidth()
	// Get the width of the whole map, in pixels.
	{
		return tile_size*numCols;
	}
	
	public int getMapHeight()
	// Get the height of the whole map, in pixels.
	{
		return tile_size*numRows;
	}
	
	public int getNumRows()
	// Get the number of rows in the tilemap.
	{
		return numRows;
	}
	
	public int getNumColumns()
	// Get the number of columns in the tilemap.
	{
		return numCols;
	}
//==============================================================================


//==============================================================================
// Utility methods.
//==============================================================================
	private void checkForGaps() throws Exception
	// Make sure there aren't any gaps for the hero sprite to fall through.
	{
		for (int i = 0; i < tilemap[0].length; i++)
			if (tilemap[tilemap.length-1][i] == null || !tilemap[tilemap.length-1][i].isCollidable())
				throw new Exception("WARNING: There is at least one gap in the floor. Please correct this.");
	}

	public void display(Graphics g)
	// Draw all of the tiles that should be at least partly visible.
	{
		// Range of tiles to draw.
		int startX = -xOffset/tile_size;
		int startY = -yOffset/tile_size;
		int endX = parent.getWidth()/tile_size + startX;
		int endY = parent.getHeight()/tile_size + startY;
		
//		System.out.println(startX + "  " + endX + "  " + tilemap[0].length);
//		System.out.println(startY + "  " + endY + "  " + tilemap.length);
		
		// Calculate the real offset for the section of the tilemap that needs
		// to be drawn.
		int newXOffset = xOffset + startX*tile_size;
		int newYOffset = yOffset + startY*tile_size;
		
		// Draw the desired range of tiles, shifting them appropriately.
		for (int i = startY; i <= endY; i++)
			for (int j = startX; j <= endX; j++)
				if (tilemap[i][j] != null)
					tilemap[i][j].draw(g,j*tile_size+newXOffset,i*tile_size+newYOffset);
	}
	
	public void update()
	// Update all of the tiles in the tilemap. If a removal has been scheduled,
	// remove the tile.
	{
		for (int i = 0; i < tilemap.length; i++)
			for (int j = 0; j < tilemap[0].length; j++)
			{
				if (tilemap[i][j] != null)
				{
					if (tilemap[i][j].isRemovalScheduled())
						tilemap[i][j] = null;
					else
						tilemap[i][j].update();
				}
			}
			
//		System.out.println("Tilemap updated.");
	}
//==============================================================================


//==============================================================================
// Collision detection methods.
//==============================================================================
	public Tile getTileAt(int x, int y)
	// Simple method to obtain a tile, given a global x and y.
	{
		if (x >= 0 && x < getMapWidth() && y >= 0 && y < getMapHeight())
			return tilemap[y/tile_size][x/tile_size];
			
		return null;
	}
	
	public boolean insideSolidTile(int x, int y)
	// Check if the given coordinate is inside a solid brick.
	{
		Tile t = getTileAt(x,y);
		if (t != null)
			return t.isCollidable();
			
		return false;
	}
	
	public void removeTile(int x, int y)
	// Remove the tile located at (x,y).
	{
		if (x >= 0 && x < numCols && y >= 0 && y < numRows)
			tilemap[y][x] = null;
	}
//==============================================================================
}
