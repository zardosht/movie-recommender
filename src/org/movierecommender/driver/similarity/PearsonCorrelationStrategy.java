package org.movierecommender.driver.similarity;

import org.movierecommender.model.User;

public class PearsonCorrelationStrategy implements SimilarityStrategy {

	@Override
	public SimilarityResult calculateSimilarty(User main, User other) {
		// TODO How should (r bar von x) und (r bar von y) berechnet werden?
		// should it be like averaging over all items rated by them? but in this
		// case how does this average exactly relate to specific item s?
		// ANSWER: Yes it must be average over all ratings of user x and y. 
		// Although it does not directly relate to item s, but it indicates the rating behaviour 
		// of user x and y (ob er alle hoch rated, oder niedrig)
		return null;
	}

}
