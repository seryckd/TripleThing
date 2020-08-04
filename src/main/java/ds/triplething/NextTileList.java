package ds.triplething;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import ds.lib.utils.RandUtil;

/*
 * Panel that uses an image as a background
 * and places 3 icons in certain spots
 * 
 * Icons animate as they are selected
 */

@SuppressWarnings("serial")
public class NextTileList extends JPanel {
	
	// -----------------------------------------------------------------------
	// Setup API
	
	static int GAP1 = 5;
	static int GAP2 = 20;	
	
	public NextTileList() {
		
		setLayout(new MigLayout("wrap 1",		// layout
				"10[]10",						// column
				"10[]5[]20[]10"));				// row
		
		tiles = new ArrayList<TileInfo>(3);
		
		for(int t=0; t<3; t++) {
			TileInfo tile = new TileInfo();
			tiles.add(tile);
		}
		
		background = Images.loadBackground("BgNextTile.png");
		
		for(int t=tiles.size()-1; t>=0; t--)
			add(tiles.get(t));
		
		setPreferredSize(new Dimension(background.getWidth(), background.getHeight()));
	}
	
    @Override 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.drawImage(background, 0, 0, null);
    }	
		
	public void addTile(Integer tile, int weight) {
		tileChoices.add(new Choice(tile, weight));
	}
	
	public void setTileChoices() {
		
		total = 0;
		
		for(int t=0; t<tileChoices.size(); t++) {
			Choice i = tileChoices.get(t);
			
			total += i.weight;
			i.top = total;			
		}		
		
		for(int t=0; t<tiles.size(); t++)
			tiles.get(t).setIcon(getRandomTile());
	}

	// -----------------------------------------------------------------------
	// Public 
	
	public Integer headId() {
		return tiles.get(0).actualId;
	}
	
	public Integer popNextTile(boolean initial) {
		
		// move the actual ids around, but do not 
		// change the display icons just yet
		
		Integer headId = tiles.get(0).actualId;
		
		for(int t=0; t<tiles.size()-1; t++)
			tiles.get(t).setActualId(tiles.get(t+1).getActualId());
		
		tiles.get(tiles.size()-1).setActualId(getRandomTile());
		
		// start a task to change the display icons
		
		if (initial) {
			for(TileInfo tile : tiles)
				tile.setActualIcon();
			
		} else {
		//TODO
			AnimationTimerTask.startAnimation(AnimationTimerTask.s_layerPane, new NextTileControl(getX(), getY()));
		}

		
		return headId;
	}
	
	// -----------------------------------------------------------------------
	// Internal
	
	
	Integer getRandomTile() {
		
		int pick = RandUtil.getFromRange(0, total);
		
		for(int t=0; t<tileChoices.size(); t++) {
			Choice i = tileChoices.get(t);
			
			if (pick <= i.top)
				return i.choice;
		}
		
		System.out.println("picking random tile failed. Rnd="+pick);
		for(int t=0; t<tileChoices.size(); t++) {
			Choice i = tileChoices.get(t);
			System.out.println("  top="+i.top);
		}
			
		return tileChoices.get(tileChoices.size()-1).choice;
		
	}
	

	class TileInfo extends JLabel {
		Integer displayId;
		Integer actualId;
		
		public TileInfo() {
			actualId = Images.ICO_BLANK;
			setDisplayEmpty();
		}
		
		public Integer getActualId() {
			return actualId;
		}
		
		public Integer getDisplayId() {
			return displayId;
		}
		
		public void setIcon(Integer id) {
			setActualId(id);
			setActualIcon();
		}
		
		public void setActualId(Integer id) {
			actualId = id;
		}
					
		public void setActualIcon() {
			displayId = actualId;			
			setIcon(Images.getIcon(displayId));
		}
		
		public void setDisplayEmpty() {
			displayId = Images.ICO_BLANK;
			setIcon(Images.getIcon(displayId));

			repaint();
		}		
	}
	
	// from top to bottom
	ArrayList<TileInfo> tiles;
	
	
	class Choice {
		Choice(Integer _choice, int _weight) {
			choice = _choice;
			weight = _weight;
		}
		
		Integer choice;
		int weight;
		int top;
	}
	
	// ------------------------------------------------------------------------
	class NextTileControl implements AnimationTimerTask.AnimationControl {

		NextTileControl(int xOffset, int yOffset) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		}
		
		public List<AnimationTimerTask.AnimatedIcon> start() {
			ArrayList<AnimationTimerTask.AnimatedIcon> icons = new ArrayList<AnimationTimerTask.AnimatedIcon>();			
			
			// Create new tiles and place them on top of the existing ones
		
			TileInfo topTile = tiles.get(2);
			TileInfo midTile = tiles.get(1);
			TileInfo lowTile = tiles.get(0);
			icons.add(new NextTileAnimation(topTile.getDisplayId(), topTile.getX()+xOffset, topTile.getY()+yOffset, midTile.getY()+yOffset));
			icons.add(new NextTileAnimation(midTile.getDisplayId(), midTile.getX()+xOffset, midTile.getY()+yOffset, lowTile.getY()+yOffset));
	
			topTile.setDisplayEmpty();
			midTile.setDisplayEmpty();
			lowTile.setDisplayEmpty();
			
			return icons;
		}
		
		public void stop() {
			
			// we finished the animation so display all the
			// actual icons
			for(TileInfo tile : tiles)
				tile.setActualIcon();
		}
		
		public int getZOrder() {
			// Use default ordering
			return -1;
		}
		
		public int getPeriodMs() {
			// Do this quickly, no need to keep the player waiting
			return 10;
		}
		
		int xOffset;
		int yOffset;		
	}
	
	 class NextTileAnimation implements AnimationTimerTask.AnimatedIcon {
				
		public NextTileAnimation(int image, int _xstart, int _ystart, int _yfinal) {
			lbl = new JLabel();
			
			xcur = _xstart;
			ycur = _ystart;
			yfinal = _yfinal;
			
			yoff = (yfinal - _ystart) / 10;
										
			lbl.setBounds((int)xcur, (int)ycur, 60, 60);
			lbl.setIcon(Images.getIcon(image));
		}

		public JLabel getLabel() {
			return lbl;
		}

		public boolean updatePos(int frameNum) {
			if (ycur >= yfinal)
				return true;

			ycur = ycur+yoff;
				
			lbl.setBounds((int)xcur, (int)ycur, 60, 60);
			return false;
		}
				
		JLabel lbl;
		
		float xcur;
		float ycur;
		
		int yfinal;		
		float yoff;
	}
	 
	// ------------------------------------------------------------------------
	
	BufferedImage background;
	Integer total;
	Vector<Choice> tileChoices = new Vector<Choice>();	
}
