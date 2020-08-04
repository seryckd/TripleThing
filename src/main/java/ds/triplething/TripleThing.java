package ds.triplething;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ds.triplething.match.DisplayMatches;
import ds.triplething.match.MatchResult;
import ds.triplething.match.MatchResultInstance;
import ds.triplething.match.Matches;

import ds.util.ImageUtil;
import ds.util.RandUtil;
import net.miginfocom.swing.MigLayout;


/*
 * in progress
 * 
 * for multiple selection, group together all the matches
 * and show them all as the blue border
 * 
 * for single selection
 * briefly flash blue borders around the matches
 */

/*
 * BUG  if the last tile is taken but it is a multiple match
 *      the game will end without the match being made
 *      
 * need to have the tiles indicate a match could be made when
 * the preview tile is highlighted (pieces animated? coloured background?)
 * 
 * display match history list
 * dynamic board - some pieces cause extra board to appear
 * when a piece is created then add it to a 2nd choice in the bottom list
 * some pieces have special rules, like being able to overwrite existing tile
 *   (this would imply a graphical hint that the tile the mouse is over
 *    could be used)
 * modify rules on the fly
 * store/load rules/configuration
 * 
 * start of with a small selection.  as you match to make more then
 *    they become available. Unlockable pieces. Oh, how about a
 *    wildcard tile
 *    
 * when make a triplet have the score appear on the square that matched
 *   (have it appear as float over) - including x Combo (x2 for double, x3 for triple, etc)
 *  
 * need utility program to shrink the jpgs to smaller size 
 * (cut down on size and loading times)
 * also perhaps a sprite sheet
 * 
 * need points to show up on the full/hints mode
 * 
 * bug - after tile has gone, the empty tile does not have the preview in it
 * 
 * detect game has ended and have a button to start new or quit
 * also have a reset / quit button available at any time
 * 
 * multiple selection
 *   moving the mouse in the targeted square towards
 *   the other selections will cause a selection change
 *   
 * be able to select different sets of matches
 * also editor to define own matches (and point values)
 * 
 * help text that shows how to play the game
 * 
 * random generator. modify so that the bomb/mystery appears maximum of
 * 1 in 20, or whatever
 */

/**
 * terminology
 * 
 * blue border	- highlight
 * red dots		- selection
 */

public class TripleThing extends JFrame implements MouseListener, ActionListener, MouseMotionListener {
		
	static int NUM_NEXT_TILES = 2;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TripleThing() {
                        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
				
        pack();        
	}
	
	JLayeredPane layerPane = new JLayeredPane();
	
	static int WIDTH = 800;
	static int HEIGHT = 500;
	
