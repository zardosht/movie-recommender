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
		// ANSWER: Yes! See answer on
		// PearsonCorrelationStrategy.calculateSimilarty

		double sum = 0;
		double weigehtedSum = 0;
		for (SimilarityResult simResult : similarity) {
			if (!simResult.getOther().hasRated(item)) {
				continue;
			}
			sum += simResult.getValue();
			weigehtedSum += simResult.getValue()
					* (simResult.getOther().getRatings().get(item) - getAverage(simResult
							.getOther()));
		}
		return new PredictionResult(main, item, getAverage(main)
				+ (weigehtedSum / sum));
	}

	private int getAverage(User user) {
		int sum = 0;
		int n = 0;
		for (Integer rating : user.getRatings().values()) {
			sum += rating;
			n++;
		}
		return sum / n;
	}

}
