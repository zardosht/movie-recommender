package org.movierecommender.model;

import java.util.ArrayList;
import java.util.List;


public class UserItemMatrix {

	private List<User> users;
	private List<Item> items;

	public UserItemMatrix() {
		users = new ArrayList<User>();
		items = new ArrayList<Item>();
	}

	public void addEntry(int userId, int itemId, int rating) {
		User user = getUserByID(userId);
		if (user == null) {
			user = new User(userId);
			users.add(user);
		}

		Item item = getItemByID(itemId);
		if (item == null) {
			item = new Item(itemId);
			items.add(item);
		}
		user.addRating(item, rating);
//		System.out.println("Add new data set: User: " + userId
//				+ " Item: " + itemId + " Rating: " + rating);
	}

	public User getUserByID(int userId) {
		for (User tmp : users) {
			if (userId == tmp.getUserId()) {
				return tmp;
			}
		}
		return null;
	}

	public Item getItemByID(int itemId) {
		for (Item tmp : items) {
			if (itemId == tmp.getItemId()) {
				return tmp;
			}
		}
		return null;
	}

	public List<User> getUsers() {
		return users;
	}

	public List<Item> getItems() {
		return items;
	}
}