	/**
	 * Experiment with fixed size
	 * JLayeredPane is used to hold the background panel and JLabels to float over the top
	 * background pane is used as the place holder for all the rest.
	 */
	void init() {
		AnimationTimerTask.setLayerPane(layerPane);
		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		add(layerPane, BorderLayout.CENTER);
		layerPane.setBounds(0, 0, WIDTH, HEIGHT);
		
		JPanel background = new JPanel();
		background.setLayout(new MigLayout(""));

		// --------------------------------------------------------------------
		// top row
		
		// title graphic
		try {
			BufferedImage titleImg = Images.loadBackground("title.png");
			ImageIcon titleIco = Images.makePNGIcon(titleImg, titleImg.getWidth(), titleImg.getHeight());
			background.add(new JLabel(titleIco), "span 2,split");
		} catch(IOException ex) {}
		
		// space (graphic to be added later), then score aligned to the right
		background.add(ctrlScore, "gap rel:push,align right,wrap");	// align right
		
		// --------------------------------------------------------------------
		// middle row
		// left is next tile list, right is grid
		nextTileList = new NextTileList();
		background.add(nextTileList, "aligny top");		
		
		JPanel p = new JPanel();
		p.setLayout(new MigLayout("",		// layout
				"10[]0[]0",					// column - small gap at the left, no gap at right
				"0[]0[]0"));					// row - no gap at the bottom
		
		for(int y=0; y<grid.height(); ++y) {
			for(int x=0; x<grid.width(); ++x) {
				Tile tile = new Tile(Images.ICO_BLANK, x, y);
				tile.setBorder(BorderFactory.createLineBorder(Color.black));
				tile.addMouseListener(this);
				grid.grid[x][y] = tile;
				p.add(tile, (x == grid.width()-1) ? "wrap" : "");
			}
		}
						
		background.add(p, "aligny top,spany");
				
		// Set the scroll pane size to be the width of the DisplayMatch
		// panel (i.e. no scroll) but the height to be the size of the 
		// tile grid
		// Note:  this doesn't actually work as we need to do this after the 
		// pack() statement.  We do it here so that pack() does't use the 
		// size of the displayMatch panel when calculating the size
				
		displayPanel = new DisplayMatches(new Tile(0,0,0));
		background.add(displayPanel, "aligny top,spany");
		displayPanel.setHeight(p.getSize().height);
		displayPanel.revalidate();
		
		// --------------------------------------------------------------------
		// bottom row
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		
		ctrlReset = new JButton("<html>New<br>Game</html>");
		ctrlReset.setActionCommand(CMD_NEWGAME);
		ctrlReset.addActionListener(this);
		buttonPanel.add(ctrlReset);
		ctrlQuit = new JButton("Quit");
		ctrlQuit.setActionCommand(CMD_QUIT);
		ctrlQuit.addActionListener(this);
		buttonPanel.add(ctrlQuit);
		
		background.add(buttonPanel, "newline");
				
		// create list of random choices
//		nextTileList.addTile(Images.ICO_BLOCK, 5);	// has no matches so DisplayMatches.addrowstodisplay fails
		nextTileList.addTile(Images.ICO_GRASS, 30);
		nextTileList.addTile(Images.ICO_ROCK, 30);
		nextTileList.addTile(Images.ICO_BUSH, 20);
		nextTileList.addTile(Images.ICO_TREE1, 20);
		nextTileList.addTile(Images.ICO_HOUSE1, 10);
		nextTileList.addTile(Images.ICO_MYSTERY, 10);
		nextTileList.addTile(Images.ICO_BOMB, 5);
		
		nextTileList.setTileChoices();
		setNextTile(true);
		updateScore(0);

		mysteryTileList.add(Images.ICO_FACTORY2);
		mysteryTileList.add(Images.ICO_HOUSE2);
		mysteryTileList.add(Images.ICO_STORE2);
		mysteryTileList.add(Images.ICO_TREE1);
		mysteryTileList.add(Images.ICO_ROCK);
		
		background.setBounds(0, 0, WIDTH, HEIGHT);
		layerPane.add(background, new Integer(50));		
		
		pack();
		
		// complicated little way of getting DisplayMatches
		// to be the same size as the tile panel		
		displayPanel.setHeight(p.getSize().height);
		displayPanel.revalidate();
		
		xTileOffset = p.getX();
		yTileOffset = p.getY();
	}
	int xTileOffset;
	int yTileOffset;
	
	void resetGame() {
		// Cancel any timers
		AnimationTimerTask.stopAll();

		possibleMatches = null;
		currentlySelectedMatch = null;
		groupedMatches.clear();
		
		state.set(STATE_PLACE);
		grid.clear();
		
		score = 0;
		updateScore(0);
		
		setNextTile(true);
	}
	
	void setNextTile(boolean fInitial) {
		
		// check for end conditions
		// no more spaces on the board
		
		if (grid.isFull()) {
			state.set(STATE_ATRACT);
		
		} else {
		
			nextTileList.popNextTile(fInitial);					
			displayPanel.updateHintList(nextTileList.headId());
		}
	}
	
	void showPreviewTile(Tile tile) {
		if (tile.getId() == Images.ICO_BLANK)
			tile.setPreviewTile(nextTileList.headId());	
	}

	/**
	 * A match has been selected
	 * @param match
	 * @param combo		score multiplier
	 */
	void matchMade(MatchResultInstance match, int combo) {
		matchMade(match.getMatchIconId(), combo);
	}
	void matchMade(Integer iconId, int combo) {
		possibleMatches = null;
		currentlySelectedMatch = null;
		
		state.getAndSet(STATE_WAIT);
		
		// select all match instances that are part of the group
		ArrayList<MatchResultInstance> groups = groupedMatches.get(iconId);
			
		int newScore = 0;
		ArrayList<Integer> scores = new ArrayList<Integer>();
		
		for(MatchResultInstance matchGroup : groups) {
			int score = matchGroup.getScore() * combo;
			newScore += score;
			scores.add(score);
		}
			
		AnimationTimerTask.startAnimation(layerPane, new UpgradeControl(groups, combo));		
		AnimationTimerTask.startAnimation(layerPane, new ScoreControl(groups.get(0).getUpgradeTile(), scores));
			
		updateScore(newScore);
	}
	
