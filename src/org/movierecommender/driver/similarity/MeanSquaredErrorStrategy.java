package org.movierecommender.driver.similarity;

import org.movierecommender.model.Item;
import org.movierecommender.model.User;
import org.movierecommender.model.UserItemMatrix;

public class MeanSquaredErrorStrategy implements SimilarityStrategy {

	private final UserItemMatrix matrix;

	public MeanSquaredErrorStrategy(UserItemMatrix matrix) {
		this.matrix = matrix;
	}

	@Override
	public SimilarityResult calculateSimilarty(User u1, User u2) {
		// TODO: schick email regarding formula an seite 15. Was bedeuted ein
		// vektor zum quadrat?
		double value = 0;
		for (Item item : matrix.getItems()) {
			Integer u1Rating = u1.getRatings().get(item);
			if (u1Rating == null) {
				u1Rating = 0;
			}
			Integer u2Rating = u2.getRatings().get(item);
			if (u2Rating == null) {
				u2Rating = 0;
			}
			value += Math.pow(u1Rating - u2Rating, 2);
		}

		return new SimilarityResult(u1, u2, value / matrix.getItems().size());
	}

}
