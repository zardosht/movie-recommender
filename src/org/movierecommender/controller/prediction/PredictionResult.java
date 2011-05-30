package org.movierecommender.controller.prediction;

import org.movierecommender.model.Item;
import org.movierecommender.model.User;

public class PredictionResult implements Comparable<PredictionResult> {

	private final User user;

	private final Item item;
	private final double value;

	public PredictionResult(User user, Item item, double value) {
		this.user = user;
		this.item = item;
		this.value = value;
	}

	@Override
	public int compareTo(PredictionResult o) {
		return (this.getValue() > o.getValue()) ? -1 : 1;
	}

	@Override
	public String toString() {
		return user + " predicted rating for " + item + " is " + getValue();
	}

	public double getValue() {
		return value;
	}

	public boolean isInvalid() {
		return !(1 <= value && value <= 5) || ((Double)value).isNaN() || ((Double)value).isInfinite();
	}

	public Item getItem() {
		return item;
	}

	public User getUser() {
		return user;
	}
}