	MatchResultInstance getMatchForTile(Tile tile) {
		for(MatchResultInstance match : possibleMatches) {
			for (Tile matchTile : match.getTiles())
				if (matchTile == tile) {
					return match;
				}
		}
		
		return null;
	}
		
	void setCurrentMatch(MatchResultInstance match) {
				
		// make sure to remove from the previous
		currentlySelectedMatch = match;
		
		for(Tile selTile : currentlySelectedMatch.getTiles())
			selTile.setTileOverlay(
					(selTile == currentlySelectedMatch.getUpgradeTile() ? Images.ICO_A_MATCH_MAIN : Images.ICO_A_MATCH), 
					Images.ICO_A_SELECT);
/*			
			if (selTile == currentlySelectedMatch.getUpgradeTile())
				selTile.setTileOverlay(Images.ICO_A_MATCH_MAIN, Images.ICO_A_SELECT);
			else
				selTile.setTileOverlay(Images.ICO_A_MATCH, Images.ICO_A_SELECT);
*/				
			
	}
	
	void unSelectCurrentMatch() {
		if (currentlySelectedMatch != null)
			for(Tile selTile : currentlySelectedMatch.getTiles())
				if (selTile != currentlySelectedMatch.getUpgradeTile())
					selTile.setTileOverlay(Images.ICO_A_MATCH);
	}
	

	/**
	 * dest		Location of placed tile
	 * combo	Number of sequential matches so far
	 * 			0 means the mystery tile
	 *          1 means the first tile
	 *          2+ means a combo is going on (get double points)
	 */
	void placeTile(Tile dest, int combo) {
		
		// Don't overwrite an existing tile
		// (should never happen)
		if (combo == 1 && (dest.getId() != Images.ICO_BLANK))
			return;
		
		//??possibleMatches = null;
		state.getAndSet(STATE_SINGLE_SELECTION);
		
		// Show the new id in the tile. If we are part of a combo this
		// has already been done
		if (combo == 1)	{		
			dest.setTile(nextTileList.headId());
		}

		// mystery tile has already resolved to another tile, continue
		// as a normally placed tile
		if (combo == 0)
			combo = 1;
		
		if (Images.ICO_MYSTERY == dest.getId()) {
			// special, the mystery tile transforms into a random tile			
			state.getAndSet(STATE_WAIT);
			AnimationTimerTask.startAnimation(layerPane, new MysteryControl(dest));			
			return;
		}
		
		if (Images.ICO_BOMB == dest.getId()) {
			// special, the bomb blows up other tiles
			state.getAndSet(STATE_WAIT);
			AnimationTimerTask.startAnimation(layerPane, new ExplodeControl(dest.xpos, dest.ypos));			
			return;
		}
				
		// we need to know
		// the 3 tiles that make a match
		// which one of the tiles will change
		// the icon it will change to and the points
		
		Vector<MatchResultInstance> matches = new Vector<MatchResultInstance>();
		groupedMatches.clear();
		
		// String is hash of tiles, so can be ignored for now
		Map<String, List<List<Tile>>> combs = grid.getCombinations(dest);
		
		for(Entry<String, List<List<Tile>>> entry : combs.entrySet()) {
			// entry.key	- hash key used to look in matches
			// entry.value	- list of ids plus result id
			MatchResult possibleMatch = Matches.one().getMatch(entry.getKey());
			
			if (possibleMatch != null)
				for(List<Tile> tiles : entry.getValue()) {
					MatchResultInstance mri = new MatchResultInstance(possibleMatch, dest, tiles); 
					matches.add(mri);
					
					// keep a list of grouped matches as well
					Integer iconId = possibleMatch.getIconId();
					if (!groupedMatches.containsKey(iconId))
						groupedMatches.put(iconId, new ArrayList<MatchResultInstance>());
					groupedMatches.get(iconId).add(mri);
				}
		}
		
		// matches is either empty - no matches found
		// or contains 1 or more entries.
		
		if (!matches.isEmpty()) {
			
			transformTile = dest;
			
			if (groupedMatches.size() == 1) {
				// all selections produce the same result so pick them all
				
				Integer iconId = groupedMatches.keySet().iterator().next();
				ArrayList<MatchResultInstance> setMatches = groupedMatches.get(iconId);
				
				for(MatchResultInstance match : setMatches) {
					for(Tile selTile : match.getTiles())
						selTile.setTileOverlay(
								(selTile == match.getUpgradeTile() ? Images.ICO_A_MATCH_MAIN : Images.ICO_A_MATCH));					
				}
				matchMade(iconId, combo);
			}
			else {
				possibleMatches = matches;
				state.getAndSet(STATE_MULTIPLE_SELECTION);

				//TODO choose a group of selections
				
				// put the selected overlay over all the possible matches
				for (MatchResultInstance match : matches) {
					for(Tile t : match.getTiles()) {
						t.setTileOverlay(Images.ICO_A_MATCH);
					}
				}
				
				// well, not all of them
				dest.setTileOverlay(Images.ICO_A_MATCH_MAIN);
				
				// highlight the first match
				setCurrentMatch(getMatchForTile(dest));
				currentMouseListener = dest;
				currentMouseListener.addMouseMotionListener(this);
			}
				
		} else {
			// tile was placed with no match
			state.getAndSet(STATE_PLACE);			
		}
				
		// After the tile has been placed, we get the next
		// one ready. If we are in a combo then this has already been done
		if (combo == 1)
			setNextTile(false);	
	}

