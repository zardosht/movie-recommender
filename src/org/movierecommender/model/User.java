package org.movierecommender.model;

import java.util.HashMap;
import java.util.Map;

public class User {

	// item -> rating
	private Map<Item, Integer> ratings;
	private final int userId;

	public User(int userId) {
		this.userId = userId;
		ratings = new HashMap<Item, Integer>();
	}

	public void addRating(Item item, int rating) {
		ratings.put(item, rating);
	}

	public int getUserId() {
		return userId;
	}
	
	@Override
	public String toString() {
		return "User: "+userId;
	}

	public boolean hasRated(Item item) {
		return ratings.get(item)!=null;
	}

	public Map<Item, Integer> getRatings() {
		return ratings;
	}
	
	
	public void unrate(Item item){
		ratings.put(item, null);
	}

}