package ds.triplething.match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ds.lib.algo.CalculatePermutations;
import ds.triplething.Images;

public class Matches {

	public static Matches one() {
		if (one == null)
			one = new Matches();
		return one;
	}
	
	static Matches one = null;
	
	private Matches() {
		
		ArrayList<Integer> idxs = new ArrayList<Integer>();
		
		// use 0 based indexing
		
		idxs.add(0);		
		permutations.put(1, CalculatePermutations.calculate(idxs));
		
		idxs.add(1);
		permutations.put(2, CalculatePermutations.calculate(idxs));
		
		idxs.add(2);
		permutations.put(3, CalculatePermutations.calculate(idxs));
		
		idxs.add(3);
		permutations.put(4, CalculatePermutations.calculate(idxs));
		
		idxs.add(4);
		permutations.put(5, CalculatePermutations.calculate(idxs));
	}
	
	/**
	 * grass
	 * tree1,2
	 * bush
	 * factory1,2,3
	 * store1,2,3,4
	 * rock
	 * house1,2,3
	 * 
	 * mystery - when placed turns into random
	 * 
	 */
	
	public void init() {
		
		// starting set
		// define the starting set in TripleThing

		addAllMatches(new MatchResult(Images.ICO_BUSH, 100), Images.ICO_GRASS, Images.ICO_GRASS, Images.ICO_GRASS);
		
		addAllMatches(new MatchResult(Images.ICO_TREE1, 100), Images.ICO_BUSH, Images.ICO_GRASS, Images.ICO_GRASS);
		addAllMatches(new MatchResult(Images.ICO_TREE1, 100), Images.ICO_BUSH, Images.ICO_BUSH, Images.ICO_GRASS);
		addAllMatches(new MatchResult(Images.ICO_TREE1, 300), Images.ICO_BUSH, Images.ICO_BUSH, Images.ICO_BUSH);

		addAllMatches(new MatchResult(Images.ICO_FACTORY1, 300), Images.ICO_ROCK, Images.ICO_ROCK, Images.ICO_ROCK);
		addAllMatches(new MatchResult(Images.ICO_FACTORY2, 400), Images.ICO_FACTORY1, Images.ICO_ROCK, Images.ICO_ROCK);
		addAllMatches(new MatchResult(Images.ICO_FACTORY2, 400), Images.ICO_FACTORY1, Images.ICO_FACTORY1, Images.ICO_ROCK);
		addAllMatches(new MatchResult(Images.ICO_FACTORY2, 500), Images.ICO_FACTORY1, Images.ICO_FACTORY1, Images.ICO_FACTORY1);
		addAllMatches(new MatchResult(Images.ICO_FACTORY3, 800), Images.ICO_FACTORY2, Images.ICO_ROCK, Images.ICO_ROCK);
		addAllMatches(new MatchResult(Images.ICO_FACTORY3, 800), Images.ICO_FACTORY2, Images.ICO_FACTORY2, Images.ICO_ROCK);	
		addAllMatches(new MatchResult(Images.ICO_FACTORY3, 1500), Images.ICO_FACTORY2, Images.ICO_FACTORY2, Images.ICO_FACTORY2);

		addAllMatches(new MatchResult(Images.ICO_HOUSE1, 300), Images.ICO_TREE1, Images.ICO_TREE1, Images.ICO_TREE1);
		addAllMatches(new MatchResult(Images.ICO_HOUSE2, 400), Images.ICO_HOUSE1, Images.ICO_TREE1, Images.ICO_TREE1);
		addAllMatches(new MatchResult(Images.ICO_HOUSE2, 400), Images.ICO_HOUSE1, Images.ICO_HOUSE1, Images.ICO_TREE1);
		addAllMatches(new MatchResult(Images.ICO_HOUSE2, 500), Images.ICO_HOUSE1, Images.ICO_HOUSE1, Images.ICO_HOUSE1);
		addAllMatches(new MatchResult(Images.ICO_HOUSE3, 800), Images.ICO_HOUSE2, Images.ICO_TREE1, Images.ICO_TREE1);
		addAllMatches(new MatchResult(Images.ICO_HOUSE3, 800), Images.ICO_HOUSE2, Images.ICO_HOUSE2, Images.ICO_TREE1);
		addAllMatches(new MatchResult(Images.ICO_HOUSE3, 1500), Images.ICO_HOUSE2, Images.ICO_HOUSE2, Images.ICO_HOUSE2);

		addAllMatches(new MatchResult(Images.ICO_STORE1, 300), Images.ICO_ROCK, Images.ICO_ROCK, Images.ICO_TREE1);
		addAllMatches(new MatchResult(Images.ICO_STORE1, 300), Images.ICO_ROCK, Images.ICO_TREE1, Images.ICO_TREE1);
		
		addAllMatches(new MatchResult(Images.ICO_STORE2, 200), Images.ICO_STORE1, Images.ICO_TREE1, Images.ICO_TREE1);
		addAllMatches(new MatchResult(Images.ICO_STORE2, 300), Images.ICO_STORE1, Images.ICO_ROCK, Images.ICO_ROCK);
		addAllMatches(new MatchResult(Images.ICO_STORE2, 300), Images.ICO_STORE1, Images.ICO_ROCK, Images.ICO_TREE1);		
		addAllMatches(new MatchResult(Images.ICO_STORE2, 800), Images.ICO_STORE1, Images.ICO_STORE1, Images.ICO_STORE1);

		addAllMatches(new MatchResult(Images.ICO_STORE3, 800), Images.ICO_STORE2, Images.ICO_STORE2, Images.ICO_TREE1);
		addAllMatches(new MatchResult(Images.ICO_STORE3, 800), Images.ICO_STORE2, Images.ICO_STORE2, Images.ICO_ROCK);
		addAllMatches(new MatchResult(Images.ICO_STORE3, 800), Images.ICO_STORE2, Images.ICO_STORE2, Images.ICO_STORE1);
		addAllMatches(new MatchResult(Images.ICO_STORE3, 1500), Images.ICO_STORE2, Images.ICO_STORE2, Images.ICO_STORE2);
				
		addAllMatches(new MatchResult(Images.ICO_STORE4, 1500), Images.ICO_STORE3, Images.ICO_STORE3, Images.ICO_ROCK);
		addAllMatches(new MatchResult(Images.ICO_STORE4, 1500), Images.ICO_STORE3, Images.ICO_STORE3, Images.ICO_TREE1);	
		addAllMatches(new MatchResult(Images.ICO_STORE4, 1500), Images.ICO_STORE3, Images.ICO_STORE3, Images.ICO_STORE2);	
		addAllMatches(new MatchResult(Images.ICO_STORE4, 2000), Images.ICO_STORE3, Images.ICO_STORE3, Images.ICO_STORE3);
		
				
		// output matches
		//for(String k : matches.keySet())
		//	System.out.println(k);
	}
	
	int addAllMatches(MatchResult result, Integer... ids) {
		if (ids.length == 0)
			return 0;
		
		int numAdded = 0;
		
		// work out match key from this permutation
		// i.e. same match key for all permutations
		List<List<Integer>> perms = permutations.get(ids.length);
		
		for(List<Integer> idxs : perms) {
			StringBuilder matchKey = new StringBuilder();

			for(int c=0; c<idxs.size(); ++c) {
				Integer idx = idxs.get(c);
			
				if (c > 0)
					matchKey.append("_");
				
				matchKey.append(ids[idx]);
			}
			String permKey = matchKey.toString();
			
			if (!matches.containsKey(permKey)) {
				matches.put(permKey, result);
				++numAdded;
			}
		}
		
		return numAdded;
	}	
	
	// returns null if no match
	public MatchResult getMatch(String key) {
		return matches.get(key);
	}
	
	public void addMatchListener(MatchListener l) {
		listeners.add(l);
	}
		
	static HashMap<Integer, List<List<Integer>>> permutations = new HashMap<Integer, List<List<Integer>>>();
	
	ArrayList<MatchListener> listeners = new ArrayList<MatchListener>();
	HashMap<String, MatchResult> matches = new HashMap<String, MatchResult>();
}