	void updateScore(int add) {
		score += add;
		//ctrlScore.setText(Integer.toString(score));
		ctrlScore.setIcon(Images.getNumber(score));
	}
	
	
	// ------------------------------------------------------------------------
	class UpgradeControl implements AnimationTimerTask.AnimationControl {

		UpgradeControl(ArrayList<MatchResultInstance> _matches, int _combo) {
			matches = _matches;
			combo = _combo;
			// all matches have the same match icon id and there is always
			// at least one entry
			matchIconId = matches.get(0).getMatchIconId();
		}
		
		public List<AnimationTimerTask.AnimatedIcon> start() {
			ArrayList<AnimationTimerTask.AnimatedIcon> icons = new ArrayList<AnimationTimerTask.AnimatedIcon>();
			
			// Create new tiles and place them on top of the existing ones
		
			for(MatchResultInstance match : matches)
				for(Tile t : match.getTiles())
					// restore original icons except for the destination one
					if (t != match.getUpgradeTile())
					{
						icons.add(new UpgradeAnimation(t.getId(), t.getX(), t.getY(), match.getUpgradeTile().getX(), match.getUpgradeTile().getY()));
						
						// this line erases icons
						t.setEmpty();
					}

			return icons;
		}
		
		public void stop() {
			
			// if we have reached the destination then stop
			transformTile.setTile(matchIconId);
			
			state.getAndSet(STATE_PLACE);
			
			// check to see if we go around again
			placeTile(transformTile, combo+1);
		}
		
		public int getZOrder() {
			return -1;
		}
		
		public int getPeriodMs() {
			return -1;
		}
		
		ArrayList<MatchResultInstance> matches;
		int combo;
		Integer matchIconId;
	}
	
	 class UpgradeAnimation implements AnimationTimerTask.AnimatedIcon {
				
		public UpgradeAnimation(int image, int _xstart, int _ystart, int _xfinal, int _yfinal) {
			lbl = new JLabel();
			
			xstart = _xstart+xTileOffset;
			ystart = _ystart+yTileOffset;
							
			xfinal = _xfinal+xTileOffset;
			yfinal = _yfinal+yTileOffset;
			
			xoff = (xfinal - xstart) / 10;
			yoff = (yfinal - ystart) / 10;
			
			xcur = xstart;
			ycur = ystart;
							
			lbl.setBounds(xstart, ystart, 60, 60);
			lbl.setIcon(Images.getIcon(image));
		}

		public JLabel getLabel() {
			return lbl;
		}

		public boolean updatePos(int frameNum) {
			if (xcur == xfinal && ycur == yfinal)
				return true;

			xcur = inc(xstart, xoff, xcur, xfinal);
			ycur = inc(ystart, yoff, ycur, yfinal);					
				
			lbl.setBounds((int)xcur, (int)ycur, 60, 60);
			return false;
		}
		
