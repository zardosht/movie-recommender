package org.movierecommender.controller;

import java.util.ArrayList;
import java.util.List;

import org.movierecommender.controller.prediction.RatingPredictor;
import org.movierecommender.controller.similarity.SimilarityStrategy;
import org.movierecommender.data.Configuration;

public class OptionsFactory {

	// Options
	// simStrat: 2
	// kNeighbors: [10,200]
	// preStrat: 2
	// threshold: [0.0,5.0] step ??

	List<Options> getAllOptions(Configuration config) {
		List<Options> result = new ArrayList<Options>();

		for (SimilarityStrategy simStrat : config.getSimilarityStrategies()) {
			for (int kN = config.getKNStart(); kN < config.getKNEnd(); kN += config.getKNStep()) {
				for (RatingPredictor pred : config.getPredictionStrategies()) {
					for (double favThreshold = config.getFavThresholdStart(); favThreshold < config.getFavThresholdEnd(); favThreshold += config.getFavThresholdStep()) {
						result.add(new Options(simStrat, kN, pred, 10,
								favThreshold));
					}
				}
			}
		}

		return result;
	}

}
