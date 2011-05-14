package org.movierecommender.model;

import java.util.HashMap;
import java.util.Map;

public class User {

	// item -> rating
	private Map<Item, Integer> itemRating;
	private final int userId;

	public User(int userId) {
		this.userId = userId;
		itemRating = new HashMap<Item, Integer>();
	}

	public void addRating(Item item, int rating) {
		itemRating.put(item, rating);
	}

	public int getUserId() {
		return userId;
	}
	
	@Override
	public String toString() {
		return "User: "+userId;
	}

}