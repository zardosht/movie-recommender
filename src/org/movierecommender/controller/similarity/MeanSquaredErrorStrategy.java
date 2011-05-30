package org.movierecommender.controller.similarity;

import java.util.List;

import org.movierecommender.model.Item;
import org.movierecommender.model.User;

public class MeanSquaredErrorStrategy implements SimilarityStrategy {

	@Override
	public SimilarityResult calculateSimilarty(User u1, User u2, List<Item> ignoreItems) {

		double value = 0;
		int rating = 0;
		for (Item item : u1.getRatings().keySet()) {
			Integer u1Rating = u1.getRatings().get(item);
			Integer u2Rating = u2.getRatings().get(item);
			if(u1Rating == null || u2Rating == null || ignoreItems.contains(item)) {
				continue;
			}
			value += Math.pow(u1Rating - u2Rating, 2);
			rating++;
		}
		//TODO: make DBZ safe
		return new SimilarityResult(u1, u2, value / rating, true);
	}

}
