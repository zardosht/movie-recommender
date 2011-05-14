package org.movierecommender.model;

public class Item {

	private final int itemId;

	public Item(int itemId) {
		this.itemId = itemId;
	}

	public int getItemId() {
		return itemId;
	}
	
	@Override
	public String toString() {
		return "Item: "+itemId;
	}
}