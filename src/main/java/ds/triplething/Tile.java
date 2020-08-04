package ds.triplething;

import javax.swing.JLabel;

public class Tile extends JLabel {

	private static final long serialVersionUID = 1L;

	public Tile(Integer id, int xpos, int ypos) {
		setTile(id);
		this.xpos = xpos;
		this.ypos = ypos;
	}
	
	public void setEmpty() {
		id = Images.ICO_BLANK;
		setIcon(Images.getIcon(id));

		repaint();
	}
	
	public void setTile(Integer id) {
		this.id = id;
		setIcon(Images.getIcon(id));
	}
	
	public void setTile(Tile rhs) {
		id = rhs.id;
		setIcon(rhs.getIcon());
	}
		
	public void setTileOverlay(Integer ... overlays) {
		setIcon(Images.getIconWithOverlay(id, overlays, true));
	}
	
	// used in conjunction with reset() to display a preview
	// that is different from the actual id
	public void setPreviewTile(Integer id) {
		setIcon(Images.getHighlightIcon(id));
	}
	
	public void removeOverlay() {
		setTile(id);
	}
	
	public Integer getId() {
		return id;
	}
	
	public int gridX() {
		return xpos;
	}
	
	public int gridY() {
		return ypos;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(xpos);
		sb.append(",");
		sb.append(ypos);
		sb.append(")-");
		sb.append(id);
		return sb.toString();
	}
	
	Integer id;
	int xpos;
	int ypos;
}

/*
public class Tile extends Canvas {
	
	public Tile() {
	}
	
	public void paint(Graphics g) {
		ico.paintIcon(this, g, 0, 0);
	}
	
	public Tile(Integer id, int xpos, int ypos) {
		setTile(id);
		this.xpos = xpos;
		this.ypos = ypos;
	}
	
	public void setEmpty() {
		id = Images.ICO_BLANK;
		ico = Images.getIcon(id);

		repaint();
	}
	
	public void setTile(Integer id) {
		this.id = id;
		//setIcon(Images.getIcon(id));
	}
	
	public void setTile(Tile rhs) {
		id = rhs.id;
		ico = rhs.ico;
	}
		
	public void setTileOverlay(Integer ... overlays) {
		ico = Images.getIconWithOverlay(id, overlays, true);
	}
	
	// used in conjunction with reset() to display a preview
	// that is different from the actual id
	public void setPreviewTile(Integer id) {
		ico = Images.getHighlightIcon(id);
	}
	
	public void removeOverlay() {
		setTile(id);
	}
	
	public Integer getId() {
		return id;
	}
	
	public int gridX() {
		return xpos;
	}
	
	public int gridY() {
		return ypos;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(xpos);
		sb.append(",");
		sb.append(ypos);
		sb.append(")-");
		sb.append(id);
		return sb.toString();
	}
	
	Icon ico = null;
	Integer id;
	int xpos;
	int ypos;

}
*/
