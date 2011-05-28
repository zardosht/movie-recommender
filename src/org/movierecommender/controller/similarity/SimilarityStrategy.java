package org.movierecommender.controller.similarity;

import java.util.List;

import org.movierecommender.model.Item;
import org.movierecommender.model.User;

public interface SimilarityStrategy {
	
	/**
	 * 
	 * @param main
	 * @param other
	 * @param ignoreItems
	 * @return
	 */
		SimilarityResult calculateSimilarty(User main, User other, List<Item> ignoreItems);
		
}
