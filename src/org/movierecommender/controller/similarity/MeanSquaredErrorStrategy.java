package org.movierecommender.controller.similarity;

import java.util.List;

import org.movierecommender.model.Item;
import org.movierecommender.model.User;

public class MeanSquaredErrorStrategy implements SimilarityStrategy {

	@Override
	public SimilarityResult calculateSimilarty(User u1, User u2, List<Item> ignoreItems) {
		// TODO: schick email regarding formula an seite 15. Was bedeuted ein
		// vektor zum quadrat? Die formell ist wohl falsh?!
		

		double value = 0;
		int rating = 0;
		for (Item item : u1.getRatings().keySet()) {
			Integer u1Rating = u1.getRatings().get(item);
			Integer u2Rating = u2.getRatings().get(item);
			// TODO: how should we go if the u1 has not evaluated an item, and
			// u2 has evaluated it? Should this item also be considered in the
			// computation? as 0?
			// answer: we only consider cases with ratings on both sides
			if(u1Rating == null || u2Rating == null || ignoreItems.contains(item)) {
				continue;
			}
			value += Math.pow(u1Rating - u2Rating, 2);
			rating++;
		}
		//TODO: make DBZ safe
		return new SimilarityResult(u1, u2, value / rating);
	}

}
