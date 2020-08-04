package ds.triplething.match;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import ds.lib.algo.HeapSort;
import ds.triplething.Images;
import ds.triplething.Tile;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class DisplayMatches extends JPanel implements ActionListener {

	/*
	 * JPanel contains 
	 * 2 buttons ('Full List', 'Hint List')
	 * Scrollable Panel, fixed width.
	 * 
	 * Hint List is recalculated based upon next item
	 * 
	 * JPanel DisplayMatches
	 * 		JButton 'full'
	 * 		JButton 'hint'
	 * 		JScrollPane scrollPane		needs to be a fixed size on screen
	 * 			JPanel displayPanel		
	 * 				JLabel shows icons in a grid.  the grid size changes dynamically
	 * 
	 */
	
	static String BUTTON_FULL = new String("BTN_FULL");
	static String BUTTON_HINT = new String("BTN_HINT");
	
	static int MODE_FULL = 0;
	static int MODE_HINT = 1;
	int mode = MODE_FULL;
	
	JPanel displayPanel;
	JScrollPane scrollPane;
	ArrayList<MatchRow> matchRows = new ArrayList<MatchRow>();
	ArrayList<JLabel> icons = new ArrayList<JLabel>();
	
	int sortCol = 0;
	Tile nextTile;
	int panelCol = 1;
	int panelRow = 0;
	Integer searchId = 0;
	
	
	public DisplayMatches(Tile nextTile) {
		
		this.nextTile = nextTile;
		
		setLayout(new MigLayout("filly","[]0[]","[]0[]"));
		
		JButton btnFull = new JButton();
		btnFull.setText("Full");
		btnFull.setActionCommand(BUTTON_FULL);
		btnFull.addActionListener(this);
		add(btnFull);
		
		JButton btnHint = new JButton();
		btnHint.setText("Hint");
		btnHint.setActionCommand(BUTTON_HINT);
		btnHint.addActionListener(this);
		add(btnHint, "wrap");
		
		displayPanel = new JPanel();
		displayPanel.setLayout(new MigLayout());
		//displayPanel.setWidth(150);
		scrollPane = new JScrollPane(displayPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane, "spanx,growy");
		
		panelCol = 0;
		panelRow = 0;
		
		for(String key : Matches.one().matches.keySet()) {
			
			MatchResult res = Matches.one().matches.get(key);
			
			MatchRow row = new MatchRow(res.getIconId(), key);
			
			if (row.size() > panelCol)
				panelCol = row.size();
		
			matchRows.add(row);
			++panelRow;
		}
		
		panelCol += 1;
		
		for(int y=0; y<panelRow; ++y)
			for(int x=1; x<=panelCol; ++x) {
				JLabel l = new JLabel(Images.getSmallIcon(Images.ICO_BLANK));
				icons.add(l);
				if (x == panelCol)
					displayPanel.add(l, "wrap");
				else
					displayPanel.add(l);
			}
		
//		displayPanel.setSize(Images.getSmallTileSizeX() * panelCol, 
//				Images.getSmallTileSizeY() * panelRow);
		
		updateFullList();		
	}
	
	public void setHeight(int height) {
		Dimension dim = displayPanel.getPreferredSize();
		dim.height = height;
		//dim.width = Images.getSmallTileSizeX() * panelCol;
		//FIXME hardcoded ???
		dim.width  = 300;
		setPreferredSize(dim);
	}
	
	public void updateFullList() {
				
		sortCol = 0;
		Comparable<?>[] ar = matchRows.toArray(new Comparable[0]);
		HeapSort.execute(ar);
		
		addRowsToDisplay(ar);
	}
	
	public void updateHintList(Integer searchId) {
		this.searchId = searchId;
		updateHintList();
	}
	
	public void updateHintList() {
		
		if (mode != MODE_HINT)
			return;
						
		// Only select rows that have searchid in them

		ArrayList<MatchRow> sliceRows = new ArrayList<MatchRow>();
		
		for(MatchRow row : matchRows) {
			for(Integer id : row.data) {
				if (searchId.equals(id)) {
					sliceRows.add(row);
					break;
				}
			}
		}
		
		if (sliceRows.size() ==0)
			return;

		// might as well sort
		sortCol = 0;
		Comparable<?>[] ar = sliceRows.toArray(new Comparable[0]);
		//HeapSort.execute(ar);
		
		addRowsToDisplay(ar);
	}
	
	void addRowsToDisplay(Comparable<?>[] rows) {
		
		int y=0, pos=0;
		for(y=0; y<rows.length; ++y) {
			MatchRow row = (MatchRow) rows[y];
			
			pos = y*panelCol;
			icons.get(pos).setIcon(Images.getSmallIcon(row.data.get(0)));
			icons.get(++pos).setIcon(Images.getSmallIcon(Images.ICO_A_EQUALS));
			
			int a=1;
			for(a=1; a<row.data.size(); ++a)
				icons.get(++pos).setIcon(Images.getSmallIcon(row.data.get(a)));
			
			for(int c=a; c<(panelCol-1); ++c)
				icons.get(++pos).setIcon(Images.getSmallIcon(Images.ICO_BLANK));
		}

		for(int c=y; c<panelRow; ++c)
			for(int a=0; a<panelCol; ++a) {
				icons.get(++pos).setIcon(Images.getSmallIcon(Images.ICO_BLANK));
			}
	}

	// ------------------------------------------------------------------------
	// ActionListener
	
	public void actionPerformed(ActionEvent evnt) {
		
		if (BUTTON_FULL.equals(evnt.getActionCommand())) {
			if (mode != MODE_FULL) {
				mode = MODE_FULL;
				updateFullList();
			}
		} else if (BUTTON_HINT.equals(evnt.getActionCommand())) {
			if (mode != MODE_HINT) {
				mode = MODE_HINT;
				updateHintList();
			}
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	class MatchRow implements Comparable<MatchRow> {
		
		public MatchRow(Integer res, String key) {
			data.add(res);
			
			for(String id : key.split("_")) {
				Integer iid = Integer.parseInt(id);
				data.add(iid);
			}
			
		}
		
		public int size() {
			return data.size();
		}
		
		// -1	this < rhs
		// 0	this == rhs
		// 1	this > rhs
		public int compareTo(MatchRow rhs) {
			return data.get(sortCol).compareTo(rhs.data.get(sortCol));
		}

		List<Integer> data = new ArrayList<Integer>();
	}
	
}
