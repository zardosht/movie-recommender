package org.movierecommender.driver.similarity;

import org.movierecommender.model.User;

public class PearsonCorrelationStrategy implements SimilarityStrategy {

	@Override
	public SimilarityResult calculateSimilarty(User main, User other) {
		// TODO How should (r bar von x) und (r bar von y) berechnet werden?
		// should it be like averaging over all items rated by them? but in this
		// case how does this average exactly relate to specific item s?
		return null;
	}

}
