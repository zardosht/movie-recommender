package org.movierecommender.driver.similarity;

import java.util.List;

import org.movierecommender.model.Item;
import org.movierecommender.model.User;

public interface SimilarityStrategy {
	
		SimilarityResult calculateSimilarty(User main, User other, List<Item> ignoreItems);
		
}
