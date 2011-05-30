package org.movierecommender.controller.prediction;

import java.util.List;

import org.movierecommender.controller.similarity.SimilarityResult;
import org.movierecommender.model.Item;
import org.movierecommender.model.User;

public class WeightedPredictor implements RatingPredictor {

	@Override
	public PredictionResult predictRating(User main, Item item,
			List<SimilarityResult> similarity) {
		

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
		//TODO DBZ and -1
		double value = getAverage(main)	+ (weigehtedSum / sum);
		return new PredictionResult(main, item, value);
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