		float inc(int start, float inc, float cur, int end) {
			float tmp;
			
			if (inc > 0) {
				if (cur + inc > end)
					tmp = end;
				else
					tmp = cur + inc;
			} else {
				if (cur + inc < end)
					tmp = end;
				else
					tmp = cur + inc;
			}

			return tmp;
		}
		
		JLabel lbl;
		
		int xstart;
		int ystart;
		
		float xcur;
		float ycur;
		
		int xfinal;
		int yfinal;
		
		float xoff;
		float yoff;
	}
	 
	// ------------------------------------------------------------------------
	 
	// ------------------------------------------------------------------------
	class ExplodeControl implements AnimationTimerTask.AnimationControl {
	
		ExplodeControl(int _bombX, int _bombY) {
			bombX = _bombX;
			bombY = _bombY;
		}
		
		public List<AnimationTimerTask.AnimatedIcon> start() {
			
			icons.add(new ExplodeAnimation(grid.getAt(bombX, bombY)));
			
			if (bombX-1 >= 0) 
				icons.add(new ExplodeAnimation(grid.getAt(bombX-1, bombY)));
			if (bombX+1 < grid.width) 
				icons.add(new ExplodeAnimation(grid.getAt(bombX+1, bombY)));
				
			if (bombY-1 >= 0)
				icons.add(new ExplodeAnimation(grid.getAt(bombX, bombY-1)));			
			if (bombY+1 < grid.height)
				icons.add(new ExplodeAnimation(grid.getAt(bombX, bombY+1)));			
			
			return icons;
		}
		
		public void stop() {
  			for(AnimationTimerTask.AnimatedIcon icon : icons) {
				ExplodeAnimation e = (ExplodeAnimation) icon;
				e.tile.setEmpty();
			}

			state.getAndSet(STATE_PLACE);
			setNextTile(false);
			
			// special case, the tile the mouse is on
			// has vanished
			showPreviewTile(grid.getAt(bombX, bombY));
		}
		
		public int getZOrder() {
			return -1;
		}
		
		public int getPeriodMs() {
			return -1;
		}		
	
		ArrayList<AnimationTimerTask.AnimatedIcon> icons = new ArrayList<AnimationTimerTask.AnimatedIcon>();		
		int bombX;
		int bombY;
	}
		
	class ExplodeAnimation implements AnimationTimerTask.AnimatedIcon {
				
		public ExplodeAnimation(Tile _tile) {
			
			tile = _tile;
			lbl = new JLabel();
			
			lbl.setBounds(tile.getX()+xTileOffset, tile.getY()+yTileOffset, 60, 60);
			lbl.setIcon(Images.getIcon(Images.ICO_EXPLODE));
			
		}

		public JLabel getLabel() {
			return lbl;
		}

		public boolean updatePos(int frameNum) {
			if (frameNum > 2)
				return true;
			
			return false;
		}

		JLabel lbl;
		Tile tile;
	}		 
	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------
	class MysteryControl implements AnimationTimerTask.AnimationControl {
	
		MysteryControl(Tile _tile) {
			mysteryTile = _tile;
		}
		
		public List<AnimationTimerTask.AnimatedIcon> start() {
			
			ArrayList<AnimationTimerTask.AnimatedIcon> icons = new ArrayList<AnimationTimerTask.AnimatedIcon>();
				
			icons.add(new MysteryAnimation(mysteryTile));
			return icons;
		}
		
		public void stop() {
			
			Integer newTile = mysteryTileList.get(RandUtil.getFromRange(0, mysteryTileList.size()-1));
	
			mysteryTile.setTile(newTile);
			state.getAndSet(STATE_PLACE);
			
			// Now the mystery tile has resolved to whatever it is supposed to be,
			// we may need to resolve a match
			placeTile(mysteryTile, 0);
			
			//setNextTile();
		}
		
		public int getZOrder() {
			return -1;
		}
		
		public int getPeriodMs() {
			return -1;
		}		
		
		Tile mysteryTile;
		int bombX;
		int bombY;
	}
		
	class MysteryAnimation implements AnimationTimerTask.AnimatedIcon {
				
		public MysteryAnimation(Tile _tile) {
			
			tile = _tile;
			lbl = new JLabel();
			
			x = tile.getX()+xTileOffset;
			y = tile.getY()+yTileOffset;
			
			lbl.setBounds(x, y, 60, 60);
			lbl.setIcon(Images.getIcon(Images.ICO_MYSTERY));
			
		}

