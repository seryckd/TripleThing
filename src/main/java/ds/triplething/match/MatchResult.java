package ds.triplething.match;

public class MatchResult {
	
	public MatchResult(Integer iconId, int score) {
		this.iconId = iconId;
		this.score = score;
	}
	
	public Integer getIconId() {
		return iconId;
	}
	
	public int getScore() {
		return score;
	}
	
	Integer iconId;
	int score;

}
