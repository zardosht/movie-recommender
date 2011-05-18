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
		// vektor zum quadrat? Die formell ist wohl falsh?!
		

		double value = 0;
		for (Item item : matrix.getItems()) {
			Integer u1Rating = u1.getRatings().get(item);
			// TODO: how should we go if the u1 has not evaluated an item, and
			// u2 has evaluated it? Should this item also be considered in the
			// computation? as 0?
			if (u1Rating == null) {
				u1Rating = 0;
			}
			Integer u2Rating = u2.getRatings().get(item);
			if (u2Rating == null) {
				u2Rating = 0;
			}
			value += Math.pow(u1Rating - u2Rating, 2);
		}
		// TODO: ACHTUNG: in this implementation, if both u1 and u2 has not
		// rated any item, then they will be considered completely similar.
		return new SimilarityResult(u1, u2, value / matrix.getItems().size());
	}

}
