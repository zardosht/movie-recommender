package org.movierecommender.driver.prediction;

import java.util.List;

import org.movierecommender.driver.similarity.SimilarityResult;
import org.movierecommender.model.Item;
import org.movierecommender.model.User;

public class MeanPredictor implements RatingPredictor {

	
	
	@Override
	public PredictionResult predictRating(User main, Item item,
			List<SimilarityResult> similarity) {

		int sumSim = 0;
		int numOtherRatings = 0;
		
		for(SimilarityResult simRes : similarity){
			User other = simRes.getOther();
			Integer otherRating = other.getRatings().get(item);
			if(otherRating != null){
				sumSim += otherRating;
				numOtherRatings++;
			}
		}
		
		int value = numOtherRatings == 0 ? -1 : sumSim / numOtherRatings;
		// TODO: make DBZ safe (see MeanSquaredErrorStrategy.calculateSimilarty)
		return new PredictionResult(main, item, value);
	}

}
