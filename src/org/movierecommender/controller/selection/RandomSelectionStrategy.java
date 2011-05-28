package org.movierecommender.controller.selection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.movierecommender.controller.prediction.PredictionResult;

public class RandomSelectionStrategy implements SelectionStrategy {

	@Override
	public List<PredictionResult> selectFavorites(List<PredictionResult> allFavorites, int count) {
		Random random = new Random();
		if(allFavorites.size() < count){
			return allFavorites;
		}else{
			Set<PredictionResult> result = new HashSet<PredictionResult>();
			while(result.size() < count){
				result.add(allFavorites.get(random.nextInt(allFavorites.size())));
			}
			return new ArrayList<PredictionResult>(result);
		}
	}

}
