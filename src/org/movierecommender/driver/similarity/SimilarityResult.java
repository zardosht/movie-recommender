package org.movierecommender.driver.similarity;

import org.movierecommender.model.User;

public class SimilarityResult {

	private final double value;
	private final User other;
	private final User main;

	public SimilarityResult(User main, User other, double value) {
		this.main = main;
		this.other = other;
		this.value = value;
	}

}
