package ds.triplething;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Data structure with the tiles in a grid
 */

public class TileGrid {
	
	public TileGrid(int width, int height) {
		this.width = width;
		this.height = height;
		
		grid = new Tile[width][height];
	}
	
	public int size() {
		return width*height;
	}
	
	/* not used, see TripleThing.init()
	public void fill(MouseListener l) {
		
		for(int x=0; x<width; ++x)
			for(int y=0; y<height; ++y) {
				Tile tile = new Tile(Images.ICO_BLANK, x, y);
				tile.setBorder(BorderFactory.createLineBorder(Color.black));
				tile.addMouseListener(l);
				grid[x][y] = tile;
			}
		
	}
	*/
	
	public boolean isFull() {
		for(int x=0; x<width; ++x)
			for(int y=0; y<height; ++y) {
				if (grid[x][y].getId() == Images.ICO_BLANK) {
					return false;
				}
			}
		return true;
	}
	
	public void clear() {
		for(int x=0; x<width; ++x)
			for(int y=0; y<height; ++y)
				grid[x][y].setEmpty();
	}

	
	/*
	 * Given a starting point (x,y) and a vector (dx,dy) check the 3 groups of 3
	 * on the vector
	 * i.e. given a line with 5 items on it 
	 *      1 2 3 4 5
	 *      where 3 is (x,y)
	 *      check 1,2,3 and 2,3,4 and 3,4,5
	 *      
	 * Return a map where the key is a hash of matched tiles and  
	 * entry is a list of tiles that match it
	 */
	protected void getTileLine(HashMap<String, List<List<Tile>>> combs, int x, int y, int dx, int dy) {
		
		int xcur = x;
		int ycur = y;

		for(int g=0; g<=2; ++g) {
			ArrayList<Tile> line = new ArrayList<Tile>();
			for(int t=0; t<3; ++t) {
				Tile tile = getAt(xcur, ycur);
				
				if (tile != null)
					line.add(tile);
				xcur += dx;
				ycur += dy;
			}
			
			String key = makeKey(line);
			
			List<List<Tile>> lstTiles = combs.get(key);
			if (lstTiles == null) {
				lstTiles = new ArrayList<List<Tile>>();
				combs.put(key, lstTiles);
			}
			lstTiles.add(line);

			xcur = xcur - 2*dx;
			ycur = ycur - 2*dy;
		}
					
	}
	
	// return all of the combinations
	// the key is the hash of the tiles, the list is the
	// tiles in a line (including the one that was just placed)
	public Map<String, List<List<Tile>>> getCombinations(Tile newTile) {
		HashMap<String, List<List<Tile>>> combs = new HashMap<String, List<List<Tile>>>();

		int cx = newTile.gridX();
		int cy = newTile.gridY();
		
		// vertical
		getTileLine(combs, cx, cy-2, 0, 1);

		// horizontal
		getTileLine(combs, cx-2, cy, 1, 0);
		
		// top left - bottom right
		getTileLine(combs, cx-2, cy-2, 1, 1);
		
		// bottom left - top right
		getTileLine(combs, cx-2, cy+2, 1, -1);
				
		return combs;
	}
	
	String makeKey(List<Tile> tiles) {
		StringBuilder key = new StringBuilder();
		
		for(Tile t : tiles)
			if (t != null) {
				if (key.length() > 0)
					key.append("_");
				key.append(t.getId());
			}
		
		return key.toString();
	}
	
	Tile getAt(int x, int y) {
		
		if (x < 0 || x >= width)
			return null;
		
		if (y < 0 || y >= height)
			return null;
		
		return grid[x][y];
	}

	int width() {
		return width;
	}
	
	int height() {
		return height;
	}
	
	int width;
	int height;
	
	Tile[][] grid;
}