		public JLabel getLabel() {
			return lbl;
		}

		public boolean updatePos(int frameNum) {
			if (frameNum > 10)
				return true;
			
			if ((frameNum % 2) == 0)
				lbl.setBounds(x+5, y, 60, 60);
			else
				lbl.setBounds(x-5, y, 60, 60);
			
			return false;
		}

		JLabel lbl;
		Tile tile;
		int x;
		int y;
	}
		 
	// ------------------------------------------------------------------------
	static int scoreLayer = 0;
	
	class ScoreControl implements AnimationTimerTask.AnimationControl {
	
		ScoreControl(Tile tile, ArrayList<Integer> scores) {
			this.tile = tile;
			this.scores = scores;
			++scoreLayer;
			
			// Each ScoreControl has a range of 12 spaces
			myScoreLayer = 500 + (scoreLayer*12);
		}
		
		public List<AnimationTimerTask.AnimatedIcon> start() {
			
			ArrayList<AnimationTimerTask.AnimatedIcon> icons = new ArrayList<AnimationTimerTask.AnimatedIcon>();
			int delay = 0;
			
			for(Integer score : scores)			
				icons.add(new ScoreAnimation(tile, score, delay++));
			
			return icons;
		}
		
		public void stop() {
			--scoreLayer;
		}
		
		public int getZOrder() {
			// If multiple scores are displayed then make sure
			// the latest score appears on top
			return myScoreLayer++;
		}
		
		public int getPeriodMs() {
			return 100;
		}				

		Tile tile;
		ArrayList<Integer> scores;
		int myScoreLayer;
	}
		
	class ScoreAnimation implements AnimationTimerTask.AnimatedIcon {
				
		public ScoreAnimation(Tile _tile, int score, int delay) {
			
			tile = _tile;
			lbl = new JLabel();
			delayFrames = delay*5;
			
			x = tile.getX()+xTileOffset;
			y = tile.getY()+yTileOffset;
			Icon icn = Images.getNumber(score);
			icnWidth = icn.getIconWidth();
			icnHeight = icn.getIconHeight();
			
			if (delayFrames == 0)
				lbl.setBounds(x, y, icnWidth, icnHeight);
			else
				// initially do not display
				lbl.setBounds(0, 0, 0, 0);
			
			lbl.setIcon(icn);
		}

		public JLabel getLabel() {
			return lbl;
		}

		public boolean updatePos(int frameNum) {
			
			if (frameNum < delayFrames)
				return false;
			
			int apparentFrameNum = frameNum - delayFrames;
			
			if (apparentFrameNum > 12) {
				// remove icon from the layer
				lbl.setBounds(0,0,0,0);
				return true;
			}
			
			// move score upwards
			lbl.setBounds(x, y-apparentFrameNum*2, icnWidth, icnHeight);
						
			return false;
		}

		JLabel lbl;
		Tile tile;
		int x;
		int y;
		int icnWidth;
		int icnHeight;
		int delayFrames;
	}

	// no user action allowed (probably animating)
	static public Integer STATE_WAIT = 0;			
	// mouse moving into an empty square will show a highlighted icon
	// that only exists until the mouse moves out of the square
	static public Integer STATE_PLACE = 1;
	// user has clicked, placed an icon and caused a match
	static public Integer STATE_SINGLE_SELECTION = 2;
	// user has clicked, placed an icon and now must choose
	// between multiple matches
	static public Integer STATE_MULTIPLE_SELECTION = 3;	
	// No game in progress, waiting for another one to start
	static public Integer STATE_ATRACT = 4;
	
	AtomicInteger state = new AtomicInteger(STATE_PLACE);
	
	DisplayMatches displayPanel;
	
	Tile transformTile;
	int score = 0;
	
	Vector<MatchResultInstance> possibleMatches = null;
	MatchResultInstance currentlySelectedMatch = null;
	HashMap<Integer, ArrayList<MatchResultInstance>> groupedMatches = new HashMap<Integer, ArrayList<MatchResultInstance>>();	
	Tile currentMouseListener = null;
				
	NextTileList nextTileList;
	TileGrid grid = new TileGrid(6, 6);
	ArrayList<Tile> ctrlNextTiles = new ArrayList<Tile>(NUM_NEXT_TILES);
	ArrayList<Integer> mysteryTileList = new ArrayList<Integer>();
	JLabel status;
	JLabel ctrlScore = new JLabel();
	
