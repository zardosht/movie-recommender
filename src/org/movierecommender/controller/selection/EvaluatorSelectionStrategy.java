package org.movierecommender.controller.selection;

import java.util.List;

import org.movierecommender.controller.prediction.PredictionResult;

public class EvaluatorSelectionStrategy implements SelectionStrategy {

	@Override
	public List<PredictionResult> selectFavorites(
			List<PredictionResult> allFavorites, int count) {
		return allFavorites;
	}

}
