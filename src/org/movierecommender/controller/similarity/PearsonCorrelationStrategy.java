package org.movierecommender.controller.similarity;

import java.util.List;

import org.movierecommender.model.Item;
import org.movierecommender.model.User;

public class PearsonCorrelationStrategy implements SimilarityStrategy {
	
	@Override
	public SimilarityResult calculateSimilarty(User main, User other, List<Item> ignoreItems) {

		int avgMain = getAverage(main);
		int avgOther = getAverage(other);
		
		int nominator = 0;
		int divisor1 = 0;
		int divisor2 = 0;
		
		for (Item item : main.getRatings().keySet()) {
			Integer u1Rating = main.getRatings().get(item);
			Integer u2Rating = other.getRatings().get(item);
			if(u1Rating == null || u2Rating == null || ignoreItems.contains(item)) {
				continue;
			}
			int differnceMain = u1Rating-avgMain; 
			int differnceOther = u2Rating-avgOther;
			
			nominator += differnceMain*differnceOther;
			divisor1 += differnceMain*differnceMain;
			divisor2 += differnceOther*differnceOther;
		}
		
		return new SimilarityResult(main, other, nominator/Math.sqrt(divisor1*divisor2), false);
	}

	private int getAverage(User user) {
		int sum = 0;
		int n = 0;
		for(Integer rating : user.getRatings().values()) {
			sum += rating;
			n ++;
		}
		return sum/n;
	}

}
