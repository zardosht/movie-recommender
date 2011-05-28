package org.movierecommender.controller.selection;

import java.util.List;

import org.movierecommender.controller.prediction.PredictionResult;

public interface SelectionStrategy {
	
	List<PredictionResult> selectFavorites(List<PredictionResult> allFavorites, int count);

}
