package org.movierecommender.driver.similarity;

import org.movierecommender.model.User;

public interface SimilarityStrategy {
	
		SimilarityResult calculateSimilarty(User main, User other);
		
}