	JButton ctrlQuit;
	JButton ctrlReset;
	static String CMD_QUIT = "QUIT";
	static String CMD_NEWGAME = "NEWGAME";
	
	
	
	// ------------------------------------------------------------------------
	// Action Listener
	public void actionPerformed(ActionEvent evnt) {
		String cmd = evnt.getActionCommand();
		if (cmd.equals(CMD_QUIT)) {
			//TODO works, but should come up with something better
			dispose();
		} else if (cmd.equals(CMD_NEWGAME)) {
			resetGame();
		}
	}

	// ------------------------------------------------------------------------
	// Mouse Listener
	
	public void mouseClicked(MouseEvent evnt) {
				
		Object src = evnt.getSource();
		if (src.getClass() == Tile.class) {
			if (state.get() == STATE_PLACE) {
				// tile has been placed. take a look to see if there was a match
				placeTile((Tile) src, 1);
			} else if (state.get() == STATE_MULTIPLE_SELECTION) {
				if (currentlySelectedMatch != null) {
					
					// Remove all overlays
					for (MatchResultInstance match : possibleMatches) {
						for(Tile t : match.getTiles()) {
							t.removeOverlay();
						}
					}
					
					matchMade(currentlySelectedMatch, 2);
					
					currentMouseListener.removeMouseMotionListener(this);
					currentMouseListener = null;					
					
					possibleMatches = null;
					state.set(STATE_PLACE);
				}
			}
		}
	}

	public void mouseEntered(MouseEvent evnt) {
		Object src = evnt.getSource();
		if (src.getClass() == Tile.class) {
			Tile tile = (Tile) src;
			if (state.get() == STATE_PLACE) {
				showPreviewTile(tile);
				
			} else if (state.get() == STATE_MULTIPLE_SELECTION) {

				// Have we entered a tile that is part of the 
				// possible match set?
				MatchResultInstance match = getMatchForTile(tile);
				
				if (match != null) {
					
					if (match.getUpgradeTile() == tile) {
						currentMouseListener = tile;
						currentMouseListener.addMouseMotionListener(this);
					}
					
					// Yes we have, so unselect the old one
					// and select the new one
					unSelectCurrentMatch();
					setCurrentMatch(match);
				}
			}
		}
	}

	public void mouseExited(MouseEvent evnt) {
		Object src = evnt.getSource();
		if (src.getClass() == Tile.class) {
			Tile tile = (Tile) src;
			
			if (state.get() == STATE_PLACE) {			
				tile.removeOverlay();				
			} else if (state.get() == STATE_MULTIPLE_SELECTION) {
				currentMouseListener.removeMouseMotionListener(this);
			}
		}
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}
	
	
	// ------------------------------------------------------------------------
	// MouseMotionListener

	public void mouseDragged(MouseEvent evnt) {
	}

	public void mouseMoved(MouseEvent evnt) {
		// not used yet - may not
		//System.out.println("(" + evnt.getX() + "," + evnt.getY() + ")");
	}
	
	static JDialog s_splashDialog = null;
	
	/**
	 * @param args
	 */
	@SuppressWarnings("serial")
	public static void main(String[] args) {
		/*
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	*/		
		try {
			
			final BufferedImage img = ImageUtil.loadImage("graphics/SplashScreen.png");
			
			s_splashDialog = new JDialog() {
	
	            @Override
	            public void paint(Graphics g) {
	                g.drawImage(img, 0, 0, null);
	            }
	        };
	        // use the same size as your image
	        s_splashDialog.setPreferredSize(new Dimension(img.getWidth(), img.getHeight())); 
	        s_splashDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	        s_splashDialog.setUndecorated(true);
	        s_splashDialog.pack();
	        s_splashDialog.setLocationRelativeTo(null);
	        s_splashDialog.setVisible(true);
	        s_splashDialog.repaint();
		} catch(IOException ex) {
			System.out.println("Unable to load splash screen " + ex);
		}
    	
	
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {            	
        		TripleThing tt = new TripleThing();
        		Matches.one().init();
        		Images.init(tt.getGraphicsConfiguration());
        		tt.init();
        		tt.setLocationRelativeTo(null);
        		s_splashDialog.dispose();
        		tt.setVisible(true);
              }
        });
	}

}
