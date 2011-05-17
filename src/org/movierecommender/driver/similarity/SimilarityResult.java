package org.movierecommender.driver.similarity;

import org.movierecommender.model.User;

public class SimilarityResult implements Comparable<SimilarityResult> {

	public User getOther() {
		return other;
	}

	public User getMain() {
		return main;
	}

	private final double value;
	private final User other;
	private final User main;

	public SimilarityResult(User main, User other, double value) {
		this.main = main;
		this.other = other;
		this.value = value;
	}

	@Override
	public int compareTo(SimilarityResult o) {
		return (this.value>o.value)?1:-1;
	}

}
