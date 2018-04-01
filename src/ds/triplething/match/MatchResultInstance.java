package ds.triplething.match;

import java.util.List;

import ds.triplething.Tile;

public class MatchResultInstance {
	
	// the 3 tiles that make a match
	// which one of the tiles will change
	// the icon it will change to and the points

	public MatchResultInstance(MatchResult matchResult, Tile upgradeTile, List<Tile> tiles) {
		this.tiles = tiles;
		this.matchResult = matchResult;
		this.upgradeTile = upgradeTile;
	}
	
	public Integer getMatchIconId() {
		return matchResult.getIconId();
	}
	
	public Tile getUpgradeTile() {
		return upgradeTile;
	}
	
	public int getScore() {
		return matchResult.getScore();
	}
	
	public List<Tile> getTiles() {
		return tiles;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(matchResult.getScore() + " " + matchResult.getIconId() + "=" + tiles);
		return sb.toString();
	}
	
	Tile upgradeTile;
	List<Tile> tiles;
	MatchResult matchResult;
}
