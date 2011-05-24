package org.movierecommender.driver.prediction;

import java.util.List;

import org.movierecommender.driver.similarity.SimilarityResult;
import org.movierecommender.model.Item;
import org.movierecommender.model.User;

public class WeightedPredictor implements RatingPredictor {

	@Override
	public PredictionResult predictRating(User main, Item item,
			List<SimilarityResult> similarity) {
		// TODO: In page 21 how should (s bar) be calculated? is it the average
		// over all ratings of user s? but again how how does this average
		// exactly relate to specific item i (see question on
		// PearsonCorrelationStrategy.calculateSimilarty)?
		// ANSWER: Yes! See answer on PearsonCorrelationStrategy.calculateSimilarty
		return null;
	}

}
